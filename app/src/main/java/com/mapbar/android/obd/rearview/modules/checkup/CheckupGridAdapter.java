package com.mapbar.android.obd.rearview.modules.checkup;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.manager.PhysicalManager;
import com.mapbar.obd.PhysicalData;

import java.util.HashMap;
import java.util.List;

/**
 * Created by THINKPAD on 2016/5/10.
 */
public class CheckupGridAdapter extends BaseAdapter {

    private LayoutInflater inflater;

    private List<PhysicalData> physicalList;
    private int[] ids = {R.drawable.ic_1, R.drawable.ic_2, R.drawable.ic_3, R.drawable.ic_4, R.drawable.ic_5, R.drawable.ic_6, R.drawable.ic_7};
    private int[] idss = {R.drawable.ic_11, R.drawable.ic_22, R.drawable.ic_33, R.drawable.ic_44, R
            .drawable.ic_55, R.drawable.ic_66, R.drawable.ic_77};
    private Context context;

    public CheckupGridAdapter(Context context, List<PhysicalData> physicalList) {
        this.context = context;
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
        HashMap<String, Integer> statuses = PhysicalManager.getInstance().getStatuses();
        if (statuses.get(String.valueOf(position + 1)) == PhysicalData.Status.OBDCHECK_ERROE.ordinal()) {
            holder.iv_item_checkUp_icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), ids[position]));
        } else {
            holder.iv_item_checkUp_icon.setImageBitmap(BitmapFactory.decodeResource(context.getResources(), idss[position]));
        }
        holder.tv_itemCheckup_name.setText(physicalList.get(position).getName());
        return view;
    }

    private static class Holder {
        public ImageView iv_item_checkUp_icon;
        public TextView tv_itemCheckup_name;
    }
}
