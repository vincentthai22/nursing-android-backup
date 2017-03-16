package com.sevenlogics.babynursing;

import com.sevenlogics.babynursing.utils.CgUtils;

import java.util.Date;

/**
 * Created by stevenchan1 on 3/8/17.
 */

public class EmptySlotData {
    Date date;
    private String dateString;

    public static EmptySlotData emptySlotData(Date date)
    {
        EmptySlotData emptySlotData = new EmptySlotData();
        emptySlotData.date = date;

        return emptySlotData;
    }

    public String getDateString()
    {
        if (this.dateString == null && this.date != null)
        {
            this.dateString = CgUtils.timeFormat.format(this.date).toLowerCase();
        }

        return this.dateString;
    }

}
