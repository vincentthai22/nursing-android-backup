package com.sevenlogics.babynursing.Couchbase;

import android.util.Log;

import com.couchbase.lite.Document;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by stevenchan1 on 3/3/17.
 */

public class UserSettings extends BaseModel
{
    private static UserSettings ourInstance;

    public static UserSettings getInstance()
    {
        if (null == ourInstance)
        {
            ArrayList<UserSettings> userSettings = CouchbaseManager.getInstance().userSettings();

            if (userSettings.size() == 0)
            {

            }
            else
            {
                ourInstance = userSettings.get(0);
            }
        }

        return ourInstance;
    }

    public String userName, userAge, userZip;
    public Boolean obtainLocationOnNewTracking, prefillWithLatest, mapOnRotation;

    public String weightMetric, heightMetric, headsizeMetric, volumeMetric, temperatureMetric;

    public Integer nursingCellIndex, solidsCellIndex, bottleCellIndex, pumpingCellIndex, sizeCellIndex, diaryCellIndex, sleepingCellIndex, diaperCellIndex;
    public Integer photoCellIndex, bathTimeCellIndex, drVisitCellIndex, medicationCellIndex, vaccinationCellIndex, activityCellIndex;

    public Boolean exportAllDateRange;
    public Date exportStartDate, exportEndDate;
    public Boolean exportAllEntries;
    public Boolean exportNursing, exportPumping, exportBottle, exportActivity;
    public Boolean exportDiary, exportSolids, exportSleeping, exportBathTime;
    public Boolean exportDiaper, exportWeight, exportHeight, exportHeadSize;
    public Boolean exportDrVisit, exportMedication, exportVaccination;

    public Boolean hasDiaperInventory;

    public String nursingSetting;

    public TrackingSetting nursingTrackingSetting;

    private static final String TAG = "UserSettings";

    public void setNursingSetting(String nursingSetting)
    {
        this.nursingSetting = nursingSetting;

        this.nursingTrackingSetting = TrackingSetting.modelForId(this.nursingSetting, TrackingSetting.class);

    }




}
