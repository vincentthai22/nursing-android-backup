package com.sevenlogics.babynursing.utils;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetDialog;
import android.view.ViewGroup;

/**
 * Created by pravin on 8/21/16.
 */
public class CgBottomSheetDialog extends BottomSheetDialog {

    Context mContext;

    public CgBottomSheetDialog(@NonNull Context context) {
        super(context);
        mContext = context;
    }

    public CgBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        int screenHeight = CgUtils.getScreenHeight(mContext);
        int statusBarHeight = CgUtils.getStatusBarHeight(mContext);
        int dialogHeight = screenHeight - statusBarHeight;
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
    }
}
