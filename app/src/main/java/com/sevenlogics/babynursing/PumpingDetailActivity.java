package com.sevenlogics.babynursing;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sevenlogics.babynursing.utils.CgBottomSheetDialog;
import com.sevenlogics.babynursing.utils.CgUtils;
import com.sevenlogics.babynursing.utils.PermissionUtils;
import com.sevenlogics.babynursing.utils.PumpingAlertDialogFragment;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Created by vincent on 3/21/17.
 */

public class PumpingDetailActivity extends AppCompatActivity {

    private static final String TAG = PumpingDetailActivity.class.getSimpleName();
    static final int REQUEST_CODE_FOR_NOTE = 2;
    private TextView mStartDetailTextView, mEndDetailTextView, mDurationDetailTextView,
            mAmountPumpedDetailTextView, mleftBreastTextView, mRightBreastTextView;
    private BottomSheetDialog cameraBottomSheet;
    private Uri imageUri;
    private String path;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private PumpingAlertDialogFragment dialogFragment;
    private FragmentManager fm;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pumping_detail);

        mStartDetailTextView = (TextView) findViewById(R.id.startDetailTextView);
        mEndDetailTextView = (TextView) findViewById(R.id.endDetailTextView);
        mDurationDetailTextView = (TextView) findViewById(R.id.durationDetailTextView);
        mAmountPumpedDetailTextView = (TextView) findViewById(R.id.amountPumpedDetailTextView);
        mleftBreastTextView = (TextView) findViewById(R.id.leftBreastDetailTextView);
        mRightBreastTextView = (TextView) findViewById(R.id.rightBreastDetailedTextView);

        mStartDetailTextView.setText(new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(new Date()));
        mEndDetailTextView.setText(new SimpleDateFormat("MM/dd/yyyy hh:mm a").format(new Date()));

        float leftBreast, rightBreast;
        leftBreast = Float.parseFloat(mleftBreastTextView.getText().toString().split(" ")[0]);
        rightBreast = Float.parseFloat(mRightBreastTextView.getText().toString().split(" ")[0]);
        DecimalFormat df = new DecimalFormat("#.#");
        df.setRoundingMode(RoundingMode.CEILING);
        mAmountPumpedDetailTextView.setText( df.format(leftBreast + rightBreast) + " ozs");
        builder = new AlertDialog.Builder(this);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setupAmountPumpedDialogs();


    }

    public void setupAmountPumpedDialogs(){

        fm = getSupportFragmentManager();
    }

    public void onClickHandler(View v){



        switch(v.getId()) {
            case R.id.amountPumpedTextView:
            case R.id.amountPumpedDetailTextView:
            case R.id.amountPumpedRelativeLayout:
                dialogFragment = PumpingAlertDialogFragment.newInstance("Enter left and/or right amount",
                        "Please enter the amount pumped by entering the individual left or right breast amount");
                dialogFragment.show(fm, "alert_dialog");
                Log.d("click", "show number picker");
                break;
            case R.id.leftBreastTextView:
            case R.id.leftBreastDetailTextView:
            case R.id.leftBreastRelativeLayout:

                Log.d("click", "show number picker");
                break;
            case R.id.rightBreastTextView:
            case R.id.rightBreastDetailedTextView:
            case R.id.rightBreastRelativeLayout:
                Log.d("click", "show number picker");
                break;
            case R.id.startTextView:
            case R.id.startDetailTextView:
            case R.id.startRelativeLayout:
                Log.d("click", "show number picker");
                break;
            case R.id.endTextView:
            case R.id.endDetailTextView:
            case R.id.endRelativeLayout:
                Log.d("click", "show number picker");
                break;
            case R.id.durationTextView:
            case R.id.durationDetailTextView:
            case R.id.durationRelativeLayout:
                Log.d("click", "show number picker");
                break;
            case R.id.locationTextView:
            case R.id.locationDetailTextView:
            case R.id.locationRelativeLayout:
                Log.d("click", "show number picker");
                break;
            case R.id.notesTextView:
            case R.id.notesDetailTextView:
            case R.id.notesRelativeLayout:
                Log.d("click", "show number picker");
                Intent intent = new Intent(this, PumpingNoteActivity.class);
                startActivityForResult(intent, REQUEST_CODE_FOR_NOTE);
                break;
            case R.id.addTextView:
            case R.id.addDetailTextView:
            case R.id.addRelativeLayout:
                showCameraBottomSheet(this);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        String s = ((SpannableString) data.getExtras().get(PumpingNoteActivity.NOTE_KEY)).toString();
        if (s.length() > 0){
            TextView notesTextView = (TextView) findViewById(R.id.notesDetailTextView);
            notesTextView.setText(s);
        }

    }

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
        recyclerView.setAdapter(new CameraItemAdapter(PumpingDetailActivity.this, new CameraItemAdapter.ItemListener() {
            @Override
            public void onItemClick(int position) {
                switch (position) {
                    case 0: {
                        CgUtils.showLog(TAG, "clicked take from camera");

                        /** if condition will check if app has acquired permission to user camera and external storage from user or not
                         * if app has already got the permission, then app will work in normal flow
                         */
                        if (!PermissionUtils.checkCameraPermission(activity)) {
                            PermissionUtils.requestCameraPermission(activity, ScrollingActivity.PERMISSION_REQUEST_CODE_CAMERA);
                        } else {
                            selectCameraImage(CgUtils.REQUEST_CODE_FOR_CAMERA);
                        }
                        break;
                    }
                    case 1: {
                        CgUtils.showLog(TAG, "clicked take from gallery");
                        /** Permission check for read external storage */
                        if (!PermissionUtils.checkReadDataPermission(PumpingDetailActivity.this)) {
                            PermissionUtils.requestReadDataPermission(PumpingDetailActivity.this, ScrollingActivity.PERMISSION_REQUEST_CODE_READ_GALLERY);
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
    public void setImageUri(Uri uri) {
        CgUtils.showLog(TAG, "setImageUri " + uri.toString());
        imageUri = uri;
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
        return FileProvider.getUriForFile(PumpingDetailActivity.this, "com.babynursing.myfileprovider", photo);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            case R.id.action_done:
                return true;
            default:
                break;
        }
        return true;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        menu.findItem(R.id.action_add_pumping).setVisible(false);


        return true;
    }
}

