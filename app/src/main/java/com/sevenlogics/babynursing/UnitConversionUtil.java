package com.sevenlogics.babynursing;

/**
 * Created by stevenchan1 on 3/13/17.
 */

public class UnitConversionUtil
{
    private static double ML_TO_OZ = 0.0338140227;

    public static float convertToMlFromOz(float oz)
    {
        return convertToMlFromOz(oz, true);
    }


    public static float convertToMlFromOz(float oz, Boolean noDecimals)
    {
        if (oz > 0.0)
        {
            float ml = (float)(oz / ML_TO_OZ);

            return ml;
        }

        return 0;
    }

}
