package com.sevenlogics.babynursing;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.sevenlogics.babynursing.Couchbase.Baby;
import com.sevenlogics.babynursing.Couchbase.TrackingSetting;
import com.sevenlogics.babynursing.Couchbase.UserSettings;
import com.sevenlogics.babynursing.TableSection.NursingDailyTableSection;
import com.sevenlogics.babynursing.TableSection.NursingSummaryTableSection;
import com.sevenlogics.babynursing.utils.HeaderListView;

import org.joda.time.DateTime;

import java.util.ArrayList;

/**
 * Created by stevenchan1 on 2/23/17.
 */

public class NursingActivity extends AppCompatActivity
{
    Baby mBaby;
    HeaderListView mListView;

    Button mPrevButton;
    Button mNextButton;

    TrackingSetting mNursingSetting;

    Boolean noRecordsForDateRange;
    Boolean alwaysExpandSingleDay;

    private final static String TAG = "NursingActivity";

    ArrayList<Object> tableSections;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nursing);

        mPrevButton = (Button) findViewById(R.id.nursing_toolbar_prev_button);
        mNextButton = (Button) findViewById(R.id.nursing_toolbar_next_button);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_nursing);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String babyId = intent.getStringExtra(MainActivity.INTENT_KEY_BABY_ID);

        mBaby = Baby.modelForId(babyId, Baby.class);

        mListView = (HeaderListView) findViewById(R.id.list_view_nursing);

        final NursingAdapter nursingAdapter = new NursingAdapter();
        nursingAdapter.setup(this);
        mListView.setAdapter(nursingAdapter);

//        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
//            {
//                nursingAdapter.itemTapped(i);
//            }
//        });

        mNursingSetting = UserSettings.getInstance().nursingTrackingSetting;
        mNursingSetting.syncDateSelection();

        changeToolbarNextPrevTitle();
        buildTableSections();

        mPrevButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                mNursingSetting.backDate();
                changeToolbarNextPrevTitle();
                buildTableSections();
            }
        });

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
            Intent intent = new Intent(this,NursingDetailActivity.class);

            intent.putExtra(MainActivity.INTENT_KEY_BABY_ID,mBaby.document.getId());

            startActivity(intent);
        }
        return true;
    }

    public void buildTableSections()
    {
        NursingSummaryTableSection summaryTableSection = NursingSummaryTableSection.nursingSummaryTableSection(mNursingSetting.dateTitle, mNursingSetting.getStartDate(), mNursingSetting.getEndDate());

        ArrayList<NursingDailyTableSection> dailyTableSections = mBaby.nursingDailyTableSections(mNursingSetting.getStartDate(), mNursingSetting.getEndDate(), mNursingSetting.getBreastType(), true);
        ArrayList<Object> nursingTableSections = new ArrayList<>();

        Boolean hasOneDailySection = false;

        if (dailyTableSections.size() > 0)
        {
            noRecordsForDateRange = false;

            summaryTableSection.calculateSummary(dailyTableSections, UserSettings.getInstance().nursingTrackingSetting.getSummaryType());

            nursingTableSections.addAll(dailyTableSections);

            hasOneDailySection = (dailyTableSections.size() == 1);
        }
        else
        {
            noRecordsForDateRange = true;
        }

        alwaysExpandSingleDay = UserSettings.getInstance().nursingTrackingSetting.isOneDayDateRange() && hasOneDailySection;

        if (alwaysExpandSingleDay)
        {
            NursingDailyTableSection dailyTableSection = (NursingDailyTableSection) nursingTableSections.get(0);

            dailyTableSection.lockExpanded();
        }

        nursingTableSections.add(0, summaryTableSection);

        tableSections = nursingTableSections;

        NursingAdapter nursingAdapter = (NursingAdapter) mListView.getListView().getAdapter();

        nursingAdapter.setupData(tableSections);

    }


    public void changeToolbarNextPrevTitle()
    {
        mPrevButton.setText(mNursingSetting.startDateString());
        mNextButton.setText(mNursingSetting.endDateString());
    }

}
