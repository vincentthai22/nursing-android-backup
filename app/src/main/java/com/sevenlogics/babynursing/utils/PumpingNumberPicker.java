package com.sevenlogics.babynursing.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.lang.reflect.Field;

/**
 * Created by vincent on 3/22/17.
 */

public class PumpingNumberPicker extends NumberPicker {

    public PumpingNumberPicker(Context context, int minValue, int maxValue, int currentValue, int textColor){
        super(context);
        setMinValue(minValue);
        setMaxValue(maxValue);
        setValue(currentValue);
        setNumberPickerTextColor(this, textColor);
    }
    public PumpingNumberPicker(Context context, int minValue, int maxValue, int currentValue, int textColor, String[] displayedValues){
        super(context);
        setMinValue(minValue);
        setMaxValue(maxValue);
        setValue(currentValue);
        setDisplayedValues(displayedValues);
        setNumberPickerTextColor(this, textColor);

    }

    public boolean setNumberPickerTextColor(NumberPicker numberPicker, int color) {
        final int count = numberPicker.getChildCount();
        for (int i = 0; i < count; i++) {
            View child = numberPicker.getChildAt(i);
            if (child instanceof EditText) {
                try {
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint) selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText) child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                } catch (NoSuchFieldException e) {
                    Log.d("setNumberPickerTextColo", e + "");
                } catch (IllegalAccessException e) {
                    Log.d("setNumberPickerTextColo", e + "");
                } catch (IllegalArgumentException e) {
                    Log.d("setNumberPickerTextColo", e + "");
                }
            }
        }
        return false;
    }

}
