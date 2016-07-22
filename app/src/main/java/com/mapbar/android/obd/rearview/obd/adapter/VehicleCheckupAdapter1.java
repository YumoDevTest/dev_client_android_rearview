package com.mapbar.android.obd.rearview.obd.adapter;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.common.Global;
import com.mapbar.android.obd.rearview.framework.manager.PhysicalManager;
import com.mapbar.android.obd.rearview.obd.MainActivity;
import com.mapbar.obd.PhysicalData;

import java.util.List;

/**
 * Created by THINKPAD on 2016/5/9.
 */
public class VehicleCheckupAdapter1 extends BaseAdapter {
    private Context mContext;
    private List<PhysicalData> physicalList;

    private PhysicalData physicalData;
    private int[] ids = {R.drawable.ic_1, R.drawable.ic_2, R.drawable.ic_3, R.drawable.ic_4, R.drawable.ic_5, R.drawable.ic_6, R.drawable.ic_7};
    private int[] idss = {R.drawable.ic_11, R.drawable.ic_22, R.drawable.ic_33, R.drawable.ic_44, R
            .drawable.ic_55, R.drawable.ic_66, R.drawable.ic_77};


    public VehicleCheckupAdapter1(Context mContext, List<PhysicalData> physicalList) {
        this.physicalList = physicalList;
        this.mContext = mContext;
    }

    public void setPhysicalData(PhysicalData physicalData) {
        this.physicalData = physicalData;
        if (physicalList.contains(physicalData)) {
            int index = physicalList.indexOf(physicalData);
            physicalList.add(index, physicalData);
        }

    }


    @Override
    public int getCount() {
        return physicalList.size();
    }

    @Override
    public Object getItem(int position) {
        return physicalList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder1 holder = null;

        if (convertView != null) {

            holder = (Holder1) convertView.getTag();
        } else {
            convertView = View.inflate(MainActivity.getInstance(), R.layout.item_recycler_checkup, null);
            holder = new Holder1();
            holder.tv_checkName = (TextView) convertView.findViewById(R.id.tv_itemCheckup_name);
            holder.tv_checkResult = (TextView) convertView.findViewById(R.id.tv_itemCheckup_result);
            holder.iv_checkIcon = (ImageView) convertView.findViewById(R.id.iv_item_checkUp_icon);
            convertView.setTag(holder);

        }


        if (physicalData != null) {
            if (physicalList.get(position).getId() == physicalData.getId()) {
                if (physicalData.getCount() == physicalData.getProcessed()) {
                    int status = physicalData.getStatus();
                    Log.e("wwwwwwww", "id----->" + physicalData.getId() + "");
                    Log.e("wwwwwwww", "status---->" + status + "");
                    if (status == PhysicalData.Status.OBDCHECK_ERROE.ordinal() || status == PhysicalData.Status.OBDCHECK_UNKNOWN.ordinal() || status == PhysicalData.Status.OBDCHECK_OK.ordinal())
                        PhysicalManager.getInstance().getStatuses().put(String.valueOf(physicalData.getId()), status);
                }
                int status = physicalData.getStatus();
                if (status == PhysicalData.Status.OBDCHECK_CHECKING.ordinal()) {
                    holder.tv_checkResult.setText("检测中");
                    holder.tv_checkResult.setTextColor(mContext.getResources().getColor(R.color.linechart));
                } else if (status == PhysicalData.Status.OBDCHECK_UNKNOWN.ordinal()) {
                    holder.tv_checkResult.setText("无法判定");
                    holder.tv_checkResult.setTextColor(mContext.getResources().getColor(R.color.checkUp_yello));
                } else if (status == PhysicalData.Status.OBDCHECK_ERROE.ordinal()) {
                    holder.tv_checkResult.setText("异常");
                    holder.tv_checkResult.setTextColor(mContext.getResources().getColor(R.color.check_red));

                } else if (status == PhysicalData.Status.OBDCHECK_OK.ordinal()) {
                    holder.tv_checkResult.setText("正常");
                    holder.tv_checkResult.setTextColor(mContext.getResources().getColor(R.color.checkUp_gray));

                }
            } else if (physicalList.get(position).getId() > physicalData.getId()) {
                holder.tv_checkResult.setText("待检测");
                holder.tv_checkResult.setTextColor(mContext.getResources().getColor(R.color.checkUp_gray));
            } else {
                int status = PhysicalManager.getInstance().getStatuses().get(String.valueOf(position + 1));
                if (status == PhysicalData.Status.OBDCHECK_CHECKING.ordinal()) {
                    holder.tv_checkResult.setText("检测中");
                    holder.tv_checkResult.setTextColor(mContext.getResources().getColor(R.color.linechart));
                } else if (status == PhysicalData.Status.OBDCHECK_UNKNOWN.ordinal()) {
                    holder.tv_checkResult.setText("无法判定");
                    holder.tv_checkResult.setTextColor(mContext.getResources().getColor(R.color.checkUp_yello));
                } else if (status == PhysicalData.Status.OBDCHECK_ERROE.ordinal()) {
                    holder.tv_checkResult.setText("异常");
                    holder.tv_checkResult.setTextColor(mContext.getResources().getColor(R.color.check_red));

                } else if (status == PhysicalData.Status.OBDCHECK_OK.ordinal()) {
                    holder.tv_checkResult.setText("正常");
                    holder.tv_checkResult.setTextColor(mContext.getResources().getColor(R.color.checkUp_gray));
                }
            }
        }
        holder.tv_checkName.setText(physicalList.get(position).getName());

        holder.iv_checkIcon.setImageBitmap(BitmapFactory.decodeResource(Global.getAppContext().getResources(), idss[position]));
        holder.tv_checkResult.setVisibility(View.VISIBLE);


        return convertView;
    }

    class Holder1 {

        ImageView iv_checkIcon;
        TextView tv_checkResult;
        TextView tv_checkName;


    }
}
