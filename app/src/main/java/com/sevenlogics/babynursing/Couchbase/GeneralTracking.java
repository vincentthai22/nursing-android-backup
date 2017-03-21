package com.sevenlogics.babynursing.Couchbase;

import com.sevenlogics.babynursing.utils.CgUtils;

import java.util.Date;

/**
 * Created by stevenchan1 on 1/12/17.
 */

public class GeneralTracking extends BaseEntity
{
    public Number displayGrouping;
    public Number endWeight2;
    public Number amountFedInOz;
    public Number startWeight1;
    public Number startWeight2;
    public String startWeightType;
    public Date endTime;
    public Number endWeight1;
    public Number duration;
    public String endWeightType;
    public Date startTime;
    public Number filterNotDisplay;
//    public CBLBaby TrackingBaby;

    public String getStartTimeString()
    {
        return CgUtils.timeFormat.format(this.startTime).toLowerCase();
    }

}
