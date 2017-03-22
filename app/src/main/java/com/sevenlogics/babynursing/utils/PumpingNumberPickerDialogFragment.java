package com.sevenlogics.babynursing.utils;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.widget.NumberPicker;

/**
 * Created by vincent on 3/22/17.
 */

public class PumpingNumberPickerDialogFragment extends DialogFragment implements NumberPicker.OnValueChangeListener {

    public PumpingNumberPickerDialogFragment(){

    }

    public static PumpingNumberPickerDialogFragment newInstance(int min, int max, int val, int color, String[] displayedValues){
        PumpingNumberPickerDialogFragment frag = new PumpingNumberPickerDialogFragment();
        Bundle args = new Bundle();
        args.putInt("min", min);
        args.putInt("max", max);
        args.putInt("color", color);
        args.putInt("val", val);
        args.putStringArray("displayedValues", displayedValues);
        return frag;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState){
        int min, max, val, color;
        String[] displayedValues;
        min = getArguments().getInt("min");
        max = getArguments().getInt("max");
        val = getArguments().getInt("val");
        color = getArguments().getInt("color");
        displayedValues = getArguments().getStringArray("displayedValues");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        PumpingNumberPicker pumpingNumberPicker = new PumpingNumberPicker(getContext(), min, max, val, color, displayedValues);
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

    }
}
