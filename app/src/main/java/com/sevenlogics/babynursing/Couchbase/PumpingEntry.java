package com.sevenlogics.babynursing.Couchbase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by vincent on 3/14/17.
 */

public class PumpingEntry {

    private int totalPumps;
    private float averageWeight, totalWeight;
    private Date date;

    private List<String> pumpingRecords;

    public PumpingEntry(int totalPumps, float averageWeight, float totalWeight){
        this.totalPumps = totalPumps;
        this.averageWeight = averageWeight;
        this.totalWeight = totalWeight;
        pumpingRecords = new ArrayList<>();
        for(int i = 1 ; i < 24;i++){
            if (i < 13)
                pumpingRecords.add(i+":00 " +((i!=12) ? "am" : "pm") );
            else
                pumpingRecords.add(i-12+":00 pm");

        }
        date = new Date();
    }


    public void setTotalPumps(int totalPumps) {
        this.totalPumps = totalPumps;
    }

    public void setAverageWeight(float averageWeight) {
        this.averageWeight = averageWeight;
    }

    public void setTotalWeight(float totalWeight) {
        this.totalWeight = totalWeight;
    }

    public int getTotalPumps() {
        return totalPumps;
    }

    public float getAverageWeight() {
        return averageWeight;
    }

    public float getTotalWeight() {
        return totalWeight;
    }

    public List<String> getPumpingRecords() {
        return pumpingRecords;
    }


}
