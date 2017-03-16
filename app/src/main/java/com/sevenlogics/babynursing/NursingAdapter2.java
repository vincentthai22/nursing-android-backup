package com.sevenlogics.babynursing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.sevenlogics.babynursing.utils.CgUtils;

/**
 * Created by stevenchan1 on 3/1/17.
 */

public class NursingAdapter2 extends BaseAdapter
{
//    ArrayList<Object> adapterData;
    ArrayList<Object> tableSections = new ArrayList<>();

    Context mContext;
    LayoutInflater mInflater;

    Calendar mCalendar;

    private final static String TAG = "NursingAdapter";

    private int ViewType_Summary = 0;
    private int ViewType_Timeline = 1;

    public void setupData(ArrayList<Object> tableSections)
    {
        this.tableSections = tableSections;

        this.notifyDataSetChanged();
    }

    public void setup(Context context)
    {
        mContext = context;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

//        adapterData = new ArrayList<>();
//
//        // today
//        mCalendar = new GregorianCalendar();
//// reset hour, minutes, seconds and millis
//        mCalendar.set(Calendar.HOUR_OF_DAY, 0);
//        mCalendar.set(Calendar.MINUTE, 0);
//        mCalendar.set(Calendar.SECOND, 0);
//        mCalendar.set(Calendar.MILLISECOND, 0);
//
//        SummaryData todayData = new SummaryData();
//        todayData.expanded = false;
//        todayData.date = mCalendar.getTime();
//        todayData.summaryHeader = "Today";
//        todayData.value1 = "1";
//        todayData.value2 = "20.0";
//        todayData.value3 = "20.0";
//        todayData.header1 = "feedings";
//        todayData.header2 = "mins avg";
//        todayData.header3 = "mins total";
//
//        adapterData.add(todayData);
//
//        mCalendar.add(Calendar.DATE, -1);
//
//        SummaryData yesterdayData = new SummaryData();
//        yesterdayData.expanded = false;
//        yesterdayData.date = mCalendar.getTime();
//        yesterdayData.summaryHeader = "Yesterday";
//        yesterdayData.value1 = "2";
//        yesterdayData.value2 = "25.0";
//        yesterdayData.value3 = "50.0";
//        yesterdayData.header1 = "feedings";
//        yesterdayData.header2 = "mins avg";
//        yesterdayData.header3 = "mins total";
//
//        adapterData.add(yesterdayData);
//

    }

    @Override
    public int getCount()
    {
        return tableSections.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        Object item = getItem(position);

        if (item.getClass() == String.class)
        {
            return ViewType_Timeline;
        }

        return ViewType_Summary;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup)
    {
        Object item = getItem(i);

        if (item.getClass() == SummaryData.class)
        {
            ViewHolderNursingSummary viewHolder;

            SummaryData summaryData = (SummaryData)item;

            if (null == view)
            {
                view = mInflater.inflate(R.layout.list_item_summary_daily, viewGroup, false);
                viewHolder = new ViewHolderNursingSummary();
                viewHolder.summaryTextView = (TextView) view.findViewById(R.id.list_item_summary_header_textview);
                viewHolder.headerTextView1 = (TextView) view.findViewById(R.id.list_item_summary_header_textview_1);
                viewHolder.headerTextView2 = (TextView) view.findViewById(R.id.list_item_summary_header_textview_2);
                viewHolder.headerTextView3 = (TextView) view.findViewById(R.id.list_item_summary_header_textview_3);
                viewHolder.valueTextView1 = (TextView) view.findViewById(R.id.list_item_summary_value_textview_1);
                viewHolder.valueTextView2 = (TextView) view.findViewById(R.id.list_item_summary_value_textview_2);
                viewHolder.valueTextView3 = (TextView) view.findViewById(R.id.list_item_summary_value_textview_3);
                view.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolderNursingSummary) view.getTag();
            }

            viewHolder.summaryTextView.setText(summaryData.summaryHeader);
            viewHolder.headerTextView1.setText(summaryData.header1);
            viewHolder.headerTextView2.setText(summaryData.header2);
            viewHolder.headerTextView3.setText(summaryData.header3);
            viewHolder.valueTextView1.setText(summaryData.value1);
            viewHolder.valueTextView2.setText(summaryData.value2);
            viewHolder.valueTextView3.setText(summaryData.value3);

            return view;
        }
        else if (item.getClass() == String.class)
        {
            ViewHolderNursingSummary viewHolder;

            String dateString = (String)item;

            if (null == view)
            {
                view = mInflater.inflate(R.layout.list_item_timeline, viewGroup, false);
                viewHolder = new ViewHolderNursingSummary();
                viewHolder.summaryTextView = (TextView) view.findViewById(R.id.list_item_timeline_textview);
                view.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolderNursingSummary) view.getTag();
            }

            viewHolder.summaryTextView.setText(dateString);

            return view;
        }

        return null;
    }

    @Override
    public long getItemId(int i)
    {
        return i;
    }

    @Override
    public Object getItem(int i)
    {
        return tableSections.get(i);
    }

    public void itemTapped(int position)
    {
        Object object = getItem(position);

        if (object.getClass() == SummaryData.class)
        {
            SummaryData summaryData = (SummaryData)object;

            int index = tableSections.indexOf(summaryData) + 1;

            if (summaryData.expanded)
            {
                //collapsing data, find all rows after this summary data and remove them
                Object nextObject = tableSections.get(index);

                int endIndex = index;

                while (nextObject.getClass() != SummaryData.class)
                {
                    endIndex++;

                    if (endIndex >= tableSections.size())
                    {
                        break;
                    }

                    nextObject = tableSections.get(endIndex);
                }

                tableSections.subList(index, endIndex).clear();

                summaryData.expanded = false;

                this.notifyDataSetChanged();
            }
            else
            {
                //expanding data, insert timeline and records after this summary data
                //loop the from the summary data's date for every hour until the next day
                Date dateIndex = summaryData.date;

                mCalendar.setTime(dateIndex);
                mCalendar.add(Calendar.DATE,1);
                mCalendar.add(Calendar.HOUR, -1);
                Date endDate = mCalendar.getTime();

                mCalendar.setTime(dateIndex);

                while (dateIndex.before(endDate))
                {
                    mCalendar.add(Calendar.HOUR,1);

                    dateIndex = mCalendar.getTime();

                    String dateString = CgUtils.timeFormat2.format(dateIndex);

                    tableSections.add(index++, dateString);
                }

                summaryData.expanded = true;

                this.notifyDataSetChanged();

            }
        }
    }

    private static class ViewHolderNursingSummary
    {
        public TextView summaryTextView;
        public TextView headerTextView1;
        public TextView headerTextView2;
        public TextView headerTextView3;
        public TextView valueTextView1;
        public TextView valueTextView2;
        public TextView valueTextView3;
    }

}
