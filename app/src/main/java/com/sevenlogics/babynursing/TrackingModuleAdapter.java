package com.sevenlogics.babynursing;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.sevenlogics.babynursing.Couchbase.*;
import java.util.ArrayList;

/**
 * Created by stevenchan1 on 1/10/17.
 */

public class TrackingModuleAdapter extends BaseAdapter
{
    private static String TAG = "TrackingModuleAdapter";

    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<TrackingModule> mModules;
    private Baby mBaby;

    private int ViewType_Photo = 0;
    private int ViewType_Tracking = 1;

    public TrackingModuleAdapter(Context context, Baby baby)
    {
        final ArrayList<TrackingModule> moduleList = new ArrayList(5);
        moduleList.add(new TrackingModule("Photo","photo"));
        moduleList.add(new TrackingModule("Diary","diary"));
        moduleList.add(new TrackingModule("Measurements","measurement"));
        moduleList.add(new TrackingModule("Nursing","nursing"));
        moduleList.add(new TrackingModule("Pumping","pumping"));
        moduleList.add(new TrackingModule("Bath Time","bathtime"));
        moduleList.add(new TrackingModule("Bottle","bottle"));
        moduleList.add(new TrackingModule("Diaper","diaper"));
        moduleList.add(new TrackingModule("Medication","medication"));

        mContext = context;
        mModules = moduleList;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mBaby = baby;
    }

    @Override
    public int getCount(){
        return mModules.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mModules.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @Override
    public int getItemViewType(int position)
    {
        TrackingModule trackingModule = (TrackingModule) getItem(position);

        if (trackingModule.title.equals("Photo"))
        {
            return ViewType_Photo;
        }

        return ViewType_Tracking;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        TrackingModule trackingModule = (TrackingModule) getItem(position);

        if (trackingModule.title.equals("Photo"))
        {
            ViewHolderTrackingModule viewHolder;

            if (null == convertView)
            {
                convertView = mInflater.inflate(R.layout.list_item_tracking_photo, parent, false);
                viewHolder = new ViewHolderTrackingModule();
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.background_image_view);
                viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.background_name_textview);
                viewHolder.textView2 = (TextView) convertView.findViewById(R.id.background_age_textview);
                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolderTrackingModule)convertView.getTag();
            }

            viewHolder.titleTextView.setText(mBaby.name);
            viewHolder.textView2.setText("6 days old");

            return convertView;
        }
        else
        {
            ViewHolderTrackingModule viewHolder;

            if (null == convertView)
            {
                convertView = mInflater.inflate(R.layout.list_item_tracking_module, parent, false);

                viewHolder = new ViewHolderTrackingModule();
                viewHolder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
                viewHolder.imageView = (ImageView) convertView.findViewById(R.id.imageView);

                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolderTrackingModule) convertView.getTag();
            }

            viewHolder.titleTextView.setText(trackingModule.title);

            int id = mContext.getResources().getIdentifier(trackingModule.imageName,"drawable",mContext.getPackageName());

            viewHolder.imageView.setImageResource(id);

            return convertView;
        }
    }


    private static class ViewHolderTrackingModule
    {
        public TextView titleTextView;
        public TextView textView2;
        public ImageView imageView;
    }

}
