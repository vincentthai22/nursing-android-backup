package com.sevenlogics.babynursing;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.common.api.GoogleApiClient;
import com.sevenlogics.babynursing.Couchbase.Baby;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by stevenchan1 on 1/17/17.
 */

public class BabyDetailActivity extends AppCompatActivity {

    final static String TAG = "BabyDetailActivity";
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private Baby mBaby;
    private EditText mDobEditText;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.baby_detail);

        Intent intent = getIntent();
        String babyId = intent.getStringExtra(MainActivity.INTENT_KEY_BABY_ID);

        mBaby = Baby.modelForId(babyId, Baby.class);

        Toolbar toolbar = (Toolbar) findViewById(R.id.baby_toolbar);
        toolbar.setTitle(mBaby.name);
        setSupportActionBar(toolbar);

        final EditText nameEditText = (EditText) findViewById(R.id.baby_name_edit_text);
        nameEditText.setText(mBaby.name);

        final Baby baby = mBaby;

        nameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b)
                {
                    Log.d(TAG,"Focus changed ");
                    baby.name = nameEditText.getText().toString();

                    //lost focus, try saving
                    baby.save();
                }
            }
        });

        mDobEditText = (EditText) findViewById(R.id.baby_dob_edit_text);
        SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy h:mm a", Locale.getDefault());
        mDobEditText.setText(dateFormat.format(baby.birthday));
        mDobEditText.setInputType(InputType.TYPE_NULL);

        mDobEditText.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                presentBirthdayDialog(view);
            }
        });

        mDobEditText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b)
                {
                    presentBirthdayDialog(view);
                }
            }
        });

        Spinner bloodSpinner = (Spinner) findViewById(R.id.baby_blood_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.blood_type,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        bloodSpinner.setAdapter(adapter);

        for (int i = 0 ; i < adapter.getCount() ; i++)
        {
            if (mBaby.bloodType.equals(adapter.getItem(i)))
            {
                bloodSpinner.setSelection(i);
                Log.d(TAG,"Blood selected:" + i);
                break;
            }
        }

        Log.d(TAG,"What is blood type" + mBaby.bloodType);

        bloodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                String bloodType = (String)adapterView.getAdapter().getItem(i);
                mBaby.bloodType = bloodType;
                mBaby.save();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });


        Spinner genderSpinner = (Spinner) findViewById(R.id.baby_gender_spinner);
        adapter = ArrayAdapter.createFromResource(this, R.array.gender, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genderSpinner.setAdapter(adapter);

        if (mBaby.isBoy)
        {
            genderSpinner.setSelection(1);
        }
        else
        {
            genderSpinner.setSelection(0);
        }

        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l)
            {
                mBaby.isBoy = (0 != i);
                mBaby.save();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView)
            {

            }
        });

    }

    public void presentBirthdayDialog(View view)
    {
        View focusView = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        final Calendar calendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2)
            {
                calendar.set(i,i1,i2);
                Date updatedDate = calendar.getTime();

                mBaby.birthday = updatedDate;
                mBaby.save();

                SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy h:mm a", Locale.getDefault());
                mDobEditText.setText(dateFormat.format(mBaby.birthday));
            }
        };

        Log.d(TAG,"What is birthday " + mBaby.birthday);
        calendar.setTime(mBaby.birthday);

        DatePickerDialog dateDialog = new DatePickerDialog(this,dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE));
        dateDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.baby_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
