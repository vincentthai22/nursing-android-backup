package com.sevenlogics.babynursing.TableSection;

import android.util.Log;

import com.sevenlogics.babynursing.AppConstants;
import com.sevenlogics.babynursing.Couchbase.Nursing;
import com.sevenlogics.babynursing.Couchbase.UserSettings;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * Created by stevenchan1 on 3/9/17.
 */

public class NursingSummaryTableSection extends NursingBaseTableSection
{
    public float leftBreastCount, rightBreastCount, bothBreastCount;

    private Date startDate, endDate;
    private Integer dayRange;

    public AppConstants.SummaryType summaryType;

    private final static String TAG = "NursingSumTblSection";

    public static NursingSummaryTableSection nursingSummaryTableSection(String title, Date startDate, Date endDate)
    {
        NursingSummaryTableSection tableSection = new NursingSummaryTableSection();

        tableSection.title = title;
        tableSection.startDate = startDate;
        tableSection.endDate = endDate;
        tableSection.amountType = UserSettings.getInstance().volumeMetric;
        tableSection.dataType = UserSettings.getInstance().nursingTrackingSetting.dataType;

        if (null != startDate && null != endDate)
        {
            tableSection.dayRange = Days.daysBetween(new LocalDate(startDate), new LocalDate(endDate)).getDays();
        }

        if (tableSection.dayRange == 0)
        {
            tableSection.dayRange = 1;
        }

        return tableSection;
    }

    public void calculateSummary(ArrayList<NursingDailyTableSection> dailyTableSections, AppConstants.SummaryType summaryType)
    {
        this.summaryType = summaryType;

        leftBreastAvgInSeconds = 0;
        leftBreastTotalInSeconds = 0;
        rightBreastAvgInSeconds = 0;
        rightBreastTotalInSeconds = 0;
        bothBreastAvgInSeconds = 0;
        bothBreastTotalInSeconds = 0;

        leftBreastCount = 0;
        rightBreastCount = 0;
        bothBreastCount = 0;

        float leftBreastAvgAmountTotal = 0, rightBreastAvgAmountTotal = 0, bothBreastAvgAmountTotal = 0;
        float leftBreastAvgDurationTotal = 0, rightBreastAvgDurationTotal = 0, bothBreastAvgDurationTotal = 0;

        for (NursingDailyTableSection dailyTableSection:dailyTableSections)
        {
            if (dailyTableSection != null && dailyTableSection.getClass() == NursingDailyTableSection.class)
            {
                dailyTableSection.calculateDailySummary();

                leftBreastTotalInSeconds += dailyTableSection.leftBreastTotalInSeconds;
                rightBreastTotalInSeconds += dailyTableSection.rightBreastTotalInSeconds;
                bothBreastTotalInSeconds += dailyTableSection.bothBreastTotalInSeconds;

                leftBreastCount += dailyTableSection.leftBreastCount;
                rightBreastCount += dailyTableSection.rightBreastCount;
                bothBreastCount += dailyTableSection.bothBreastCount;

                leftBreastTotalAmount += dailyTableSection.leftBreastTotalAmount;
                rightBreastTotalAmount += dailyTableSection.rightBreastTotalAmount;
                bothBreastTotalAmount += dailyTableSection.bothBreastTotalAmount;

                leftBreastAvgAmountTotal += dailyTableSection.leftBreastAvgAmount;
                rightBreastAvgAmountTotal += dailyTableSection.rightBreastAvgAmount;
                bothBreastAvgAmountTotal += dailyTableSection.bothBreastAvgAmount;

                leftBreastAvgDurationTotal += dailyTableSection.leftBreastAvgInSeconds;
                rightBreastAvgDurationTotal += dailyTableSection.rightBreastAvgInSeconds;
                bothBreastAvgDurationTotal += dailyTableSection.bothBreastAvgInSeconds;

                bottleTotalAmount += dailyTableSection.bottleTotalAmount;
                bottleOnlyGroupCount += dailyTableSection.bottleOnlyGroupCount;

                bothBreastCount += dailyTableSection.bottleOnlyGroupCount;
            }
        }

        Integer numberOfRows = 0;

        if (summaryType == AppConstants.SummaryType.DailyAvg)
        {
            numberOfRows = dailyTableSections.size();

            if (numberOfRows == 0)
                numberOfRows = 1;

            bottleAvgAmount = bottleTotalAmount / numberOfRows;

            leftBreastTotalInSeconds = leftBreastTotalInSeconds / numberOfRows;
            rightBreastTotalInSeconds = rightBreastTotalInSeconds / numberOfRows;
            bothBreastTotalInSeconds = bothBreastTotalInSeconds / numberOfRows;

            leftBreastCount = leftBreastCount / numberOfRows;
            rightBreastCount = rightBreastCount / numberOfRows;
            bothBreastCount = bothBreastCount / numberOfRows;

            leftBreastTotalAmount = leftBreastTotalAmount / numberOfRows;
            rightBreastTotalAmount = rightBreastTotalAmount / numberOfRows;
            bothBreastTotalAmount = bothBreastTotalAmount / numberOfRows;

            leftBreastAvgInSeconds = leftBreastAvgDurationTotal / numberOfRows;
            rightBreastAvgInSeconds = rightBreastAvgDurationTotal / numberOfRows;
            bothBreastAvgInSeconds = bothBreastAvgDurationTotal / numberOfRows;

            leftBreastAvgAmount = leftBreastAvgAmountTotal / numberOfRows;
            rightBreastAvgAmount = rightBreastAvgAmountTotal / numberOfRows;
            bothBreastAvgAmount = bothBreastAvgAmountTotal / numberOfRows;
        }
        else
        {
            leftBreastAvgInSeconds = leftBreastCount == 0 ? 0 : leftBreastTotalInSeconds / leftBreastCount;
            rightBreastAvgInSeconds = rightBreastCount == 0 ? 0 : rightBreastTotalInSeconds / rightBreastCount;
            bothBreastAvgInSeconds = bothBreastCount == 0 ? 0 : bothBreastTotalInSeconds / bothBreastCount;

            leftBreastAvgAmount = leftBreastCount == 0 ? 0 : leftBreastTotalAmount / leftBreastCount;
            rightBreastAvgAmount = rightBreastCount == 0 ? 0 : rightBreastTotalAmount / rightBreastCount;
            bothBreastAvgAmount = bothBreastCount == 0 ? 0 : bothBreastTotalAmount / bothBreastCount;
        }

    }

}
