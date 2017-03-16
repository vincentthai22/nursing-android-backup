package com.sevenlogics.babynursing;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;

import com.sevenlogics.babynursing.Couchbase.Baby;
import com.sevenlogics.babynursing.utils.CgBottomSheetDialog;
import com.sevenlogics.babynursing.utils.CgUtils;
import com.sevenlogics.babynursing.utils.PermissionUtils;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.TimePicker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ScrollingActivity extends AppCompatActivity {
    private static final String TAG = ScrollingActivity.class.getSimpleName();
    CollapsingToolbarLayout toolbarLayout;
    Toolbar toolbar;
    TextView txtBirthdate, txtBirthTime, txtGender, txtBloodGroup, txtWeightUnit, txtLengthUnit, txtHeadSizeUnit, txtBirthPlace;
    Calendar cal;
    LinearLayout layoutWeight, layoutLength, layoutHeadSize;
    EditText etxtWeight, etxtLength, etxtHeadSize;
    PopupMenu genderPopupMenu, bloodGroupPopupMenu;
    public static Baby mBaby;
    private final int GIRL = 0, BOY = 1;
    View view;
    BottomSheetDialog cameraBottomSheet    ;
    public static final int PERMISSION_REQUEST_CODE_CAMERA = 1234;
    public static final int PERMISSION_REQUEST_CODE_READ_GALLERY = 2345;
    private Uri imageUri;
    ImageView bannerImg;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        initializeControls();
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String babyId = intent.getStringExtra(MainActivity.INTENT_KEY_BABY_ID);

        mBaby = Baby.modelForId(babyId, Baby.class);

        /** Setting navigation icon (close icon) and onCLick listener*/
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.img_close_btn));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                onBackPressed();
            }
        });

        /**Setting title (baby name) to toolbar layout*/
        toolbarLayout.setTitle(mBaby.name);

        toolbarLayout.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View view)
            {
                AlertDialog.Builder alert = new AlertDialog.Builder(ScrollingActivity.this);

                final EditText edittext = new EditText(ScrollingActivity.this);

                edittext.setText(mBaby.name);
                alert.setTitle("Name");

                alert.setView(edittext);

                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        mBaby.name = edittext.getText().toString();
                        toolbarLayout.setTitle(mBaby.name);
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        // what ever you want to do with No option.
                    }
                });

                alert.show();
            }
        }
        );

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        /**Setting onClickListeners to various buttons and textviews */
        fab.setOnClickListener(clickListener);

        SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy", Locale.getDefault());
        txtBirthdate.setText(dateFormat.format(mBaby.birthday));
        txtBirthdate.setInputType(InputType.TYPE_NULL);
        txtBirthdate.setOnClickListener(clickListener);

        dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
        txtBirthTime.setText(dateFormat.format(mBaby.birthday));
        txtBirthTime.setInputType(InputType.TYPE_NULL);
        txtBirthTime.setOnClickListener(clickListener);

        genderPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (item.toString().equalsIgnoreCase(getResources().getString(R.string.string_girl)))
                {
                    mBaby.isBoy = false;
                }
                else
                {
                    mBaby.isBoy = true;
                }

                txtGender.setText(String.valueOf(item.toString()));
                return false;
            }
        });
        bloodGroupPopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mBaby.bloodType = item.toString();

                txtBloodGroup.setText(String.valueOf(item.toString()));
                return false;
            }
        });

        if (!mBaby.isBoy)
        {
            txtGender.setText(R.string.string_girl);
        }
        else
        {
            txtGender.setText(R.string.string_boy);
        }

        txtGender.setOnClickListener(clickListener);

        txtBloodGroup.setOnClickListener(clickListener);
        txtBloodGroup.setText(mBaby.bloodType);

        txtWeightUnit.setText("lb");
        txtLengthUnit.setText("cm");
        txtHeadSizeUnit.setText("cm");

        txtBirthPlace.setText("Diamond Bar, CA");
    }

    void initializeControls() {
        view = findViewById(R.id.layout_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        txtBirthdate = (TextView) findViewById(R.id.txt_birthdate);
        txtBirthTime = (TextView) findViewById(R.id.txt_birthtime);
        cal = Calendar.getInstance();
        txtBirthdate.setText(CgUtils.dateFormat.format(cal.getTime()));
        txtBirthTime.setText(CgUtils.timeFormat.format(cal.getTime()));
        txtGender = (TextView) findViewById(R.id.txt_gender);
        txtBloodGroup = (TextView) findViewById(R.id.txt_blood_info);

        /** Initializing Popup menus */
        Context wrapper = new ContextThemeWrapper(ScrollingActivity.this, R.style.popupMenuStyle);
        genderPopupMenu = new PopupMenu(wrapper, txtGender, Gravity.LEFT);
        bloodGroupPopupMenu = new PopupMenu(wrapper, txtBloodGroup, Gravity.LEFT);
        for (String string : getResources().getStringArray(R.array.gender_array)) {
            genderPopupMenu.getMenu().add(string);
        }
        for (String string : getResources().getStringArray(R.array.blood_groups_array)) {
            bloodGroupPopupMenu.getMenu().add(string);
        }

        layoutWeight = (LinearLayout) findViewById(R.id.layout_weight);
        layoutLength = (LinearLayout) findViewById(R.id.layout_length);
        layoutHeadSize = (LinearLayout) findViewById(R.id.layout_head_size);

        etxtWeight = (EditText) layoutWeight.findViewById(R.id.layout_edittext);
        txtWeightUnit = (TextView) layoutWeight.findViewById(R.id.layout_textview);

        etxtLength = (EditText) layoutLength.findViewById(R.id.layout_edittext);
        txtLengthUnit = (TextView) layoutLength.findViewById(R.id.layout_textview);

        etxtHeadSize = (EditText) layoutHeadSize.findViewById(R.id.layout_edittext);
        txtHeadSizeUnit = (TextView) layoutHeadSize.findViewById(R.id.layout_textview);

        txtBirthPlace = (TextView) findViewById(R.id.txt_birthplace);

        bannerImg = (ImageView) findViewById(R.id.banner_img);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings)
        {
                mBaby.save();

                ScrollingActivity.this.finish();

        }
        return true;
    }

    /**
     * Below is the listener used for various component's onclick function
     */
    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.txt_birthdate: {

                    cal.setTime(mBaby.birthday);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(ScrollingActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            cal.set(i,i1,i2);
                            Date updatedDate = cal.getTime();

                            mBaby.birthday = updatedDate;

                            SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy", Locale.getDefault());
                            txtBirthdate.setText(dateFormat.format(mBaby.birthday));
                        }
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

                    datePickerDialog.show();
                    break;
                }
                case R.id.txt_birthtime: {
                    cal.setTime(mBaby.birthday);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(ScrollingActivity.this, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int i, int i1) {
                            cal.set(Calendar.HOUR_OF_DAY, i);
                            cal.set(Calendar.MINUTE, i1);

                            Date updatedDate = cal.getTime();

                            mBaby.birthday = updatedDate;

                            SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
                            txtBirthTime.setText(dateFormat.format(mBaby.birthday));
                        }
                    }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);
                    timePickerDialog.show();
                    break;
                }
                case R.id.txt_gender: {
                    genderPopupMenu.show();
                    break;
                }
                case R.id.txt_blood_info: {
                    bloodGroupPopupMenu.show();
                    break;
                }
                case R.id.fab: {
                    showCameraBottomSheet(ScrollingActivity.this);
                    break;
                }
            }
        }
    };

    void showCameraBottomSheet(final Activity activity) {
        /** This method will display the bottomsheet when clicked on camera floating action button*/
        cameraBottomSheet = new CgBottomSheetDialog(activity);
        View view = activity.getLayoutInflater().inflate(R.layout.bottom_sheet_dialog, null);
        /**initializing recyclerview and attaching adapter to it */
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setHasFixedSize(true);

        /** To perform actions on click of item from bottomsheet, an clicklistner is passed along with context so that we can receive the callback here
         */
        recyclerView.setAdapter(new CameraItemAdapter(ScrollingActivity.this, new CameraItemAdapter.ItemListener() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0: {
                        CgUtils.showLog(TAG, "clicked take from camera");

                        /** if condition will check if app has acquired permission to user camera and external storage from user or not
                         * if app has already got the permission, then app will work in normal flow
                         */
                        if (!PermissionUtils.checkCameraPermission(activity)) {
                            PermissionUtils.requestCameraPermission(activity, PERMISSION_REQUEST_CODE_CAMERA);
                        } else {
                            selectCameraImage(CgUtils.REQUEST_CODE_FOR_CAMERA);
                        }
                        break;
                    }
                    case 1: {
                        CgUtils.showLog(TAG, "clicked take from gallery");
                        /** Permission check for read external storage */
                        if (!PermissionUtils.checkReadDataPermission(ScrollingActivity.this)) {
                            PermissionUtils.requestReadDataPermission(ScrollingActivity.this, PERMISSION_REQUEST_CODE_READ_GALLERY);
                        } else {
                            selectLibraryImage(CgUtils.REQUEST_CODE_FOR_CAMERA);
                        }
                        break;
                    }
                }
                if (cameraBottomSheet != null)
                    cameraBottomSheet.dismiss();
            }
        }));

        cameraBottomSheet.setContentView(view);
        cameraBottomSheet.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog d = (BottomSheetDialog) dialogInterface;

                FrameLayout bottomSheet = (FrameLayout) d.findViewById(android.support.design.R.id.design_bottom_sheet);

                // Right here!
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        cameraBottomSheet.show();
        cameraBottomSheet.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                cameraBottomSheet = null;
            }
        });
    }

    public void selectCameraImage(int id) {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        imageUri = getImagePath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT,
                imageUri);

        setImageUri(imageUri);
        startActivityForResult(intent, id);
    }

    public void selectLibraryImage(int id) {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(
                Intent.createChooser(intent, "Select File"), id);
    }

    private Uri getImagePath() {
        File photoFolder = new File(Environment.getExternalStorageDirectory(), "BabyNursing");
        if (!photoFolder.exists()) {
            CgUtils.showLog(TAG, "Create new folder");
            photoFolder.mkdirs();
            CgUtils.showLog(TAG, "Folder " + photoFolder.exists() + " D " + photoFolder.isDirectory());
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(new Date());
        File photo = new File(photoFolder, "Pic_" + timeStamp + ".jpg");
        CgUtils.showLog(TAG, "filePhoto " + photo.getAbsolutePath());
        path = photo.getAbsolutePath();
        //return Uri.fromFile(photo);
        return FileProvider.getUriForFile(ScrollingActivity.this, "com.babynursing.myfileprovider", photo);
    }

    public void setImageUri(Uri uri) {
        CgUtils.showLog(TAG, "setImageUri " + uri.toString());
        imageUri = uri;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** Result from camera or gallery will be received here
         * further tasks will be done according to the request code
         */
        switch (requestCode) {
            case CgUtils.REQUEST_CODE_FOR_CAMERA: {
                if (resultCode == RESULT_OK) {
                    CgUtils.showLog(TAG, "Result recieved from camera or gallery");
                    String cameraUrl = null;
                    if (null != imageUri) {
                        //bannerImg.setImageURI(imageUri);
                        //bannerImg.setImageBitmap(CgUtils.decodeSampledBitmapFromResource(getPath(imageUri),200,200));
                        CgUtils.showLog(TAG, "imageUri: " + Environment.getExternalStorageDirectory() + imageUri.getPath());
                        /** Method used below will scale the picture (if bigger) and set it to the banner imageview */
                        CgUtils.setPic(bannerImg, path);
                        imageUri = null;
                    } else if (data != null && data.getData() instanceof Uri)
                        bannerImg.setImageURI(data.getData());

                    CgUtils.showLog(TAG, "cameraUrl " + cameraUrl);

                    if (!TextUtils.isEmpty(cameraUrl))
                    {
//                        ScrollingActivity.data.uri = cameraUrl;

                        Uri imgUri = Uri.parse(cameraUrl);
                        bannerImg.setImageURI(imgUri);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        CgUtils.showLog(TAG, "Inside onRequestPermissionResult");

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PERMISSION_REQUEST_CODE_CAMERA: {
                    for (int result : grantResults) {
                        if (result != PackageManager.PERMISSION_GRANTED)
                            break;
                    }
                    selectCameraImage(CgUtils.REQUEST_CODE_FOR_CAMERA);
                    break;
                }

                case PERMISSION_REQUEST_CODE_READ_GALLERY: {
                    selectLibraryImage(CgUtils.REQUEST_CODE_FOR_CAMERA);
                    break;
                }
            }
        }
    }
}
