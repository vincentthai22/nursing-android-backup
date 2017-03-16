package com.sevenlogics.babynursing;

/**
 * Created by stevenchan1 on 3/13/17.
 */

import com.sevenlogics.babynursing.AppConstants;
import com.sevenlogics.babynursing.Couchbase.BaseEntity;
import com.sevenlogics.babynursing.Couchbase.GeneralTracking;
import com.sevenlogics.babynursing.Couchbase.UserSettings;
import com.sevenlogics.babynursing.utils.CgUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Objects;

public class BabyManager {
    private static final BabyManager ourInstance = new BabyManager();

    public static BabyManager getInstance() {
        return ourInstance;
    }

    private Hashtable<String, Number> pumpingBorder, bottleBorder, nursingBorder;

    private BabyManager()
    {
        pumpingBorder = new Hashtable<>();
        bottleBorder = new Hashtable<>();
        nursingBorder = new Hashtable<>();
    }

    public Boolean isItem(Integer index, AppConstants.TrackingType trackingType, ArrayList<Object> records)
    {
        Integer groupInterval = groupingInterval(trackingType);

        return this.isItem(index, trackingType, false, records, groupInterval);
    }

    public Boolean isItem(Integer index, Boolean checkGroupStart, ArrayList<Object> records, AppConstants.TrackingType trackingType, Integer groupingInterval)
    {
        if (records.get(index).getClass().getSuperclass() != GeneralTracking.class)
        {
            return false;
        }

        Integer indexToCompare = checkGroupStart ? index - 1 : index + 1;

        Integer recordCount = records.size();

        while (indexToCompare >= 0 && indexToCompare < recordCount)
        {
            if (records.get(indexToCompare).getClass().getSuperclass() == GeneralTracking.class)
            {
                break;
            }

            indexToCompare = checkGroupStart ? indexToCompare - 1 : indexToCompare + 1;
        }

        if ((checkGroupStart && indexToCompare < 0) || (!checkGroupStart && indexToCompare > recordCount))
        {
            return true;
        }

        if (index < recordCount && index >= 0 && indexToCompare < recordCount && indexToCompare >= 0)
        {
            GeneralTracking currentRecord = (GeneralTracking) records.get(index);
            GeneralTracking otherRecord = (GeneralTracking) records.get(indexToCompare);

            Date time1 = checkGroupStart ? currentRecord.startTime : currentRecord.endTime;
            Date time2 = checkGroupStart ? otherRecord.endTime : otherRecord.startTime;

            if (CgUtils.midnight(time1).equals(CgUtils.midnight(time2)))
            {
                if (Math.abs(CgUtils.timeDifference(time1,time2)) > groupingInterval)
                {
                    return true;
                }
            }
            else
            {
                return true;
            }
        }

        return false;
    }

    public Boolean isItem(Integer index, AppConstants.TrackingType trackingType, Boolean checkGroupStart, ArrayList<Object> records, Integer groupingInterval)
    {
        if (trackingType != AppConstants.TrackingType.TrackingTypeBottle && AppConstants.TrackingType.TrackingTypePumping != trackingType && trackingType != AppConstants.TrackingType.TrackingTypeNursing)
        {
            return true;
        }

        Number borderValue = 0;
        BaseEntity record = (BaseEntity) records.get(index);
        String key = record.document.getId();

        if (trackingType == AppConstants.TrackingType.TrackingTypePumping)
        {
            borderValue = pumpingBorder.get(key);
        }
        else if (trackingType == AppConstants.TrackingType.TrackingTypeBottle)
        {
            borderValue = bottleBorder.get(key);
        }
        else if (trackingType == AppConstants.TrackingType.TrackingTypeNursing)
        {
            borderValue = nursingBorder.get(key);
        }

        if (null == borderValue)
        {
            updateGrouping(index, records, trackingType, groupingInterval);
            return this.isItem(index,trackingType,checkGroupStart,records,groupingInterval);
        }

        Integer borderMask = checkGroupStart ? AppConstants.TOP_BORDER : AppConstants.BOTTOM_BORDER;

        Integer result = borderValue.intValue() & borderMask;

        return (result != 0);
    }

    public void updateGrouping(Integer index, ArrayList<Object> records, AppConstants.TrackingType trackingType, Integer groupingInterval)
    {
        GeneralTracking record = (GeneralTracking)records.get(index);

        if (null == record)
        {
            return;
        }

        Integer borderValue = 0;

        if (this.isItem(index, true, records, trackingType, groupingInterval))
        {
            borderValue |= AppConstants.TOP_BORDER;
        }

        if (this.isItem(index, false, records, trackingType, groupingInterval))
        {
            borderValue |= AppConstants.BOTTOM_BORDER;
        }

        saveBorderValue(record, borderValue, trackingType);
    }

    public void saveBorderValue(GeneralTracking record, Number newBorder, AppConstants.TrackingType trackingType)
    {
        String key = record.document.getId();

        if (trackingType == AppConstants.TrackingType.TrackingTypePumping)
        {
            pumpingBorder.put(key, newBorder);
        }
        else if (trackingType == AppConstants.TrackingType.TrackingTypeBottle)
        {
            bottleBorder.put(key, newBorder);
        }
        else if (trackingType == AppConstants.TrackingType.TrackingTypeNursing)
        {
            nursingBorder.put(key, newBorder);
        }
    }

    public Integer groupingInterval(AppConstants.TrackingType trackingType)
    {
        Integer groupingInterval = 0;

        if (trackingType == AppConstants.TrackingType.TrackingTypeNursing)
        {
            groupingInterval = UserSettings.getInstance().nursingTrackingSetting.groupingInterval;
        }

        return groupingInterval;
    }


}
