package com.sevenlogics.babynursing.Couchbase;

import android.support.v4.app.NavUtils;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.fasterxml.jackson.core.sym.NameN;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sevenlogics.babynursing.AppConstants;
import com.sevenlogics.babynursing.BabyManager;
import com.sevenlogics.babynursing.TableSection.NursingDailyTableSection;
import com.sevenlogics.babynursing.utils.CgUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import com.sevenlogics.babynursing.AppConstants;

import static com.sevenlogics.babynursing.Couchbase.CouchbaseManager.DOC_TYPE_BABY;

/**
 * Created by stevenchan1 on 1/12/17.
 */

public class Baby extends BaseEntity implements Serializable
{
    public Date birthday;
    public String bloodType;
    public String selectedActivityType;
    public String name;
    public String importVaccination;
    public String tintColor;
    public Number sorting;
    public Boolean isBoy;

    final static String TAG = "Baby";

//    @property (nonatomic, readonly) NSSet *BabyDiaries;
//    @property (nonatomic, readonly) NSSet *BabyNursings;
//    @property (nonatomic, readonly) NSSet *BabySizes;
//    @property (nonatomic, readonly) NSSet *BabyPumpings;
//    @property (nonatomic, readonly) NSSet *BabyBottles;
//    @property (nonatomic, readonly) NSSet *BabyDiapers;
//    @property (nonatomic, readonly) NSSet *BabySleepings;
//    @property (nonatomic, readonly) NSSet *BabySolids;
//    @property (nonatomic, readonly) NSSet *babyBathTimes;
//    @property (nonatomic, readonly) NSSet *babyActivities;
//    @property (nonatomic, readonly) NSSet *babyMedications;
//    @property (nonatomic, readonly) NSSet *babyVaccines;
//    @property (nonatomic, readonly) NSSet *babyDrVisits;
//    @property (nonatomic, readonly) NSSet *babyDoctors;
//    @property (nonatomic, readonly) NSSet *BabyPhotos;
//    @property (nonatomic, readonly) NSSet *babyRecordings;
//
//    @property (nonatomic, readonly) NSString *babyId;
//
//    @property (nonatomic, strong) UIImage *coverPhotoImage;

    @Override
    public String docType()
    {
        return DOC_TYPE_BABY;
    }

    public void save()
    {
        ObjectMapper mObjectMapper = CouchbaseManager.getInstance().getObjectMapper();
        Map<String, Object> updatedProperties = new HashMap<String, Object>();

        updatedProperties.putAll(document.getProperties());
        updatedProperties.putAll(mObjectMapper.convertValue(this,Map.class));

        try
        {
            Log.d(TAG,"What is updated properties" + updatedProperties);

            document.putProperties(updatedProperties);
            Log.d(TAG, "Success put properties");
        }
        catch (CouchbaseLiteException e) {
            Log.e(TAG,"Error putting: " + e);
            e.printStackTrace();
        }
    }



    public ArrayList<Nursing> nursingsRecordsFiltered(Date startDate, Date endDate, Boolean allowBottle)
    {
        AppConstants.BreastType breastType = UserSettings.getInstance().nursingTrackingSetting.getBreastType();

        ArrayList<Nursing> nursings = CouchbaseManager.getInstance().nursings(this, startDate, endDate);

        ArrayList<Nursing> filteredNursings;

        if (breastType == AppConstants.BreastType.BOTH)
        {
            filteredNursings = nursings;
        }
        else
        {
            filteredNursings = new ArrayList<>();

            for (Nursing nursing : nursings)
            {
                if (nursing.leftBreast && breastType == AppConstants.BreastType.LEFT)
                {
                    filteredNursings.add(nursing);
                }
                else if (!nursing.leftBreast && breastType == AppConstants.BreastType.RIGHT)
                {
                    filteredNursings.add(nursing);
                }
            }
        }

        return filteredNursings;
    }

    public ArrayList<NursingDailyTableSection> nursingDailyTableSections(Date startDate, Date endDate, AppConstants.BreastType breastType, Boolean allowBottle)
    {
        ArrayList<NursingDailyTableSection> dailyTableSections = new ArrayList<>();

        ArrayList<Nursing> combinedFilteredRecords = nursingsRecordsFiltered(startDate, endDate, allowBottle);

        ArrayList<Nursing> filteredRecords = new ArrayList<>(combinedFilteredRecords);

        //sort the records
        Collections.sort(filteredRecords, new Comparator<Nursing>()
        {
            @Override
            public int compare(Nursing nursing, Nursing t1)
            {
                return t1.startTime.compareTo(nursing.startTime);
            }
        });

        ArrayList<Object> filteredRecordsAscending = new ArrayList<>();

        for (Nursing nursing:filteredRecords
             ) {
            filteredRecordsAscending.add(0, nursing);
        }

        Integer nursingCount = filteredRecords.size();
        Integer nursingStartIndex = -1;
        Integer nursingEndIndex = -1;
        Integer nursingIndex = 0;


        if (null != startDate)
        {
            for (nursingIndex = 0 ; nursingIndex < nursingCount ; nursingIndex++)
            {
                GeneralTracking record = filteredRecords.get(nursingIndex);

                if (nursingStartIndex < 0 && CgUtils.timeDifference(record.startTime,startDate) >= 0 && record.startTime.before(endDate))
                {
                    nursingStartIndex = nursingIndex;
                }
                else if (record.startTime.before(startDate))
                {
                    break;
                }
            }
        }
        else
        {
            nursingStartIndex = 0;
        }

        if (null != endDate)
        {
            nursingEndIndex = nursingIndex;
        }
        else
        {
            nursingEndIndex = nursingCount;
        }

        if (nursingStartIndex > -1 && nursingStartIndex < nursingEndIndex)
        {
            Nursing nursingRecord = filteredRecords.get(nursingStartIndex);

            Date dateIndex = CgUtils.midnight(nursingRecord.startTime);

            NursingDailyTableSection dailyTableSection = NursingDailyTableSection.nursingDailyTableSection(dateIndex);

            Date emptyDateIndex = null;
            Boolean hasInsertedEmptyDate = false;

            if (UserSettings.getInstance().nursingTrackingSetting.showEmptySlots)
            {
                emptyDateIndex = CgUtils.addUnitToDate(dateIndex, Calendar.DATE, 1);
                emptyDateIndex = CgUtils.addUnitToDate(emptyDateIndex, Calendar.SECOND, AppConstants.EMPTY_TIME_SLOT_INTERVAL_IN_SECONDS * -1);

                for (; emptyDateIndex.after(nursingRecord.startTime) ; emptyDateIndex = CgUtils.addUnitToDate(emptyDateIndex, Calendar.SECOND, AppConstants.EMPTY_TIME_SLOT_INTERVAL_IN_SECONDS * -1))
                {
                    dailyTableSection.addEmptySlotRecord(emptyDateIndex);
                    hasInsertedEmptyDate = true;
                }
            }

            dailyTableSection.addNursingRecord(nursingRecord);
            hasInsertedEmptyDate = false;

            dailyTableSections.add(dailyTableSection);

            Nursing prevNursingRecord = (Nursing)nursingRecord;

            for (Integer i = nursingStartIndex + 1 ; i < nursingEndIndex ; i++)
            {
                nursingRecord = filteredRecords.get(i);

                Date currentDate = CgUtils.midnight(((Nursing) nursingRecord).startTime);

                if (currentDate.before(dateIndex))
                {
                    if (UserSettings.getInstance().nursingTrackingSetting.showEmptySlots)
                    {
                        for (; CgUtils.timeDifference(emptyDateIndex, dateIndex) >= 0 ; emptyDateIndex = CgUtils.addUnitToDate(emptyDateIndex, Calendar.SECOND, AppConstants.EMPTY_TIME_SLOT_INTERVAL_IN_SECONDS * -1))
                        {
                            dailyTableSection.addEmptySlotRecord(emptyDateIndex);
                            hasInsertedEmptyDate = true;
                        }
                    }

                    dateIndex = currentDate;

                    emptyDateIndex = CgUtils.addUnitToDate(dateIndex, Calendar.DATE, 1);
                    emptyDateIndex = CgUtils.addUnitToDate(emptyDateIndex, Calendar.SECOND, AppConstants.EMPTY_TIME_SLOT_INTERVAL_IN_SECONDS * -1);

                    dailyTableSection = NursingDailyTableSection.nursingDailyTableSection(dateIndex);
                    dailyTableSections.add(dailyTableSection);
                }
                else
                {
                    if (prevNursingRecord.startTime.equals(emptyDateIndex))
                    {
                        emptyDateIndex = CgUtils.addUnitToDate(emptyDateIndex, Calendar.SECOND, AppConstants.EMPTY_TIME_SLOT_INTERVAL_IN_SECONDS * -1);
                    }
                }

                if (UserSettings.getInstance().nursingTrackingSetting.showEmptySlots)
                {
                    for (; emptyDateIndex.after(nursingRecord.startTime) ; emptyDateIndex = CgUtils.addUnitToDate(emptyDateIndex, Calendar.SECOND, AppConstants.EMPTY_TIME_SLOT_INTERVAL_IN_SECONDS * -1))
                    {
                        if (BabyManager.getInstance().isItem(filteredRecords.size() - i - 1, AppConstants.TrackingType.TrackingTypeNursing, filteredRecordsAscending))
                        {
                            dailyTableSection.addEmptySlotRecord(emptyDateIndex);
                            hasInsertedEmptyDate = true;
                        }

                    }
                }

                dailyTableSection.addNursingRecord(nursingRecord);
                hasInsertedEmptyDate = false;

                prevNursingRecord = nursingRecord;
                Log.d(TAG, "What is loop data now: " + dailyTableSection.sectionData);
            }

//            Log.d(TAG, "What is data now: " + dailyTableSection.sectionData);

            if (UserSettings.getInstance().nursingTrackingSetting.showEmptySlots)
            {
                for (; emptyDateIndex.after(dateIndex) ; emptyDateIndex = CgUtils.addUnitToDate(emptyDateIndex, Calendar.SECOND, AppConstants.EMPTY_TIME_SLOT_INTERVAL_IN_SECONDS * -1))
                {
                    dailyTableSection.addEmptySlotRecord(emptyDateIndex);
                    hasInsertedEmptyDate = true;
                }
            }

            prevNursingRecord = null;
        }

        return dailyTableSections;
    }

}
