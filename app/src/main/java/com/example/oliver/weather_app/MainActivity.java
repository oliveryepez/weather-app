package com.example.oliver.weather_app;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import app.AppController;


public class MainActivity extends AppCompatActivity implements LocationListener {

    private final double KELVIN_CONST = 273.15;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Double latitude, longitude;
    private boolean gps_enabled, network_enabled;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       try{
           locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
           Criteria criteria = new Criteria();
           String provider = locationManager.getBestProvider(criteria, false);

           if(provider !=null && !provider.equals("")){
               Location location = locationManager.getLastKnownLocation(provider);
               locationManager.requestLocationUpdates(provider,2000,1,this);

               if(location != null){
                    onLocationChanged(location);
               }else{
                   Toast.makeText(getApplicationContext(),"location not found", Toast.LENGTH_LONG ).show();
               }
           }else{
               Toast.makeText(getApplicationContext(),"Provider is null",Toast.LENGTH_LONG).show();
           }

       }catch (SecurityException e){
           e.printStackTrace();
       }

        TextView txt_loading = (TextView)findViewById(R.id.txt_loading);
        setTextViewFont(txt_loading, "helvetica");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setActivityBackground(Drawable background){
        RelativeLayout mainLayout = (RelativeLayout) findViewById(R.id.mainLayout);
        mainLayout.setBackground(background);
    }

    private void setTextViewFont(TextView textView, String fontString){

        Typeface font = null;

        switch (fontString.toLowerCase()){
            case "helvetica":
                font = Typeface.createFromAsset(getAssets(), "fonts/helvetica-neue-ultra-light.ttf");
                break;

            case "lato":
                font = Typeface.createFromAsset(getAssets(), "fonts/lato-light.ttf");
                break;
        }

        textView.setTypeface(font);
    }

    private void setCity(JSONObject response) throws JSONException {
        String name = response.getString("name");
        TextView txt_city = (TextView) findViewById(R.id.txt_city);
        setTextViewFont(txt_city, "helvetica");
        txt_city.setText(name);
    }

    private String convertToCelsius(Double kelvin_temperature){
        String textview_text = null;
        Double celsius_tempeture = 0.0;

        celsius_tempeture = kelvin_temperature - this.KELVIN_CONST;
        textview_text = Double.toString(celsius_tempeture);
        textview_text = String.format("%.0f", celsius_tempeture);

        return textview_text;
    }

    private String convertToDate(long unixTime, String format){
        Date date = new Date(unixTime * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        String time = dateFormat.format(date);

        return time;
    }

    private void setIcons(TextView textView, String icon){
        Typeface typeface_font = Typeface.createFromAsset(getAssets(), "fonts/weathericons.ttf");
        textView.setTypeface(typeface_font);
        textView.setText(icon);
    }

    private void setTemperature(JSONObject response) throws JSONException {
        JSONObject mainObject = response.getJSONObject("main");
        Double kelvin_temperature = mainObject.getDouble("temp");

        String textview_text = convertToCelsius(kelvin_temperature);

        TextView txt_temperature = (TextView) findViewById(R.id.txt_temperature);
        setTextViewFont(txt_temperature, "helvetica");
        txt_temperature.setText(textview_text + (char) 0x00B0);

    }

    private void setTime(JSONObject response) throws JSONException {
        long milliseconds = response.getLong("dt");
        String time = convertToDate(milliseconds, "hh:mm a");

        TextView txt_time = (TextView)findViewById(R.id.txt_time);
        setTextViewFont(txt_time, "helvetica");
        txt_time.setText(time);
    }

    private void setWeather(JSONObject response) throws JSONException {
        JSONArray weather_array = response.getJSONArray("weather");

        int weather_id = weather_array.getJSONObject(0).getInt("id");
        String weather_desc = weather_array.getJSONObject(0).getString("description");

        TextView txt_weather = (TextView) findViewById(R.id.txt_weather);
        TextView icon_weather = (TextView) findViewById(R.id.weather_icon);

        String icon = "";
        Drawable background = null;

        switch (weather_id){
            case 200:
            case 201:
            case 202:
            case 210:
            case 211:
            case 212:
            case 221:
            case 230:
            case 231:
            case 232:
                    icon = getString(R.string.weather_thunder);
                    background = getResources().getDrawable(R.drawable.thunder_background);
                break;

            case 300:
            case 301:
            case 302:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 321:
                icon = getString(R.string.weather_drizzle);
                background = getResources().getDrawable(R.drawable.drizzle_background);
                break;

            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
            case 511:
            case 520:
            case 521:
            case 522:
            case 531:
                icon = getString(R.string.weather_rainy);
                background = getResources().getDrawable(R.drawable.rainy_background);
                break;

            case 800:
                icon = getString(R.string.weather_sunny);
                background = getResources().getDrawable(R.drawable.sunny_background);
                break;

            case 801:
            case 802:
            case 803:
            case 804:
                icon = getString(R.string.weather_cloudy);
                background = getResources().getDrawable(R.drawable.cloudy_background);
                break;
        }

        try{
            setIcons(icon_weather, icon);
            icon_weather.setText(icon);
            setTextViewFont(txt_weather, "helvetica");
            txt_weather.setText(weather_desc);

            setActivityBackground(background);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setMaxMinTemperature(JSONObject response){
        try {
            JSONObject mainObject = response.getJSONObject("main");

            Double dMaxTemp = mainObject.getDouble("temp_max");
            Double dMinTemp = mainObject.getDouble("temp_min");

            TextView maxTemp = (TextView) findViewById(R.id.txt_maxTemp);
            TextView minTemp = (TextView) findViewById(R.id.txt_minTemp);

            TextView minIcon = (TextView)findViewById(R.id.min_icon);
            TextView maxIcon = (TextView) findViewById(R.id.max_icon);

            setIcons(maxIcon, getString(R.string.max_temp));
            setIcons(minIcon, getString(R.string.min_temp));

            String sMaxTemp = convertToCelsius(dMaxTemp);
            String sMinTemp = convertToCelsius(dMinTemp);

            setTextViewFont(maxTemp, "helvetica");
            setTextViewFont(minTemp, "helvetica");

            maxTemp.setText(sMaxTemp + (char) 0x00B0);
            minTemp.setText(sMinTemp + (char) 0x00B0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setSunriseSunset(JSONObject response){
        try {
            JSONObject oSys = response.getJSONObject("sys");
            long sunrise_unix = oSys.getLong("sunrise");
            long sunset_unix = oSys.getLong("sunset");

            String sunrise_time = convertToDate(sunrise_unix, "hh:mm a");
            String sunset_time = convertToDate(sunset_unix, "hh:mm a");

            TextView sunrise = (TextView) findViewById(R.id.icon_sunrise);
            TextView sunset = (TextView) findViewById(R.id.icon_sunset);

            TextView txt_sunrise = (TextView) findViewById(R.id.txt_sunrise);
            TextView txt_sunset = (TextView) findViewById(R.id.txt_sunset);

            setIcons(sunrise, getString(R.string.sunrise));
            setIcons(sunset, getString(R.string.sunset));

            setTextViewFont(txt_sunrise, "helvetica");
            setTextViewFont(txt_sunset, "helvetica");

            txt_sunrise.setText(sunrise_time);
            txt_sunset.setText(sunset_time);


        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void setPressure(JSONObject response){
        try {
            JSONObject oMain = response.getJSONObject("main");
            int pressureValue = oMain.getInt("pressure");

            TextView pressureIcon = (TextView) findViewById(R.id.icon_pressure);
            setIcons(pressureIcon, getString(R.string.pressure));

            TextView txt_pressure = (TextView) findViewById(R.id.txt_pressure);
            setTextViewFont(txt_pressure, "helvetica");
            txt_pressure.setText(Integer.toString(pressureValue) + " hPa");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private void setHumidity(JSONObject response){
        try {
            JSONObject oMain = response.getJSONObject("main");
            int humidity_value = oMain.getInt("humidity");

            TextView humidity_icon = (TextView) findViewById(R.id.icon_humidity);
            setIcons(humidity_icon, getString(R.string.humidity));

            TextView txt_humidity = (TextView) findViewById(R.id.txt_humidity);
            setTextViewFont(txt_humidity, "helvetica");
            txt_humidity.setText(Integer.toString(humidity_value) + " %");

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    private String setIconWeather(int id){
        String icon = null;

        switch (id){
            case 200:
            case 201:
            case 202:
            case 210:
            case 211:
            case 212:
            case 221:
            case 230:
            case 231:
            case 232:
                icon = getString(R.string.weather_thunder);
                break;

            case 300:
            case 301:
            case 302:
            case 310:
            case 311:
            case 312:
            case 313:
            case 314:
            case 321:
                icon = getString(R.string.weather_drizzle);

                break;

            case 500:
            case 501:
            case 502:
            case 503:
            case 504:
            case 511:
            case 520:
            case 521:
            case 522:
            case 531:
                icon = getString(R.string.weather_rainy);

                break;

            case 800:
                icon = getString(R.string.weather_sunny);

                break;

            case 801:
            case 802:
            case 803:
            case 804:
                icon = getString(R.string.weather_cloudy);

                break;
        }


        return icon;
    }

    private void setTableForecast(int countryId) throws JSONException {

        final String tag_json_obj = "forecast_request";
        String forecast_url = String.format("http://api.openweathermap.org/data/2.5/forecast/daily?id=%1$d&lan=%2$s", countryId, "en");

        final TableLayout tblForecast = (TableLayout) findViewById(R.id.tblForecast);

        JsonObjectRequest forecastRequest = new JsonObjectRequest(Request.Method.GET, forecast_url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray listArray = response.getJSONArray("list");
                            tblForecast.removeAllViews();

                            //Set Table Header
                            TableRow tblHeader = new TableRow(context);
                            tblHeader.setPadding(15, 18, 15, 18);
                            TextView txt_header = new TextView(context);
                            setTextViewFont(txt_header, "helvetica");
                            txt_header.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                            txt_header.setText("Forecast");

                            View separatorHeader = new View(context);
                            separatorHeader.setPadding(15, 0, 75, 0);
                            separatorHeader.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                            separatorHeader.setBackgroundColor(Color.parseColor("#DADFE1"));

                            tblHeader.addView(txt_header);
                            tblForecast.addView(tblHeader);
                            tblForecast.addView(separatorHeader);

                            for(int item = 0; item < listArray.length(); item++){
                                TableRow tblRow = new TableRow(context);

                                TextView txt_day = new TextView(context);
                                TextView txt_daily_weather = new TextView(context);
                                TextView txt_daily_max_temp = new TextView(context);
                                TextView txt_daily_min_temp = new TextView(context);

                                tblRow.setPadding(15, 18, 15, 18);

                                //Set day text
                                txt_day.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                long unix_day = listArray.getJSONObject(item).getLong("dt");
                                String day_of_week = convertToDate(unix_day, "EEEE");
                                setTextViewFont(txt_day, "helvetica");
                                txt_day.setText(day_of_week);

                                //Set weather icon
                                txt_daily_weather.setGravity(Gravity.CENTER_HORIZONTAL);
                                txt_daily_weather.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
                                txt_daily_weather.setPadding(175, 0, 25, 0);
                                int weather_id = listArray.getJSONObject(0).optJSONArray("weather").getJSONObject(0).getInt("id");
                                String icon = setIconWeather(weather_id);
                                setIcons(txt_daily_weather, icon);

                                //Set max temperature
                                double max_temp = listArray.getJSONObject(item).getJSONObject("temp").getDouble("max");
                                String text_max_temp = convertToCelsius(max_temp);
                                txt_daily_max_temp.setGravity(Gravity.CENTER_HORIZONTAL);
                                txt_daily_max_temp.setPadding(75, 0, 20, 0);
                                txt_daily_max_temp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                setTextViewFont(txt_daily_max_temp, "helvetica");
                                txt_daily_max_temp.setText(text_max_temp + (char) 0x00B0);

                                //Set min temperature
                                double min_temp = listArray.getJSONObject(item).getJSONObject("temp").getDouble("min");
                                String txt_min_temp = convertToCelsius(min_temp);
                                txt_daily_min_temp.setGravity(Gravity.CENTER_HORIZONTAL);
                                txt_daily_min_temp.setPadding(25, 0, 20, 0);
                                txt_daily_min_temp.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                                txt_daily_min_temp.setTextColor(Color.parseColor("#59ABE3"));
                                setTextViewFont(txt_daily_max_temp, "helvetica");
                                txt_daily_min_temp.setText(text_max_temp + (char) 0x00B0);

                                View separatorRow = new View(context);
                                separatorRow.setPadding(15, 0, 75, 0);
                                separatorRow.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 2));
                                separatorRow.setBackgroundColor(Color.parseColor("#6C7A89"));


                                tblRow.addView(txt_day);
                                tblRow.addView(txt_daily_weather);
                                tblRow.addView(txt_daily_max_temp);
                                tblRow.addView(txt_daily_min_temp);
                                tblForecast.addView(tblRow);
                                tblForecast.addView(separatorRow);
                            }


                        }catch(JSONException e){
                            Log.i(tag_json_obj, e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(tag_json_obj, tag_json_obj + volleyError.toString());
            }
        });

        AppController.getInstance().addToRequestQueue(forecastRequest, forecast_url);

    }

    @Override
    public void onLocationChanged(Location location) {
       try{
           this.latitude = location.getLatitude();
           this.longitude = location.getLongitude();
       }catch (Exception e){
           Log.i("FUCKING ERROR", e.toString());
       }


        final String tag_json_obj = "json_obj_req";
        String url = String.format("http://api.openweathermap.org/data/2.5/weather?lat=%1$f&lon=%2$f", this.latitude, this.longitude);


        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                int country_id;
                findViewById(R.id.loaging_spinner).setVisibility(View.GONE);
                findViewById(R.id.txt_loading).setVisibility(View.GONE);
                try {

                    country_id = response.getInt("id");

                    setCity(response);
                    setTemperature(response);
                    setTime(response);
                    setWeather(response);
                    setMaxMinTemperature(response);
                    setSunriseSunset(response);
                    setPressure(response);
                    setHumidity(response);
                    setTableForecast(country_id);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.i(tag_json_obj, volleyError.toString());
            }
        });

        AppController.getInstance().addToRequestQueue(jsonRequest, tag_json_obj);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
