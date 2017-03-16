package com.sevenlogics.babynursing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.sevenlogics.babynursing.Couchbase.Baby;
import com.sevenlogics.babynursing.Couchbase.CouchbaseManager;
import com.sevenlogics.babynursing.Couchbase.Nursing;

/**
 * Created by stevenchan1 on 2/24/17.
 */

public class NursingDetailActivity extends AppCompatActivity {

    Baby mBaby;
    Nursing nursing;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursing_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_nursing_detail);
        setSupportActionBar(toolbar);

        setTitle("New Nursing");

        Intent intent = getIntent();
        String babyId = intent.getStringExtra(MainActivity.INTENT_KEY_BABY_ID);

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
            if (null == nursing)
            {
                //new nursing
                nursing = CouchbaseManager.getInstance().insertNursing(mBaby);


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
