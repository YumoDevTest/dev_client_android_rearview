package com.mapbar.android.obd.rearview.obd.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.obd.PhysicalData;

import java.util.List;

/**
 * Created by THINKPAD on 2016/5/10.
 */
public class CheckupGridAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<PhysicalData> physicalList;

    public CheckupGridAdapter(Context context, List<PhysicalData> physicalList) {
        this.inflater = LayoutInflater.from(context);
        this.physicalList = physicalList;
    }

    @Override
    public int getCount() {
        return physicalList.size();
    }

    @Override
    public Object getItem(int position) {
        return physicalList == null ? null : physicalList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = inflater.inflate(R.layout.item_grid, parent, false);
        }
        Holder holder = (Holder) view.getTag();
        if (holder == null) {
            holder = new Holder();
            holder.iv_item_checkUp_icon = (ImageView) view.findViewById(R.id.iv_item_checkUp_icon);
            holder.tv_itemCheckup_name = (TextView) view.findViewById(R.id.tv_itemCheckup_name);
            view.setTag(holder);
        }
        holder.iv_item_checkUp_icon.setImageBitmap(physicalList.get(position).getIcon());
        holder.tv_itemCheckup_name.setText(physicalList.get(position).getName());
        return view;
    }

    private static class Holder {
        public ImageView iv_item_checkUp_icon;
        public TextView tv_itemCheckup_name;
    }
}
