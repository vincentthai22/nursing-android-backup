package com.sevenlogics.babynursing.Couchbase;

import android.util.Log;

import com.sevenlogics.babynursing.AppConstants;
import com.sevenlogics.babynursing.utils.CgUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class TrackingSetting extends BaseModel
{
    public String dateTitle;
    public Integer dataType;
    public float ozPerMinute;
    public Integer groupingInterval;
    public Boolean showEmptySlots;
    public Boolean isDateSelectionToday;
    public Boolean isDateSelectionThisWeek;
    public Boolean isDateSelectionThisMonth;

    public String breastSelectedText;
    public String dataTypeText;
    public String summaryTypeText;

    public Date startDateData, endDateData;
    public Integer pagingOffsetData;
    public String dateSelectionData;

    public Integer breastSelected;
    public Integer summaryType;

    public ArrayList<Object> stringData1;

    private final static String TAG = "TrackingSetting";

    public AppConstants.BreastType getBreastType()
    {
        AppConstants.BreastType breastType = AppConstants.BreastType.values()[breastSelected - 1];

        return breastType;
    }

    public AppConstants.SummaryType getSummaryType()
    {
        AppConstants.SummaryType aSummaryType = AppConstants.SummaryType.values()[summaryType - 1];

        return aSummaryType;
    }


    public void syncDateSelection()
    {
        String prevSelection = getDateSelection();

        setDateSelection(null);
        setStartDate(null);
        setEndDate(null);

        setDateSelection(prevSelection);
    }

    public String getDateSelection()
    {
        return this.dateSelectionData;
    }

    public void setDateSelection(String dateSelection)
    {
        if (null == getDateSelection() || !getDateSelection().equals(dateSelection))
        {
            this.pagingOffsetData = 0;
            this.dateSelectionData = dateSelection;

            if (null == getDateSelection())
            {
                this.startDateData = null;
                this.endDateData = null;
                this.dateTitle = "";
            }
            else if (getDateSelection().equals(AppConstants.DATE_SELECTION_TODAY))
            {
                this.startDateData = CgUtils.getTodayDate();
                this.endDateData = CgUtils.getTomorrowDate();
                this.dateTitle = AppConstants.DATE_SELECTION_TODAY;
            }
            else if (getDateSelection().equals(AppConstants.DATE_SELECTION_THIS_WEEK))
            {
                this.startDateData = CgUtils.getFirstDateOfWeek();
                this.endDateData = CgUtils.getFirstDateOfNextWeek();
                this.dateTitle = AppConstants.DATE_SELECTION_THIS_WEEK;
            }
            else if (getDateSelection().equals(AppConstants.DATE_SELECTION_THIS_MONTH))
            {
                this.startDateData = CgUtils.getFirstDateOfMonth();
                this.endDateData = CgUtils.getFirstDateOfNextMonth();
                this.dateTitle = AppConstants.DATE_SELECTION_THIS_MONTH;
            }
            else
            {
                this.startDateData = null;
                this.endDateData = null;
                this.dateTitle = "";
            }
        }

    }

    public String startDateString()
    {
        if (getDateSelection() != null)
        {
            String startDateString = null;

            if (getDateSelection().equals(AppConstants.DATE_SELECTION_TODAY))
            {
                Date date = CgUtils.addUnitToDate(getStartDate(), Calendar.DATE, -1);
                startDateString = CgUtils.dateString(date, "MMM d");
            }
            else if (getDateSelection().equals(AppConstants.DATE_SELECTION_THIS_WEEK))
            {
                Date date = CgUtils.addUnitToDate(getStartDate(), Calendar.DATE, -7);
                startDateString = CgUtils.dateString(date, "MMM d") + "\n" + CgUtils.dateString(getStartDate(), "MMM d");

                Log.d(TAG, "What is startDateString " + startDateString);

            }
            else if (getDateSelection().equals(AppConstants.DATE_SELECTION_THIS_MONTH))
            {
                Date date = CgUtils.addUnitToDate(getStartDate(), Calendar.MONTH, -1);
                startDateString = CgUtils.dateString(date, "MMM");
            }

            return startDateString;
        }

        return null;
    }

    public String endDateString()
    {
        if (getDateSelection() != null)
        {
            String endDateString = null;

            if (getDateSelection().equals(AppConstants.DATE_SELECTION_TODAY))
            {
                endDateString = CgUtils.dateString(getEndDate(), "MMM d");
            }
            else if (getDateSelection().equals(AppConstants.DATE_SELECTION_THIS_WEEK))
            {
                Date date = CgUtils.addUnitToDate(getEndDate(), Calendar.DATE, 7);
                endDateString = CgUtils.dateString(getEndDate(), "MMM d") + "\n" + CgUtils.dateString(date, "MMM d");
            }
            else if (getDateSelection().equals(AppConstants.DATE_SELECTION_THIS_MONTH))
            {
                Date date = CgUtils.addUnitToDate(getStartDate(), Calendar.MONTH, 1);
                endDateString = CgUtils.dateString(date, "MMM");
            }

            return endDateString;
        }

        return null;
    }

    public Date getStartDate()
    {
        return this.startDateData;
    }

    public void setStartDate(Date startDate)
    {
        if (getStartDate() == null)
        {
            this.startDateData = startDate;
            this.pagingOffsetData = 0;
            this.dateSelectionData = null;

            if (getStartDate() != null && getEndDate() != null)
            {
                this.dateTitle = CgUtils.dateFormat.format(getStartDate()) + " - " + CgUtils.dateFormat.format(getEndDate());
            }
        }
    }

    public Date getEndDate()
    {
        return this.endDateData;
    }

    public void setEndDate(Date endDate)
    {
          if (getEndDate() != null)
          {
              this.pagingOffsetData = 0;
              this.endDateData = endDate;
              this.dateSelectionData = null;

              if (getStartDate() != null && getEndDate() != null)
              {
                  this.dateTitle = CgUtils.dateFormat.format(getStartDate()) + " - " + CgUtils.dateFormat.format(getEndDate());
              }
          }

    }

    public Integer getPagingOffset()
    {
        return pagingOffsetData;
    }

    public void setPagingOffset(Integer pagingOffset)
    {
        pagingOffsetData = pagingOffset;

        if (getDateSelection() != null)
        {
            if (pagingOffsetData == 0)
            {
                dateTitle = getDateSelection();
            }
            else if (getDateSelection().equals(AppConstants.DATE_SELECTION_TODAY))
            {
                if (pagingOffsetData == -1)
                {
                    dateTitle = "Yesterday";
                }
                else
                {
                    dateTitle = CgUtils.dateString(getStartDate(), "EEE, " + AppConstants.DATE_FORMAT);
                }
            }
            else if (getDateSelection().equals(AppConstants.DATE_SELECTION_THIS_WEEK))
            {
                if (pagingOffsetData == -1)
                {
                    dateTitle = "Last Week";
                }
                else
                {
                    dateTitle = CgUtils.dateString(getStartDate(), "MMM d") + " - " + CgUtils.dateString(getEndDate(), "MMM d");
                }
            }
            else if (getDateSelection().equals(AppConstants.DATE_SELECTION_THIS_MONTH))
            {
                if (pagingOffsetData == -1)
                {
                    dateTitle = "Last Month";
                }
                else
                {
                    dateTitle = CgUtils.dateString(getStartDate(), "MMMM yyyy");
                }
            }
        }
    }


    public void backDate()
    {
        if (getDateSelection() != null)
        {
            if (getDateSelection().equals(AppConstants.DATE_SELECTION_TODAY))
            {
                startDateData = CgUtils.addUnitToDate(getStartDate(),Calendar.DATE, -1);
                endDateData = CgUtils.addUnitToDate(getEndDate(),Calendar.DATE, -1);
            }
            else if (getDateSelection().equals(AppConstants.DATE_SELECTION_THIS_WEEK))
            {
                startDateData = CgUtils.addUnitToDate(getStartDate(),Calendar.DATE, -7);
                endDateData = CgUtils.addUnitToDate(getEndDate(),Calendar.DATE, -7);
            }
            else if (getDateSelection().equals(AppConstants.DATE_SELECTION_THIS_MONTH))
            {
                startDateData = CgUtils.addUnitToDate(getStartDate(),Calendar.MONTH, -1);
                endDateData = CgUtils.addUnitToDate(getEndDate(),Calendar.MONTH, -1);
            }

            setPagingOffset(getPagingOffset() - 1);
        }
    }

    public Boolean isOneDayDateRange()
    {
        if (null != getStartDate() && null != getEndDate())
        {
            long timeInterval = CgUtils.timeDifference(getEndDate(), getStartDate());

            return timeInterval <= AppConstants.DAY_IN_SECONDS;
        }

        return false;
    }



}
