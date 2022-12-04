package com.example.rcoem_weather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private String name, updated_at, description, temperature, min_temperature, max_temperature, pressure, wind_speed, humidity;
    private int condition;
    private long update_time, sunset, sunrise;
    private String city = "";

    EditText sv;
    TextView city_name,city_temp,city_desc;
    Button search_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing instances
        initialize_variable();

        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                city = sv.getText().toString();

                //searching city
                searchCity(city);
            }
        });

    }

    private void initialize_variable(){
        sv = findViewById(R.id.City_search);
        city_name = findViewById(R.id.City_Name);
        city_temp = findViewById(R.id.City_temp);
        search_btn = findViewById(R.id.searchbtn);
        city_desc = findViewById(R.id.description);
    }

    private void searchCity(String cn) {
        if (cn == null || cn.isEmpty()) {
            Toast.makeText(this,"Please enter the correct city name",Toast.LENGTH_LONG).show();
        } else {
//            Log.d("TAG", "searchCity:"+cn);
            setLatitudeLongitudeUsingCity(cn);
        }
    }


    private void setLatitudeLongitudeUsingCity(String cityName) {
        URL.setCity_url(cityName);
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL.getCity_url(), null, response -> {
            try {
                LocationCord.lat = response.getJSONObject("coord").getString("lat");
                LocationCord.lon = response.getJSONObject("coord").getString("lon");
                getTodayWeatherInfo(cityName);
                // After the successfully city search the cityEt(editText) is Empty.
//                binding.layout.cityEt.setText("");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> Toast.makeText(this,"invalid City name",Toast.LENGTH_LONG).show());
        requestQueue.add(jsonObjectRequest);
    }

    private void getTodayWeatherInfo(String cityName) {
        URL url = new URL();
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        @SuppressLint("DefaultLocale") JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url.getLink(), null, response -> {
            try {
                this.name = name;
//                update_time = response.getJSONObject("current").getLong("dt");
//                updated_at = new SimpleDateFormat("EEEE hh:mm a", Locale.ENGLISH).format(new Date(update_time * 1000));
//
//                condition = response.getJSONArray("daily").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getInt("id");
//                sunrise = response.getJSONArray("daily").getJSONObject(0).getLong("sunrise");
//                sunset = response.getJSONArray("daily").getJSONObject(0).getLong("sunset");
                description = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("main");

                temperature = String.valueOf(Math.round(response.getJSONObject("current").getDouble("temp") - 273.15));
//                min_temperature = String.format("%.0f", response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getDouble("min") - 273.15);
//                max_temperature = String.format("%.0f", response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getDouble("max") - 273.15);
//                pressure = response.getJSONArray("daily").getJSONObject(0).getString("pressure");
//                wind_speed = response.getJSONArray("daily").getJSONObject(0).getString("wind_speed");
//                humidity = response.getJSONArray("daily").getJSONObject(0).getString("humidity");
                Log.d("informationex", "getTodayWeatherInfo: "+temperature+name);
                updateUI();
                sv.setText("");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, null);
        requestQueue.add(jsonObjectRequest);
        Log.i("json_req", "Day 0");
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(){
        city_name.setText(city.toUpperCase()+"'s Temperature");
        city_temp.setText(temperature + "°C");
        city_desc.setText(description);
    }
}