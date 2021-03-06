package com.mapbar.android.obd.rearview.modules.setting;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mapbar.android.obd.rearview.R;
import com.mapbar.obd.foundation.base.BaseFragment;
import com.mapbar.android.obd.rearview.lib.config.Configs;
import com.mapbar.android.obd.rearview.util.QRUtils;
import com.mapbar.android.obd.rearview.modules.push.ixintui.AixintuiConfigs;
import com.mapbar.android.obd.rearview.framework.log.Log;
import com.mapbar.android.obd.rearview.framework.log.LogTag;
import com.mapbar.android.obd.rearview.lib.config.MyApplication;
import com.mapbar.obd.UserCenter;

/**
 * 更改手机号 - 扫码页
 * Created by zhangyunfei on 16/7/26.
 */
public class ChangePhoneFragment extends BaseFragment {
    private View rootView;
    private ImageView imagevie1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.setting_chang_phone, null);
            imagevie1 = (ImageView) rootView.findViewById(R.id.imagevie1);

            showQrCodeView();
        }
        return rootView;
    }

    @Override
    public void onStart() {

        super.onStart();
    }

    /**
     * 显示二维码
     */
    private void showQrCodeView() {
        Bitmap bmQR = QRUtils.createQR(buildQrURL());
        imagevie1.setImageBitmap(bmQR);
    }

    /**
     * 构造二维码url
     *
     * @return
     */
    private String buildQrURL() {
        StringBuilder sb = new StringBuilder();
        sb.append(Configs.URL_REG_INFO).append("imei=").append(MyApplication.getInstance().getImei()).append("&");
        sb.append("pushToken=").append(AixintuiConfigs.getPushToken()).append("&");
        sb.append("token=").append(UserCenter.getInstance().getCurrentUserToken());
//        sb.append("&").append("userid=").append(UserCenter.create().getCurrentIdAndType().userId); //少了userid

        String url = sb.toString();
        // 日志
        if (Log.isLoggable(LogTag.OBD, Log.DEBUG)) {
            Log.d(LogTag.OBD, "在修改手机号时，构造二维码url： " + url);
        }
        return url;
    }
}
