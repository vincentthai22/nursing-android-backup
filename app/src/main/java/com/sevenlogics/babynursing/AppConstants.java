package com.sevenlogics.babynursing;

/**
 * Created by stevenchan1 on 3/6/17.
 */



public class AppConstants {
    public final static String DATE_SELECTION_TODAY = "Today";
    public final static String DATE_SELECTION_THIS_WEEK = "This Week";
    public final static String DATE_SELECTION_THIS_MONTH = "This Month";

    public final static Integer EMPTY_TIME_SLOT_INTERVAL_IN_SECONDS = 3600;

    public final static String DATE_FORMAT = "MMM d, yyyy";
    public final static String TIME_FORMAT = "";

    public final static Integer TOP_BORDER = 1;
    public final static Integer BOTTOM_BORDER = 2;

    public final static String VOLUME_TYPE_ML = "VOLUME_TYPE_ML";

    public final static Integer DAY_IN_SECONDS = 86400;

    public final static String DAILY_AVG_SUMMARY = "(daily averages)";
    public final static String CUMULATIVE_SUMMARY = "(cumulative)";

    public final static String ML_AVG = "ML_AVG";
    public final static String ML_TOTAL = "ML_TOTAL";
    public final static String OZ_AVG = "OZ_AVG";
    public final static String OZ_TOTAL = "OZ_TOTAL";
    public final static String MINS_AVG = "MINS_AVG";
    public final static String MINS_TOTAL = "MINS_TOTAL";

//    #define DAILY_AVG_SUMMARY       NSLocalizedString(@"(daily averages)", @"(daily averages)")
//#define CUMULATIVE_SUMMARY      NSLocalizedString(@"(cumulative)", @"(cumulative)")

//    #define DAY_IN_SECONDS  86400

    public enum BreastType
    {
        LEFT,//=1
        RIGHT,
        BOTH
    }

    public enum SummaryType
    {
        DailyAvg,//=1
        Accumulative,
        MostRecent,
        TimeElapse
    }

    public enum TrackingType
    {
        TrackingTypeUnknown,//=0
        TrackingTypeNursing,
        TrackingTypeDiaper,
        TrackingTypePumping,
        TrackingTypeDiary,
        TrackingTypeBottle,
        TrackingTypeSize,
        TrackingTypeSolids,
        TrackingTypeSleeping,
        TrackingTypeHealth,
        TrackingTypeDrVisit,
        TrackingTypeMedication,
        TrackingTypeVaccination,
        TrackingTypeBath,
        TrackingTypeActivity
    }

    public enum DataType
    {
        Unknown,
        Time,
        Amount,
        Hours
    }


}
