package com.sevenlogics.babynursing;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.sevenlogics.babynursing.Couchbase.Nursing;
import com.sevenlogics.babynursing.Couchbase.UserSettings;
import com.sevenlogics.babynursing.TableSection.NursingDailyTableSection;
import com.sevenlogics.babynursing.TableSection.NursingSummaryTableSection;
import com.sevenlogics.babynursing.utils.CgUtils;
import com.sevenlogics.babynursing.utils.SectionAdapter;

import java.util.ArrayList;

/**
 * Created by stevenchan1 on 3/14/17.
 */

public class NursingAdapter extends SectionAdapter
{
    ArrayList<Object> tableSections = new ArrayList<>();

    private static final String TAG = "NursingAdapter";

    private Context context;
    private android.view.LayoutInflater inflater;

    public Boolean noRecordsForDateRange = false;

    public void setupData(ArrayList<Object> tableSections)
    {
        this.tableSections = tableSections;
        this.notifyDataSetChanged();
    }

    public void setup(Context context)
    {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int numberOfSections()
    {
        return tableSections.size();
    }

    @Override
    public int numberOfRows(int section)
    {
        if (section > -1 && section < tableSections.size())
        {
            Object sectionObject = tableSections.get(section);

            if (sectionObject.getClass() == NursingSummaryTableSection.class)
            {
                NursingSummaryTableSection summaryTableSection = (NursingSummaryTableSection)sectionObject;

                if (noRecordsForDateRange)
                {
                    return 1;
                }
                else
                {
                    //this should always be 0
                    return summaryTableSection.sectionData.size();
                }
            }
            else if (sectionObject.getClass() == NursingDailyTableSection.class)
            {
                NursingDailyTableSection dailyTableSection = (NursingDailyTableSection)sectionObject;

//                if (dailyTableSection.expanded)
//                {
                    return dailyTableSection.sectionData.size();
//                }
            }
        }

        return 0;

    }

    @Override
    public Object getRowItem(int section, int row)
    {
        Object sectionObject = tableSections.get(section);

        if (sectionObject.getClass() == NursingSummaryTableSection.class)
        {
            //Summary section should not have any data, apart from no Records
            if (noRecordsForDateRange)
                return noRecordsForDateRange;

            NursingSummaryTableSection nursingSummaryTableSection = (NursingSummaryTableSection)sectionObject;

            return nursingSummaryTableSection.sectionData.get(row);
        }
        else if (sectionObject.getClass() == NursingDailyTableSection.class)
        {
            NursingDailyTableSection nursingDailyTableSection = (NursingDailyTableSection)sectionObject;

            return nursingDailyTableSection.sectionData.get(row);
        }

        return null;
    }

    @Override
    public boolean hasSectionHeaderView(int section)
    {
        return true;
    }

    @Override
    public int getSectionHeaderViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getSectionHeaderItemViewType(int section)
    {
        Object sectionObject = tableSections.get(section);

        if (sectionObject.getClass() == NursingSummaryTableSection.class)
        {
            return 0;
        }
        else if (sectionObject.getClass() == NursingDailyTableSection.class)
        {
            return 1;
        }

        return 1;
    }

    @Override
    public int getRowItemViewType(int section, int row)
    {
        Object object = getRowItem(section, row);

        if (object.getClass() == EmptySlotData.class)
        {
            return 0;
        }
        else if (object.getClass() == Nursing.class)
        {
            return 1;
        }
        else if (object.getClass() == Boolean.class)
        {
            return 2;
        }

        return 0;
    }

    @Override
    public int getRowViewTypeCount()
    {
        return 3;
    }

    @Override
    public View getRowView(int section, int row, View convertView, ViewGroup parent)
    {
        Object object = getRowItem(section, row);

        if (object.getClass() == EmptySlotData.class)
        {
            EmptySlotData emptySlotData = (EmptySlotData)object;

            ViewHolderTimeline viewHolderTimeline;

            if (convertView == null)
            {
                convertView = this.inflater.inflate(R.layout.list_item_timeline,parent, false);
                viewHolderTimeline = new ViewHolderTimeline();
                viewHolderTimeline.timeTextView = (TextView)convertView.findViewById(R.id.list_item_timeline_textview);

                convertView.setTag(viewHolderTimeline);
            }
            else
            {
                viewHolderTimeline = (ViewHolderTimeline) convertView.getTag();
            }

            viewHolderTimeline.timeTextView.setText(emptySlotData.getDateString());
        }
        else if (object.getClass() == Nursing.class)
        {
            Nursing nursing = (Nursing)object;

            ViewHolderTimeline viewHolderTimeline;

            if (convertView == null)
            {
                convertView = this.inflater.inflate(R.layout.list_item_nursing,parent, false);
                viewHolderTimeline = new ViewHolderTimeline();
                viewHolderTimeline.timeTextView = (TextView)convertView.findViewById(R.id.list_item_nursing_time_textview);
                viewHolderTimeline.durationTextView = (TextView)convertView.findViewById(R.id.list_item_nursing_duration_textview);
                viewHolderTimeline.dataTypeTextView = (TextView)convertView.findViewById(R.id.list_item_nursing_datatype_textview);
                viewHolderTimeline.breastTextView = (TextView)convertView.findViewById(R.id.list_item_nursing_breast_textview);
                convertView.setTag(viewHolderTimeline);
            }
            else
            {
                viewHolderTimeline = (ViewHolderTimeline) convertView.getTag();
            }

            viewHolderTimeline.timeTextView.setText(nursing.getStartTimeString());

            if (nursing.leftBreast)
            {
                viewHolderTimeline.breastTextView.setText("L");
            }
            else
            {
                viewHolderTimeline.breastTextView.setText("R");
            }

            if (UserSettings.getInstance().nursingTrackingSetting.dataType == AppConstants.DataType.Time.ordinal())
            {
                float duration = nursing.duration.floatValue() / 60;
                viewHolderTimeline.durationTextView.setText(CgUtils.stringWithFormat("#.1",duration));

                if (duration != 1.0)
                {
                    viewHolderTimeline.dataTypeTextView.setText("mins");
                }
                else
                {
                    viewHolderTimeline.dataTypeTextView.setText("min");
                }
            }
        }
        else    //noRecordsForDateRange
        {
            convertView = inflater.inflate(R.layout.list_item_no_record, parent, false);
        }

        return convertView;
    }

    @Override
    public View getSectionHeaderView(int section, View convertView, ViewGroup parent)
    {
        Object sectionObject = tableSections.get(section);

        if (sectionObject.getClass() == NursingSummaryTableSection.class)
        {
            ViewHolderNursingSummary viewHolder;

            if (null == convertView)
            {
                convertView = inflater.inflate(R.layout.list_item_summary, parent, false);
                viewHolder = new ViewHolderNursingSummary();
                viewHolder.summaryTextView = (TextView) convertView.findViewById(R.id.nursing_summary_date_textview);
                viewHolder.summaryTypeTextView = (TextView) convertView.findViewById(R.id.nursing_summary_type_textview);
                viewHolder.headerTextView1 = (TextView) convertView.findViewById(R.id.nursing_summary_feedings_textview);
                viewHolder.headerTextView2 = (TextView) convertView.findViewById(R.id.nursing_summary_mins_avg_textview);
                viewHolder.headerTextView3 = (TextView) convertView.findViewById(R.id.nursing_summary_mins_total_textview);
                viewHolder.valueTextView1 = (TextView) convertView.findViewById(R.id.nursing_summary_feedings_value_textview);
                viewHolder.valueTextView2 = (TextView) convertView.findViewById(R.id.nursing_summary_mins_avg_value_textview);
                viewHolder.valueTextView3 = (TextView) convertView.findViewById(R.id.nursing_summary_mins_total_value_textview);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolderNursingSummary) convertView.getTag();
            }

            NursingSummaryTableSection summaryTableSection = (NursingSummaryTableSection)sectionObject;

            viewHolder.summaryTextView.setText(summaryTableSection.title);

            Log.d(TAG, "Summary section header view " + summaryTableSection.title);

            if (summaryTableSection.summaryType == AppConstants.SummaryType.Accumulative)
            {
                viewHolder.summaryTypeTextView.setText(AppConstants.CUMULATIVE_SUMMARY);
            }
            else
            {
                viewHolder.summaryTypeTextView.setText(AppConstants.DAILY_AVG_SUMMARY);
            }

            if (summaryTableSection.breastType == AppConstants.BreastType.LEFT)
            {
                viewHolder.valueTextView1.setText(CgUtils.stringWithFormat("#0.0",summaryTableSection.leftBreastCount));

                if (summaryTableSection.dataType == AppConstants.DataType.Time.ordinal())
                {
                    viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.1",summaryTableSection.leftBreastAvgInSeconds / 60.0));
                    viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.1",summaryTableSection.leftBreastTotalInSeconds / 60.0));
                }
                else
                {
                    if (summaryTableSection.amountType.equals(AppConstants.VOLUME_TYPE_ML))
                    {
                        viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.0",summaryTableSection.leftBreastAvgAmount));
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.0",summaryTableSection.leftBreastTotalAmount));
                    }
                    else
                    {
                        viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.1",summaryTableSection.leftBreastAvgAmount));
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.1",summaryTableSection.leftBreastTotalAmount));
                    }
                }
            }
            else if (summaryTableSection.breastType == AppConstants.BreastType.RIGHT)
            {
                viewHolder.valueTextView1.setText(CgUtils.stringWithFormat("#0.0",summaryTableSection.rightBreastCount));

                if (summaryTableSection.dataType == AppConstants.DataType.Time.ordinal())
                {
                    viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.1",summaryTableSection.rightBreastAvgInSeconds / 60.0));
                    viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.1",summaryTableSection.rightBreastTotalInSeconds / 60.0));
                }
                else
                {
                    if (summaryTableSection.amountType.equals(AppConstants.VOLUME_TYPE_ML))
                    {
                        viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.0",summaryTableSection.rightBreastAvgAmount));
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.0",summaryTableSection.rightBreastTotalAmount));
                    }
                    else
                    {
                        viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.1",summaryTableSection.rightBreastAvgAmount));
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.1",summaryTableSection.rightBreastTotalAmount));
                    }
                }
            }
            else
            {
                viewHolder.valueTextView1.setText(CgUtils.stringWithFormat("#0.0",summaryTableSection.bothBreastCount));

                if (summaryTableSection.dataType == AppConstants.DataType.Time.ordinal())
                {
                    viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.1",summaryTableSection.bothBreastAvgInSeconds / 60.0));
                    viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.1",summaryTableSection.bothBreastTotalInSeconds / 60.0));
                }
                else
                {
                    if (summaryTableSection.amountType.equals(AppConstants.VOLUME_TYPE_ML))
                    {
                        viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.0",summaryTableSection.bothBreastAvgAmount));
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.0",summaryTableSection.bothBreastTotalAmount));
                    }
                    else
                    {
                        viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.1",summaryTableSection.bothBreastAvgAmount));
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.1",summaryTableSection.bothBreastTotalAmount));
                    }
                }
            }

            if (null != UserSettings.getInstance().nursingTrackingSetting.stringData1 && UserSettings.getInstance().nursingTrackingSetting.stringData1.size() > 0)
            {
                if (summaryTableSection.summaryType == AppConstants.SummaryType.Accumulative)
                {
                    if (summaryTableSection.amountType.equals(AppConstants.VOLUME_TYPE_ML))
                    {
                        viewHolder.headerTextView3.setText(AppConstants.ML_TOTAL + " (bottle)");
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.0",summaryTableSection.bottleTotalAmount));
                    }
                    else
                    {
                        viewHolder.headerTextView3.setText(AppConstants.OZ_TOTAL + " (bottle)");
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.1",summaryTableSection.bottleTotalAmount));
                    }
                }
                else
                {
                    if (summaryTableSection.amountType.equals(AppConstants.VOLUME_TYPE_ML))
                    {
                        viewHolder.headerTextView3.setText(AppConstants.ML_AVG + " (bottle)");
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.0",summaryTableSection.bottleAvgAmount));
                    }
                    else
                    {
                        viewHolder.headerTextView3.setText(AppConstants.OZ_TOTAL + " (bottle)");
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.1",summaryTableSection.bottleAvgAmount));
                    }
                }
            }
        }
        else if (sectionObject.getClass() == NursingDailyTableSection.class)
        {
            ViewHolderNursingSummary viewHolder;

            if (null == convertView)
            {
                convertView = inflater.inflate(R.layout.list_item_summary_daily, parent, false);
                viewHolder = new ViewHolderNursingSummary();
                viewHolder.summaryTextView = (TextView) convertView.findViewById(R.id.list_item_summary_header_textview);
                viewHolder.headerTextView1 = (TextView) convertView.findViewById(R.id.list_item_summary_header_textview_1);
                viewHolder.headerTextView2 = (TextView) convertView.findViewById(R.id.list_item_summary_header_textview_2);
                viewHolder.headerTextView3 = (TextView) convertView.findViewById(R.id.list_item_summary_header_textview_3);
                viewHolder.valueTextView1 = (TextView) convertView.findViewById(R.id.list_item_summary_value_textview_1);
                viewHolder.valueTextView2 = (TextView) convertView.findViewById(R.id.list_item_summary_value_textview_2);
                viewHolder.valueTextView3 = (TextView) convertView.findViewById(R.id.list_item_summary_value_textview_3);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolderNursingSummary) convertView.getTag();
            }

            final NursingDailyTableSection dailyTableSection = (NursingDailyTableSection)sectionObject;

            if (CgUtils.isToday(dailyTableSection.date))
            {
                viewHolder.summaryTextView.setText("Today");
            }
            else if (CgUtils.isYesterday(dailyTableSection.date))
            {
                viewHolder.summaryTextView.setText("Yesterday");
            }
            else
            {
                viewHolder.summaryTextView.setText(CgUtils.dateString(dailyTableSection.date,"EEE, dd"));
            }

            if (dailyTableSection.dataType == AppConstants.DataType.Time.ordinal())
            {
                viewHolder.headerTextView2.setText(AppConstants.MINS_AVG);
                viewHolder.headerTextView3.setText(AppConstants.MINS_TOTAL);
            }
            else if (dailyTableSection.dataType == AppConstants.DataType.Amount.ordinal())
            {
                if (dailyTableSection.amountType.equals(AppConstants.VOLUME_TYPE_ML))
                {
                    viewHolder.headerTextView2.setText(AppConstants.ML_AVG);
                    viewHolder.headerTextView3.setText(AppConstants.ML_TOTAL);
                }
                else
                {
                    viewHolder.headerTextView2.setText(AppConstants.OZ_AVG);
                    viewHolder.headerTextView3.setText(AppConstants.OZ_TOTAL);
                }
            }

            if (dailyTableSection.breastType == AppConstants.BreastType.LEFT)
            {
                viewHolder.valueTextView1.setText(CgUtils.stringWithFormat("#0.0",dailyTableSection.leftBreastCount + dailyTableSection.bottleOnlyGroupCount));

                if (dailyTableSection.dataType == AppConstants.DataType.Time.ordinal())
                {
                    viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.1",dailyTableSection.leftBreastAvgInSeconds / 60));
                    viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.1",dailyTableSection.leftBreastTotalInSeconds / 60));
                }
                else
                {
                    if (dailyTableSection.amountType.equals(AppConstants.VOLUME_TYPE_ML))
                    {
                        viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.0",dailyTableSection.leftBreastAvgAmount));
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.0",dailyTableSection.leftBreastTotalAmount));
                    }
                    else
                    {
                        viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.1",dailyTableSection.leftBreastAvgAmount));
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.1",dailyTableSection.leftBreastTotalAmount));
                    }
                }
            }
            else if (dailyTableSection.breastType == AppConstants.BreastType.RIGHT)
            {
                viewHolder.valueTextView1.setText(CgUtils.stringWithFormat("#0.0",dailyTableSection.rightBreastCount + dailyTableSection.bottleOnlyGroupCount));

                if (dailyTableSection.dataType == AppConstants.DataType.Time.ordinal())
                {
                    viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.1",dailyTableSection.rightBreastAvgInSeconds / 60));
                    viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.1",dailyTableSection.rightBreastTotalInSeconds / 60));
                }
                else
                {
                    if (dailyTableSection.amountType.equals(AppConstants.VOLUME_TYPE_ML))
                    {
                        viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.0",dailyTableSection.rightBreastAvgAmount));
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.0",dailyTableSection.rightBreastTotalAmount));
                    }
                    else
                    {
                        viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.1",dailyTableSection.rightBreastAvgAmount));
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.1",dailyTableSection.rightBreastTotalAmount));
                    }
                }
            }
            else
            {
                viewHolder.valueTextView1.setText(CgUtils.stringWithFormat("#0.0",dailyTableSection.bothBreastCount + dailyTableSection.bottleOnlyGroupCount));

                if (dailyTableSection.dataType == AppConstants.DataType.Time.ordinal())
                {
                    viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.1",dailyTableSection.bothBreastAvgInSeconds / 60));
                    viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.1",dailyTableSection.bothBreastTotalInSeconds / 60));
                }
                else
                {
                    if (dailyTableSection.amountType.equals(AppConstants.VOLUME_TYPE_ML))
                    {
                        viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.0",dailyTableSection.bothBreastAvgAmount));
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.0",dailyTableSection.bothBreastTotalAmount));
                    }
                    else
                    {
                        viewHolder.valueTextView2.setText(CgUtils.stringWithFormat("#0.1",dailyTableSection.bothBreastAvgAmount));
                        viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#0.1",dailyTableSection.bothBreastTotalAmount));
                    }
                }
            }

            if (null != UserSettings.getInstance().nursingTrackingSetting.stringData1 && UserSettings.getInstance().nursingTrackingSetting.stringData1.size() > 0)
            {
                if (dailyTableSection.amountType.equals(AppConstants.VOLUME_TYPE_ML))
                {
                    viewHolder.headerTextView3.setText(AppConstants.ML_AVG + " (bottle)");
                    viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#.0",dailyTableSection.bottleAvgAmount));
                }
                else
                {
                    viewHolder.headerTextView3.setText(AppConstants.OZ_AVG + " (bottle)");
                    viewHolder.valueTextView3.setText(CgUtils.stringWithFormat("#.1",dailyTableSection.bottleAvgAmount));
                }
            }

            final NursingAdapter adapter = this;

            convertView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Section view clicked");
                    //toggle the expanded
                    dailyTableSection.expanded = !dailyTableSection.expanded;
                    adapter.notifyDataSetChanged();
                }
            });
        }

        return convertView;
    }

    @Override
    public void onRowItemClick(AdapterView<?> parent, View view, int section, int row, long id)
    {
//        Intent intent = new Intent(this,NursingDetailActivity.class);
//
//        intent.putExtra(MainActivity.INTENT_KEY_BABY_ID,mBaby.document.getId());
//
//        startActivity(intent);
    }

    private static class ViewHolderTimeline
    {
        public TextView timeTextView;
        public TextView dataTypeTextView;
        public TextView durationTextView;
        public TextView breastTextView;
    }


    private static class ViewHolderNursingSummary
    {
        public TextView summaryTextView;
        public TextView summaryTypeTextView;
        public TextView headerTextView1;
        public TextView headerTextView2;
        public TextView headerTextView3;
        public TextView valueTextView1;
        public TextView valueTextView2;
        public TextView valueTextView3;
    }

}
