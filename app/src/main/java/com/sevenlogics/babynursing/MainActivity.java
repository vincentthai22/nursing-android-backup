package com.sevenlogics.babynursing;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sevenlogics.babynursing.Couchbase.Baby;
import com.sevenlogics.babynursing.Couchbase.CouchbaseManager;
import com.sevenlogics.babynursing.Couchbase.Nursing;
import com.sevenlogics.babynursing.Couchbase.UserSettings;

import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private ListView mMainListView;

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String INTENT_KEY_BABY_ID = "INTENT_KEY_BABY_ID";

    private ArrayList<Baby> mAllBabies;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initiate CouchbaseManager
        CouchbaseManager.getInstance().setup(getApplicationContext());

//        CouchbaseManager.getInstance().startPull(true);
//        CouchbaseManager.getInstance().allDocumentTesting();

        mAllBabies = CouchbaseManager.getInstance().allBabies();

        Log.d(TAG,"Baby Count: " + mAllBabies.size());

        final Context context = this;
        final Baby baby = mAllBabies.get(0);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mMainListView = (ListView)findViewById(R.id.main_list_view);

        final TrackingModuleAdapter adapter = new TrackingModuleAdapter(this,baby);
        mMainListView.setAdapter(adapter);

        mMainListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
             {
                 @Override
                 public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                     TrackingModule trackingModule = (TrackingModule) adapter.getItem(position);

                     Intent detailIntent;

                     if (trackingModule.title.equals("Nursing"))
                     {
                         detailIntent = new Intent(context, NursingActivity.class);
                     }
                     else
                     {
                         detailIntent = new Intent(context, ScrollingActivity.class);
                     }

                     if (detailIntent != null)
                     {
                         detailIntent.putExtra(INTENT_KEY_BABY_ID,baby.document.getId());
                         startActivity(detailIntent);
                     }

                 }
             }
        );

    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
//        int id = item.getItemId();
//
//        if (id == R.id.nav_camera) {
//            // Handle the camera action
//        } else if (id == R.id.nav_gallery) {
//
//        } else if (id == R.id.nav_slideshow) {
//
//        } else if (id == R.id.nav_manage) {
//
//        } else if (id == R.id.nav_share) {
//
//        } else if (id == R.id.nav_send) {
//
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
