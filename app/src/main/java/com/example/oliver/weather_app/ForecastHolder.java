package com.example.oliver.weather_app;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by oliver on 20/09/15.
 */
public class ForecastHolder extends RecyclerView.ViewHolder {
    protected TextView vHour;

    public ForecastHolder(View itemView) {
        super(itemView);

        vHour = (TextView) itemView.findViewById(R.id.txt_hour);
    }
}
