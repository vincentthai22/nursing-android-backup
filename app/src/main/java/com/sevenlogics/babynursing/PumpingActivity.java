package com.sevenlogics.babynursing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;

import com.sevenlogics.babynursing.Couchbase.PumpingEntry;

import java.util.ArrayList;
import java.util.List;

public class PumpingActivity extends AppCompatActivity {


    private PumpingExpandableListAdapter mExpandableListAdapter;
    private ExpandableListView mExpandableListView;
    private List<PumpingEntry> listData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pumping);

        listData = new ArrayList<>();
        listData.add(new PumpingEntry(2, 0.3f, 0.6f));
        listData.add(new PumpingEntry(2, 0.3f, 0.6f));

        mExpandableListView = (ExpandableListView) findViewById(R.id.expandableListView);
        mExpandableListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                if (groupPosition == 0)
                    return true;
                return false;
            }
        });
        mExpandableListAdapter = new PumpingExpandableListAdapter(this, listData);
        mExpandableListView.setAdapter(mExpandableListAdapter);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_add_pumping) {
            addButtonHandler();
            return true;
        } else if (id == android.R.id.home){
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem item = menu.findItem(R.id.action_settings);
        item.setVisible(false);
        menu.findItem(R.id.action_done).setVisible(false);

        return true;
    }

    public void addButtonHandler(){
        Intent intent = new Intent(this, PumpingDetailActivity.class);
        this.startActivity(intent);
    }

}
