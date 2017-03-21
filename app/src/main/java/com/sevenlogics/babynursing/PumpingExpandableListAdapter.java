package com.sevenlogics.babynursing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.sevenlogics.babynursing.Couchbase.PumpingEntry;

import java.util.List;

/**
 * Created by vincent on 3/14/17.
 */

public class PumpingExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<PumpingEntry> pumpingList;

    public PumpingExpandableListAdapter(Context context, List<PumpingEntry> pumpingList){
        this.context = context;
        this.pumpingList = pumpingList;

    }

    @Override
    public int getGroupCount() {
        return pumpingList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return pumpingList.get(groupPosition).getPumpingRecords().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return pumpingList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return pumpingList.get(groupPosition).getPumpingRecords().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return groupPosition + childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (groupPosition != 0)
                view = layoutInflater.inflate(R.layout.list_view_cell_pumping,parent, false);
            else
                view = layoutInflater.inflate(R.layout.list_view_header_cell_pumping,parent,false);
        } else
            view = convertView;

        TextView pumpingsTextView = (TextView) view.findViewById(R.id.pumpingsDataTextView);
        TextView averageWeightTextView = (TextView) view.findViewById(R.id.averageWeightDataTextView);
        TextView totalWeightTextView = (TextView) view.findViewById(R.id.weightTotalDataTextView);
        PumpingEntry pumpingEntry = ((PumpingEntry)getGroup(groupPosition));
        pumpingsTextView.setText(pumpingEntry.getTotalPumps() + "");
        averageWeightTextView.setText(pumpingEntry.getAverageWeight()+"");
        totalWeightTextView.setText(pumpingEntry.getTotalWeight()+"");
        return view;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        View view;
        if(convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.expandable_list_item_child,parent,false);
        } else
            view = convertView;
        TextView timeTextView = (TextView) view.findViewById(R.id.timeTextView);

        timeTextView.setText((String)getChild(groupPosition,childPosition));
        return view;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
