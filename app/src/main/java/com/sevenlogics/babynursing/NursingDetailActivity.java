package com.sevenlogics.babynursing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.sevenlogics.babynursing.Couchbase.Baby;
import com.sevenlogics.babynursing.Couchbase.CouchbaseManager;
import com.sevenlogics.babynursing.Couchbase.Nursing;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by stevenchan1 on 2/24/17.
 */

public class NursingDetailActivity extends AppCompatActivity {

    Baby mBaby;
    Nursing mNursing;

    TextView mStartDateTextView;
    TextView mStartTimeTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursing_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_nursing_detail);
        setSupportActionBar(toolbar);

        mStartDateTextView = (TextView) findViewById(R.id.nursing_detail_start_date_textview);
        mStartTimeTextView = (TextView) findViewById(R.id.nursing_detail_start_time_textview);

        Intent intent = getIntent();
        String babyId = intent.getStringExtra(MainActivity.INTENT_KEY_BABY_ID);

        String nursingId = intent.getStringExtra(MainActivity.INTENT_KEY_NURSING_ID);

        if (null != nursingId)
        {
            mNursing = Nursing.modelForId(nursingId, Nursing.class);
        }

        if (null != mNursing)
        {
            setTitle("Edit Nursing");

            SimpleDateFormat dateFormat = new SimpleDateFormat("M/dd/yyyy", Locale.getDefault());
            mStartDateTextView.setText(dateFormat.format(mBaby.birthday));
            mStartDateTextView.setInputType(InputType.TYPE_NULL);
//            mStartDateTextView.setOnClickListener(clickListener);

            dateFormat = new SimpleDateFormat("h:mm a", Locale.getDefault());
            mStartTimeTextView.setText(dateFormat.format(mBaby.birthday));
            mStartTimeTextView.setInputType(InputType.TYPE_NULL);
//            mStartTimeTextView.setOnClickListener(clickListener);
        }
        else
        {
            setTitle("New Nursing");
        }

        mBaby = Baby.modelForId(babyId, Baby.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_module, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (item.getItemId() == R.id.action_module_add)
        {
            //validate data
            if (null == mNursing)
            {
                //new nursing
                mNursing = CouchbaseManager.getInstance().insertNursing(mBaby);
            }
            else
            {
                //existing nursing
            }



            NursingDetailActivity.this.finish();
        }
        return true;
    }
}
