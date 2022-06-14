package com.teammoeg.frostedheart.climate;

import java.util.ArrayList;

public class TempEvent {
    public long startTime;
    public long peakTime;
    public float peakTemp;
    public long bottomTime;
    public float bottomTemp;
    public long endTime;
    public boolean isCold;
    public long calmEndTime;

    public TempEvent(long startTime, long peakTime, float peakTemp, long bottomTime, float bottomTemp, long endTime,
                     long calmEndTime, boolean isCold) {
        this.startTime = startTime;
        this.peakTime = peakTime;
        this.peakTemp = peakTemp;
        this.bottomTime = bottomTime;
        this.bottomTemp = bottomTemp;
        this.endTime = endTime;
        this.calmEndTime = calmEndTime;
        this.isCold = isCold;
    }

    //TODO: impl random
    public static TempEvent getTempEvent(long prevEndTime) {
        return new TempEvent(prevEndTime, 0, 0, 0, 0, 0, 0, true);
    }

    public float getHourTemp(long t) {
        if (t >= startTime && t < peakTime) {
            return getPiecewiseTemp(t, startTime, peakTime, 0, peakTemp, 0, 0);
        } else if (t >= peakTime && t < bottomTime) {
            return getPiecewiseTemp(t, peakTime, bottomTime, peakTemp, bottomTemp, 0, 0);
        } else if (t >= bottomTime && t < endTime) {
            return getPiecewiseTemp(t, bottomTime, endTime, bottomTemp, 0, 0, 0);
        } else if (t >= endTime && t < calmEndTime) {
            return 0F;
        } else {
            return 0F;
        }
    }

    private float getPiecewiseTemp(long t, long t0, long t1, float T0, float T1, float dT0, float dT1) {

        float D1 = t - t0;
        float D2 = t1 - t0;
        float D3 = t - t1;
        float D4 = t0 - t1;

        float F1 = D3 / D4;
        float F2 = D1 / D2;

        float P1 = (float) Math.pow(F1, 2);
        float P2 = (float) Math.pow(F2, 2);

        return  T0 * (1 + 2 * F2) * P1 +
                T1 * (1 + 2 * F1) * P2 +
                dT0 * D1 * P1 +
                dT1 * D3 * P2;
    }

}