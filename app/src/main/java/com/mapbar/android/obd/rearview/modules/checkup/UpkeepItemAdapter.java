package com.mapbar.android.obd.rearview.modules.checkup;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.android.obd.rearview.lib.config.MyApplication;
import com.mapbar.obd.MaintenanceTask;
import com.mapbar.obd.foundation.log.LogUtil;

import java.util.Arrays;
import java.util.List;

/**
 * Created by THINKPAD on 2016/5/25.
 * 设置保养项显示的adapter
 * 根据获取的保养
 */
public class UpkeepItemAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {

    private String[] names = {"更换发动机机油", "发动机润滑油", "更换机油滤清器", "更换空气滤清器", "更换PCV滤清器", "更换燃油滤清器",
            "更换燃油粗滤器", "更换燃油空气滤清器", "更换火花塞", "更换前桥齿轮油", "更换后桥齿轮油", "更换齿轮油", "更换整车制动液", "更换主减速器油",
            "更换发电机皮带", "更换压缩机皮带", "更换转向助力皮带", "更换传动皮带套件", "更换发动机正时套件", "更换空调滤清器", "前制动器", "后制动器",
            "更换电瓶", "更换蓄电池", "更换三元催化器", "轮胎换位", "更换点火线", "更换离合器油", "更换燃油泵", "更换PDCC储液罐", "更换碳罐", "清洗油路",
            "更换分动器油", "更换差速器油", "清洗进气道", "更换轮毂轴承润滑脂", "清洗喷油嘴", "清洗节气门", "更换CVVT油道滤网", "更换前轮轴承", "更换水泵",
            "更换冷却液", "更换转向助力液", "更换变速器油", "更换变速箱油（CVT）", "更换变速箱油（AT）", "更换变速箱油（MT）", "更换变速箱油（DCT）", "更换变速箱油（AMT）"};

    private int[] icons = {R.drawable.img_upkeep1, R.drawable.img_upkeep1, R.drawable.img_upkeep2, R.drawable.img_upkeep3, R.drawable.img_upkeep3, R.drawable.img_upkeep4,
            R.drawable.img_upkeep4, R.drawable.img_upkeep4, R.drawable.img_upkeep5, R.drawable.img_upkeep6, R.drawable.img_upkeep6, R.drawable.img_upkeep6, R.drawable.img_upkeep7,
            R.drawable.img_upkeep7, R.drawable.img_upkeep8, R.drawable.img_upkeep8, R.drawable.img_upkeep8, R.drawable.img_upkeep8, R.drawable.img_upkeep8, R.drawable.img_upkeep9,
            R.drawable.img_upkeep10, R.drawable.img_upkeep10, R.drawable.img_upkeep11, R.drawable.img_upkeep11, R.drawable.img_upkeep12, R.drawable.img_upkeep13, R.drawable.img_upkeep14,
            R.drawable.img_upkeep15, R.drawable.img_upkeep16, R.drawable.img_upkeep17, R.drawable.img_upkeep18, R.drawable.img_upkeep19, R.drawable.img_upkeep20, R.drawable.img_upkeep21,
            R.drawable.img_upkeep22, R.drawable.img_upkeep23, R.drawable.img_upkeep24, R.drawable.img_upkeep25, R.drawable.img_upkeep26, R.drawable.img_upkeep27, R.drawable.img_upkeep28,
            R.drawable.img_upkeep29, R.drawable.img_upkeep30, R.drawable.img_upkeep30, R.drawable.img_upkeep30, R.drawable.img_upkeep30, R.drawable.img_upkeep30, R.drawable.img_upkeep30};

    private MaintenanceTask[] tasks;

    private Context mContext;

    private List nameList;

    public UpkeepItemAdapter(Context mContext, MaintenanceTask[] tasks) {
        for (MaintenanceTask task :
                tasks) {
            LogUtil.d("UpkeepItemAdapter", "## " + task.name);
        }
        this.tasks = tasks;
        this.mContext = mContext;
        nameList = Arrays.asList(names);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(View.inflate(mContext, R.layout.item_upkeep, new LinearLayout(mContext)));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder arg0, int position) {

        Holder holder = (Holder) arg0;
        int i = nameList.indexOf(tasks[position].name);
        if (i == -1)
            holder.img_icon.setImageBitmap(BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), R.drawable.img_upkeep_nol));
        else
            holder.img_icon.setImageBitmap(BitmapFactory.decodeResource(MyApplication.getInstance().getResources(), icons[i]));
        holder.tv_name.setText(String.valueOf(tasks[position].name));
    }

    @Override
    public int getItemCount() {
        return tasks.length;
    }

    class Holder extends RecyclerView.ViewHolder {
        public TextView tv_name;
        public ImageView img_icon;

        public Holder(View itemView) {
            super(itemView);
            img_icon = (ImageView) itemView.findViewById(R.id.img_icon);
            tv_name = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }
}
