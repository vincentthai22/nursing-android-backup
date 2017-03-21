package com.sevenlogics.babynursing.TableSection;

import android.util.Log;

import com.sevenlogics.babynursing.AppConstants;
import com.sevenlogics.babynursing.BabyManager;
import com.sevenlogics.babynursing.Couchbase.Bottle;
import com.sevenlogics.babynursing.Couchbase.GeneralTracking;
import com.sevenlogics.babynursing.Couchbase.UserSettings;
import com.sevenlogics.babynursing.EmptySlotData;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import com.sevenlogics.babynursing.Couchbase.Nursing;
import com.sevenlogics.babynursing.UnitConversionUtil;

/**
 * Created by stevenchan1 on 3/8/17.
 */

public class NursingDailyTableSection extends NursingBaseTableSection
{
    public Date date;
    public Boolean expanded = false;

    Integer sectionIndex;
    ArrayList<Nursing> leftBreastNursingRecords = new ArrayList<>();
    ArrayList<Nursing> rightBreastNursingRecords = new ArrayList<>();

    private final static String TAG = "NursingDailyTblSection";

    public float leftBreastCount, rightBreastCount, bothBreastCount;

    public static NursingDailyTableSection nursingDailyTableSection(Date date)
    {
        NursingDailyTableSection tableSection = new NursingDailyTableSection();

        tableSection.date = date;
        tableSection.amountType = UserSettings.getInstance().volumeMetric;
        tableSection.dataType = UserSettings.getInstance().nursingTrackingSetting.dataType;
        tableSection.breastType = UserSettings.getInstance().nursingTrackingSetting.getBreastType();

        return tableSection;
    }

    public void addEmptySlotRecord(Date date)
    {
        if (null != date)
        {
            sectionData.add(0, EmptySlotData.emptySlotData(date));
        }
    }

    public void addNursingRecord(GeneralTracking generalTracking)
    {
        if (generalTracking.getClass() == Nursing.class)
        {
            Nursing nursing = (Nursing)generalTracking;
            sectionData.add(0, nursing);

            if (nursing.leftBreast)
            {
                leftBreastNursingRecords.add(0, nursing);
            }
            else
            {
                rightBreastNursingRecords.add(0, nursing);
            }
        }
        else if (generalTracking.getClass() == Bottle.class)
        {
            sectionData.add(0, generalTracking);
        }
    }

    public void calculateDailySummary()
    {
        leftBreastAvgInSeconds = 0;
        leftBreastTotalInSeconds = 0;
        rightBreastAvgInSeconds = 0;
        rightBreastTotalInSeconds = 0;
        bothBreastAvgInSeconds = 0;
        bothBreastTotalInSeconds = 0;

        leftBreastCount = 0;
        rightBreastCount = 0;
        bothBreastCount = 0;

        leftBreastAvgAmount = 0;
        leftBreastTotalAmount = 0;
        rightBreastAvgAmount = 0;
        rightBreastTotalAmount = 0;
        bothBreastAvgAmount = 0;
        bothBreastTotalAmount = 0;

        bottleAvgAmount = 0;
        bottleTotalAmount = 0;
        bottleCount = 0;
        bottleOnlyGroupCount = 0;

        Boolean wasEmptySlot = true;
        Boolean groupHasLeftBreast = false;
        Boolean groupHasRightBreast = false;
        Boolean groupHasBottle = false;

        Integer count = sectionData.size();

        Log.d(TAG, "What is section data: " + sectionData);

        for (Integer i = 0 ; i < count ; i++)
        {
            Object record = sectionData.get(i);

            if (record.getClass().getSuperclass() == GeneralTracking.class)
            {
                if (record.getClass() == Nursing.class)
                {
                    Nursing nursingRecord = (Nursing)record;

                    Integer nursingTimeInterval = nursingRecord.duration.intValue();

                    float amountFedInOz = 0;

                    if (nursingRecord.amountFedInOz != null)
                    {
                        amountFedInOz = nursingRecord.amountFedInOz.floatValue();
                    }
                    else
                    {
                        amountFedInOz = nursingTimeInterval * UserSettings.getInstance().nursingTrackingSetting.ozPerMinute / 60;
                    }

                    if (nursingRecord.leftBreast)
                    {
                        leftBreastTotalInSeconds += nursingTimeInterval;
                        leftBreastTotalAmount += amountFedInOz;

                        groupHasLeftBreast = true;
                    }
                    else
                    {
                        rightBreastTotalInSeconds += nursingTimeInterval;
                        rightBreastTotalAmount += amountFedInOz;

                        groupHasRightBreast = true;
                    }

                    bothBreastTotalInSeconds += nursingTimeInterval;
                    bothBreastTotalAmount += amountFedInOz;
                }
                else if (record.getClass() == Bottle.class)
                {
                    Bottle bottleRecord = (Bottle)record;

                    groupHasBottle = true;

                    bottleTotalAmount += bottleRecord.amountFedInOz.floatValue();
                }

                if (BabyManager.getInstance().isItem(i, AppConstants.TrackingType.TrackingTypeNursing, sectionData))
                {
                    if (groupHasLeftBreast || groupHasRightBreast)
                    {
                        bothBreastCount++;

                        if (groupHasLeftBreast)
                        {
                            leftBreastCount++;
                        }

                        if (groupHasRightBreast)
                        {
                            rightBreastCount++;
                        }
                    }

                    if (groupHasBottle)
                    {
                        bottleCount++;
                    }

                    if (groupHasBottle && !groupHasLeftBreast && !groupHasRightBreast)
                    {
                        bottleOnlyGroupCount++;
                    }

                    groupHasLeftBreast = false;
                    groupHasRightBreast = false;
                    groupHasBottle = false;
                }
                else
                {
                    wasEmptySlot = true;
                }
            }
            else
            {
                wasEmptySlot = true;
            }
        }

        if (leftBreastCount > 0)
        {
            leftBreastAvgInSeconds = leftBreastTotalInSeconds / leftBreastCount;
            leftBreastAvgAmount = leftBreastTotalAmount / leftBreastCount;
        }

        if (rightBreastCount > 0)
        {
            rightBreastAvgInSeconds = rightBreastTotalInSeconds / rightBreastCount;
            rightBreastAvgAmount = rightBreastTotalAmount / rightBreastCount;
        }

        if (bothBreastCount > 0)
        {
            bothBreastAvgInSeconds = bothBreastTotalInSeconds / bothBreastCount;
            bothBreastAvgAmount = bothBreastTotalAmount / bothBreastCount;
        }

        if (bottleCount > 0)
        {
            bottleAvgAmount = bottleTotalAmount / bottleCount;
        }

        if (amountType.equals(AppConstants.VOLUME_TYPE_ML))
        {
            leftBreastAvgAmount = UnitConversionUtil.convertToMlFromOz(leftBreastAvgAmount);
            leftBreastTotalAmount = UnitConversionUtil.convertToMlFromOz(leftBreastTotalAmount);

            rightBreastAvgAmount = UnitConversionUtil.convertToMlFromOz(rightBreastAvgAmount);
            rightBreastTotalAmount = UnitConversionUtil.convertToMlFromOz(rightBreastTotalAmount);

            bothBreastAvgAmount = UnitConversionUtil.convertToMlFromOz(bothBreastAvgAmount);
            bothBreastTotalAmount = UnitConversionUtil.convertToMlFromOz(bothBreastTotalAmount);

            bottleAvgAmount = UnitConversionUtil.convertToMlFromOz(bottleAvgAmount);
            bottleTotalAmount = UnitConversionUtil.convertToMlFromOz(bottleTotalAmount);
        }
    }

    public void lockExpanded()
    {
        expanded = true;


    }


}
