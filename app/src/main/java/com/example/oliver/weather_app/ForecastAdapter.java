package com.example.oliver.weather_app;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by oliver on 20/09/15.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastHolder> {

    private List<HourlyForecast> hourlyForecastList;

    public ForecastAdapter(List<HourlyForecast> hourlyForecastList){
        this.hourlyForecastList = hourlyForecastList;
    }

    @Override
    public ForecastHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).
                        inflate(R.layout.forecast_view, viewGroup, false);

        return new ForecastHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ForecastHolder forecastHolder, int i) {
        HourlyForecast hourlyForecast = hourlyForecastList.get(i);
        forecastHolder.vHour.setText(hourlyForecast.hour);
    }

    @Override
    public int getItemCount() {
        return hourlyForecastList.size();
    }
}
