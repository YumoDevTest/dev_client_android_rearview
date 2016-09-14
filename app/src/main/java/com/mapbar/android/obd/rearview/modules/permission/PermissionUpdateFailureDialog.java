//package com.mapbar.android.obd.rearview.modules.permission;
//
//import android.app.Dialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//
//import com.mapbar.android.obd.rearview.R;
//
///**
// * 权限失败dialog
// * Created by zhangyunfei on 16/8/4.
// */
//public class PermissionUpdateFailureDialog extends Dialog {
//
//    private View contentView;
//    private Button btn_retry;
//    private Button btn_skip;
//    private View.OnClickListener onRetryClickListener;
//    private View.OnClickListener onSkipClickListener;
//
//    public PermissionUpdateFailureDialog(Context context) {
//        super(context, R.style.Dialog_Fullscreen);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        init(getActivity());
//    }
//
//    private void init(Context context) {
//        LayoutInflater inflater = LayoutInflater.from(context);
//        contentView = inflater.inflate(R.layout.permission_failure, null);
//        setContentView(contentView);
//
//        btn_retry = (Button) contentView.findViewById(R.id.btn_retry);
//        btn_skip = (Button) contentView.findViewById(R.id.btn_skip);
//
//        setCancelable(false);
//        setCanceledOnTouchOutside(false);
//
//        btn_retry.setOnClickListener(this.onRetryClickListener);
//        btn_skip.setOnClickListener(this.onSkipClickListener);
//    }
//
//    /**
//     * 重试按钮点击事件
//     *
//     * @param onRetryClickListener
//     */
//    public void setOnRetryClickListener(View.OnClickListener onRetryClickListener) {
//        this.onRetryClickListener = onRetryClickListener;
//        if (btn_retry != null)
//            btn_retry.setOnClickListener(this.onRetryClickListener);
//
//    }
//
//    /**
//     * 跳过按钮点击事件
//     *
//     * @param onSkipClickListener
//     */
//    public void setOnSkipClickListener(View.OnClickListener onSkipClickListener) {
//        this.onSkipClickListener = onSkipClickListener;
//        if (btn_skip != null)
//            btn_skip.setOnClickListener(this.onSkipClickListener);
//    }
//
//}
