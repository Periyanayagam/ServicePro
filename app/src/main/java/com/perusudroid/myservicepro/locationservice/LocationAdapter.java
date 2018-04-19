package com.perusudroid.myservicepro.locationservice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.perusudroid.myservicepro.R;

import java.util.List;

/**
 * Created by Perusudroid on 4/19/2018.
 */

public class LocationAdapter extends ArrayAdapter<Data> {


    private List<Data> myData;
    private Context mContext;

    public LocationAdapter(List<Data> mData, Context applicationContext) {
        super(applicationContext, R.layout.inflater_location, mData);
        this.myData = mData;
        this.mContext=applicationContext;
    }

    public void refresh(List<Data> mData) {
        this.myData = mData;
        notifyDataSetChanged();
    }

    // View lookup cache
    private static class ViewHolder {
        TextView tvAddress;
        TextView tvLatLng;
    }


    public LocationAdapter(@NonNull Context context, int resource, List<Data> mList) {
        super(context, resource);
        this.myData = mList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get the data item for this position
        Data dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag


        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.inflater_location, parent, false);
            viewHolder.tvAddress = (TextView) convertView.findViewById(R.id.tvAddress);
            viewHolder.tvLatLng = (TextView) convertView.findViewById(R.id.tvLatLng);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        viewHolder.tvAddress.setText(dataModel.getAddress());
        viewHolder.tvLatLng.setText(String.format("%s , %s", dataModel.getLat(), dataModel.getLng()));

        // Return the completed view to render on screen
        return convertView;
    }


}


