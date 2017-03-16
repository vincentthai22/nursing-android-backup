package com.sevenlogics.babynursing.TableSection;

import com.sevenlogics.babynursing.AppConstants;

/**
 * Created by stevenchan1 on 3/8/17.
 */

public class NursingBaseTableSection extends TableViewSection
{
    public float bottleCount, bottleOnlyGroupCount;

    public AppConstants.BreastType breastType;
    public Integer dataType;
    public float bothBreastAvgAmount, bothBreastTotalAmount;
    public String amountType;

    public float leftBreastAvgInSeconds, leftBreastTotalInSeconds;
    public float rightBreastAvgInSeconds, rightBreastTotalInSeconds;
    public float leftBreastAvgAmount, leftBreastTotalAmount;
    public float rightBreastAvgAmount, rightBreastTotalAmount;

    public float bothBreastAvgInSeconds, bothBreastTotalInSeconds;
    public float bottleAvgAmount, bottleTotalAmount;
}
