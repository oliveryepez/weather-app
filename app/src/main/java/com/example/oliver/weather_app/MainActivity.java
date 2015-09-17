package com.example.oliver.weather_app;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import app.AppController;


public class MainActivity extends AppCompatActivity {

    private final double KELVIN_CONST = 273.15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String tag_json_obj = "json_obj_req";
        String url = "http://api.openweathermap.org/data/2.5/weather?q=San%20Jose,crc";

        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    setCity(response);
                    setTemperature(response);
                    setTime(response);
                    setWeatherIcon(response);
                    setMaxMinTemperature(response);
                    setWeatherDesc(response);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });

        AppController.getInstance().addToRequestQueue(jsonRequest, tag_json_obj);

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

    public void drawActivityBackground(JSONArray timeArray){
        
    }

    private void setTextViewFont(TextView textView, String weight){

        Typeface font = null;

        switch (weight.toLowerCase()){
            case "light":
                font = Typeface.createFromAsset(getAssets(), "fonts/lato-light.ttf");
                break;

            case "bold":
                font = Typeface.createFromAsset(getAssets(), "fonts/lato-semibold.ttf");
                break;
        }

        textView.setTypeface(font);
    }

    private void setCity(JSONObject response) throws JSONException {
        String name = response.getString("name");
        TextView txt_city = (TextView) findViewById(R.id.txt_city);
        setTextViewFont(txt_city, "light");
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

    private void setTemperature(JSONObject response) throws JSONException {
        JSONObject mainObject = response.getJSONObject("main");
        Double kelvin_temperature = mainObject.getDouble("temp");

        String textview_text = convertToCelsius(kelvin_temperature);

        TextView txt_temperature = (TextView) findViewById(R.id.txt_temperature);
        setTextViewFont(txt_temperature, "light");
        txt_temperature.setText(textview_text + (char) 0x00B0);

    }

    private void setTime(JSONObject response) throws JSONException {
        long milliseconds = response.getLong("dt");

        Time date = new Time(milliseconds*1000);
        SimpleDateFormat format = new SimpleDateFormat("hh:mm a");

        String time = format.format(date);

        TextView txt_time = (TextView)findViewById(R.id.txt_time);
        setTextViewFont(txt_time, "light");
        txt_time.setText(time);
    }

    private void setWeatherIcon(JSONObject response) throws JSONException {
        JSONArray weatther_array = response.getJSONArray("weather");
        int weather_id = weatther_array.getJSONObject(0).getInt("id");

        TextView weather_icon = (TextView) findViewById(R.id.txt_icon_weather);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/weathericons.ttf");
        weather_icon.setTypeface(font);

        String icon = "";

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

        try{
            weather_icon.setText(icon);

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

            String sMaxTemp = convertToCelsius(dMaxTemp);
            String sMinTemp = convertToCelsius(dMinTemp);

            setTextViewFont(maxTemp, "light");
            setTextViewFont(minTemp, "light");

            maxTemp.setText(sMaxTemp + (char) 0x00B0);
            minTemp.setText("/ " + sMinTemp + (char) 0x00B0);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setWeatherDesc(JSONObject response){
        try {
            JSONArray wearher_array = response.getJSONArray("weather");
            String weather_desc = wearher_array.getJSONObject(0).getString("description");
            TextView txt_description = (TextView)findViewById(R.id.txt_weather_desc);
            setTextViewFont(txt_description, "light");
            txt_description.setText(weather_desc);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
