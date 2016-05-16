package com.mapbar.android.obd.rearview.obd.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.framework.manager.PhysicalManager;
import com.mapbar.obd.PhysicalData;

import java.util.List;

/**
 * Created by THINKPAD on 2016/5/9.
 */
public class VehicleCheckupAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {
    private Context mContext;
    private List<PhysicalData> physicalList;

    private PhysicalData physicalData;


    public VehicleCheckupAdapter(Context mContext, List<PhysicalData> physicalList) {
        this.physicalList = physicalList;
        this.mContext = mContext;
    }

    public void setPhysicalData(PhysicalData physicalData) {
        this.physicalData = physicalData;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(View.inflate(mContext, R.layout.item_recycler_checkup, new RelativeLayout(mContext)));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder arg0, int position) {

        Holder holder = (Holder) arg0;
        if (physicalData != null) {
            if (physicalList.get(position).getId() == physicalData.getId()) {
                if (physicalData.getCount() == physicalData.getProcessed()) {
                    int status = physicalData.getStatus();
                    if (status == PhysicalData.Status.OBDCHECK_ERROE.ordinal() || status == PhysicalData.Status.OBDCHECK_UNKNOWN.ordinal() || status == PhysicalData.Status.OBDCHECK_OK.ordinal())
                        PhysicalManager.getInstance().getStatuses().add(physicalData.getStatus());
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
                int status = PhysicalManager.getInstance().getStatuses().get(position);
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
        holder.iv_checkIcon.setImageBitmap(physicalList.get(position).getIcon());
        holder.tv_checkResult.setVisibility(View.VISIBLE);
//        int status = physicalList.get(position).getStatus();
//        if (status == PhysicalData.Status.OBDCHECK_WILLCHECK.ordinal()) {
//            holder.tv_checkResult.setText(mContext.getResources().getText(R.string.vehicle_checkup_item_waiting));
//            holder.tv_checkResult.setTextColor(mContext.getResources().getColor(R.color.checkUp_gray));
//        }
//        else
        {//if (status == PhysicalData.Status.OBDCHECK_INITVALUE.ordinal()) {
//            holder.tv_checkCount.setVisibility(View.INVISIBLE);
//            holder.tv_checkResult.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return physicalList.size();
    }

    class Holder extends RecyclerView.ViewHolder {

        private final ImageView iv_checkIcon;
        private final TextView tv_checkResult;
        public TextView tv_checkName;

        public Holder(View itemView) {
            super(itemView);
            tv_checkName = (TextView) itemView.findViewById(R.id.tv_itemCheckup_name);
            tv_checkResult = (TextView) itemView.findViewById(R.id.tv_itemCheckup_result);
            iv_checkIcon = (ImageView) itemView.findViewById(R.id.iv_item_checkUp_icon);
        }

    }
}
