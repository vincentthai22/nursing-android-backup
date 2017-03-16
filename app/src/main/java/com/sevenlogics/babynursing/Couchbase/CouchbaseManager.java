package com.sevenlogics.babynursing.Couchbase;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.couchbase.lite.Database;
import com.couchbase.lite.DatabaseOptions;
import com.couchbase.lite.Document;
import com.couchbase.lite.Emitter;
import com.couchbase.lite.Manager;
import com.couchbase.lite.Mapper;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.ReplicationFilter;
import com.couchbase.lite.View;
import com.couchbase.lite.android.AndroidContext;
import com.couchbase.lite.auth.Authenticator;
import com.couchbase.lite.auth.AuthenticatorFactory;
import com.couchbase.lite.replicator.Replication;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.std.StdArraySerializers;
import com.sevenlogics.babynursing.MainActivity;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by stevenchan1 on 1/12/17.
 */
public class CouchbaseManager implements Replication.ChangeListener
{
    private static CouchbaseManager ourInstance = new CouchbaseManager();

    public static CouchbaseManager getInstance() {
        return ourInstance;
    }

    public static final String DOC_TYPE_BABY = "DOC_TYPE_BABY";
    public static final String DOC_TYPE_NURSING = "DOC_TYPE_NURSING";
    public static final String DOC_TYPE_USER_SETTINGS = "DOC_TYPE_USER_SETTINGS";

    private static final String DB_NAME = "baby-nursing-cb";
    private static final String TAG = "CouchbaseEvents";

    // Storage Type: .SQLITE_STORAGE or .FORESTDB_STORAGE
    private static final String STORAGE_TYPE = Manager.SQLITE_STORAGE;

    //DEV
    private static final String SYNC_URL_HTTP = "http://104.198.101.120:4984/baby-nursing/";
    private static final String SYNC_USERNAME = "nursingmobile1";
    private static final String SYNC_PASSWORD = "SlBn2016";

    private Manager mManager;
    private Database mDatabase;
    private Replication mPull;
    private Replication mPush;
    private Authenticator mAuthenticator;

    private ObjectMapper mObjectMapper;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
    private ArrayList<String> mChannelIds;

//    private Throwable mReplError;
//    private String mCurrentUserId;

    public ObjectMapper getObjectMapper()
    {
        return mObjectMapper;
    }

    private URL getSyncURL()
    {
        URL url = null;

        try
        {
            url = new URL(SYNC_URL_HTTP);
        }
        catch (Exception e)
        {
            Log.e(TAG, "Invalid sync url", e);
        }

        return url;
    }

    public int documentCount()
    {
        return this.mDatabase.getDocumentCount();
    }

    public void setup(Context context)
    {
        try
        {
            mObjectMapper = new ObjectMapper();
            mObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mObjectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);

            mChannelIds = new ArrayList<>();
            //TESTING

//            mChannelIds.add("8ac9QKJZgIe3BfFrLGJ4qSnTm553");
            mChannelIds.add("TBykAXATdPPXQGksJib4rp2u8Ck2");

            mManager = new Manager(new AndroidContext(context),Manager.DEFAULT_OPTIONS);

            DatabaseOptions options = new DatabaseOptions();
            options.setCreate(true);
            options.setStorageType(STORAGE_TYPE);

            mDatabase = mManager.openDatabase(DB_NAME, options);

            mAuthenticator = AuthenticatorFactory.createBasicAuthenticator(SYNC_USERNAME, SYNC_PASSWORD);
        }
        catch (Exception e)
        {
            Log.e(TAG,"Error getting database",e);
            return;
        }
    }

    private CouchbaseManager()
    {
        Log.d("CouchbaseManager","Constructor");

    }

    public void startPull(Boolean continuous)
    {
        if (null == mManager || null == mDatabase
                || null == mChannelIds || mChannelIds.size() < 1
                || null == mAuthenticator)
        {
            Log.e(TAG,"Cannot start pull");
            return;
        }

        if (null == mPull)
        {
            mPull = mDatabase.createPullReplication(getSyncURL());
            mPull.addChangeListener(this);
        }

        mPull.setContinuous(continuous);
        mPull.setChannels(mChannelIds);
        mPull.setAuthenticator(mAuthenticator);

        mPull.stop();
        mPull.start();
    }

    public Document documentWithId(String documentId)
    {
        return mDatabase.getDocument(documentId);
    }

    private String convertToDateString(Date date)
    {
        String dateString = dateFormat.format(date) + "Z";

        return dateString;
    }

    public ArrayList<UserSettings> userSettings()
    {
        Query query = this.userSettingsQuery();

        ArrayList<UserSettings> userSettings = new ArrayList<>();

        try
        {
            QueryEnumerator enumerator = query.run();

            while (enumerator.hasNext())
            {
                QueryRow queryRow = enumerator.next();

                Document document = queryRow.getDocument();

                UserSettings userSetting = UserSettings.modelForDocument(document,UserSettings.class);

                userSettings.add(userSetting);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "What is UserSettings exception: " + e);
        }

        return userSettings;
    }

    public ArrayList<Nursing> nursings(Baby baby, Date startDate, Date endDate)
    {
        Query query = this.nursingsQuery();

        ArrayList<Nursing> nursings = new ArrayList<>();

        try
        {
            String startDateKey = convertToDateString(startDate);
            String endDateKey = convertToDateString(endDate);

            Log.d(TAG,"What is startDateKey " + startDateKey);
            Log.d(TAG,"What is endDateKey " + endDateKey);

            query.setStartKey(new Object[]{baby.document.getId(),startDateKey});
            query.setEndKey(new Object[]{baby.document.getId(),endDateKey});

            QueryEnumerator enumerator = query.run();

//            Log.d(TAG, "What is count: " + enumerator.getCount());

            while (enumerator.hasNext())
            {
                QueryRow queryRow = enumerator.next();

                Document document = queryRow.getDocument();

                Nursing nursing = Nursing.modelForDocument(document,Nursing.class);

                nursings.add(nursing);
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "What is exception " + e);
        }

        return nursings;
    }

    public ArrayList<Baby> allBabies()
    {
        Query query = this.babiesQuery();

        ArrayList<Baby> babies = new ArrayList<>();

        try
        {
            QueryEnumerator babiesEnumerator = query.run();

            Log.d(TAG,"How many babies: " + babiesEnumerator.getCount());

            while (babiesEnumerator.hasNext())
            {
                QueryRow queryRow = babiesEnumerator.next();

                Document document = queryRow.getDocument();

                Log.d(TAG, "What is baby document: " + document);

                Baby baby = Baby.modelForDocument(document,Baby.class);

                babies.add(baby);
            }

            Log.d(TAG,"Done getting babies " + babies.size());
        }
        catch (Exception e)
        {
            Log.e(TAG,"Exception getting baby " + e.getLocalizedMessage());
        }

        return babies;
    }

    public void allDocumentTesting()
    {
        Query query = this.mDatabase.createAllDocumentsQuery();

        try
        {
            QueryEnumerator enumerator = query.run();

            Log.d(TAG, "Doc count " + enumerator.getCount());

            while (enumerator.hasNext())
            {
                QueryRow row = enumerator.next();

                String type = (String)row.getDocument().getProperties().get("type");

                if (type.equals(DOC_TYPE_NURSING))
                {
                    Log.d(TAG, "Doc properties: " + row.getDocument().getProperties());
                }
//                Log.d(TAG, "What is doc type: " + type);

            }

        }
        catch (Exception e)
        {

        }
    }


    public Query babiesQuery()
    {
        View view = this.mDatabase.getView("Babies");

        if (view.getMap() == null)
        {
            Mapper mapper = new Mapper()
            {
                public void map(Map<String, Object> document, Emitter emitter)
                {
                    String type = (String)document.get("type");

                    if (DOC_TYPE_BABY.equals(type))
                    {
                        emitter.emit(type,document.get("documentID"));
                    }
                }
            };

            view.setMap(mapper,"5");
        }

        return view.createQuery();
    }

    public Query userSettingsQuery()
    {
        View view = this.mDatabase.getView("UserSettings");

        if (view.getMap() == null)
        {
            Mapper mapper = new Mapper()
            {
                @Override
                public void map(Map<String, Object> document, Emitter emitter)
                {
                    String type = (String)document.get("type");

                    if (DOC_TYPE_USER_SETTINGS.equals(type))
                    {
                        emitter.emit(type,document.get("documentID"));
                    }
                }
            };

            view.setMap(mapper, "1");
        }

        return view.createQuery();
    }

    public Query nursingsQuery()
    {
        View view = this.mDatabase.getView("Nursings");

        if (view.getMap() == null)
        {
            Mapper mapper = new Mapper()
            {
                public void map(Map<String, Object> document, Emitter emitter)
                {
                    String type = (String)document.get("type");

                    if (DOC_TYPE_NURSING.equals(type))
                    {
                        Boolean isLive = false;

                        if (null != document.get("liveNursing"))
                        {
                            Object isLiveObject = document.get("liveNursing");

                            if (isLiveObject.getClass() == Boolean.class)
                            {
                                isLive = (Boolean)isLiveObject;
                            }
                            else if (isLiveObject.getClass() == Integer.class)
                            {
                                isLive = (((Integer)isLiveObject) == 1);
                            }
                        }

                        if (!isLive)
                        {
                            Log.d(TAG,"Query What is start time" + document.get("startTime"));

                            Object[] keys = new Object[]{document.get("nursingBaby"),document.get("startTime")};

                            emitter.emit(keys,document.get("documentID"));

                        }
                    }
                }
            };

            view.setMap(mapper,"7");
        }

        return view.createQuery();
    }



    public Nursing insertNursing(Baby baby)
    {
        Document document = mDatabase.createDocument();
        Nursing nursing = Nursing.modelForDocument(document, Nursing.class);

        nursing.createdTS = new Date();
        nursing.nursingBaby = baby.document.getId();
        nursing.cblChannel = mChannelIds;

        return nursing;
    }

    @Override
    public void changed(Replication.ChangeEvent event)
    {
        Log.d(TAG,"Replication Changed: " + mPull.getStatus());
        Throwable error = null;
        if (mPull != null)
        {
            if (error == null)
            {
                error = mPull.getLastError();
            }

            Log.d(TAG, "What is last error " + mPull.getLastError());
        }

//        if (error == null || error == mReplError)
//            error = mPush.getLastError();
//
//        if (error != mReplError) {
//            mReplError = error;
//            if (mReplError != null)
//                showErrorMessage(mReplError.getMessage(), null);
//        }
    }
}
