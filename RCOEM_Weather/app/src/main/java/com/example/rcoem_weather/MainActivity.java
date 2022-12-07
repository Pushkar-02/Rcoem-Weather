package com.example.rcoem_weather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {


    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    TextView txtLat;
    String lat;
    String provider;
    protected String latitude, longitude;
    protected boolean gps_enabled, network_enabled;
    String curr_city;


    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(provider, 400, 1, this);
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    @Override
    public void onLocationChanged(Location location) {
        //You had this as int. It is advised to have Lat/Loing as double.
        double lat = location.getLatitude();
        double lng = location.getLongitude();

        Geocoder geoCoder = new Geocoder(this, Locale.getDefault());
        StringBuilder builder = new StringBuilder();
        try {
            List<Address> address = geoCoder.getFromLocation(lat, lng, 1);
            String cityName = address.get(0).getLocality();
            curr_city = cityName;


//            latituteField.setText(String.valueOf(lat));
//            longitudeField.setText(String.valueOf(lng));
//            addressField.setText(fnialAddress); //This will display the final address.

            Log.e(cityName,"City");
            //Log.e(String.valueOf(lat),"check");

        } catch (IOException e) {
            // Handle IOException
        } catch (NullPointerException e) {
            // Handle NullPointerException
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {


    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(this, "Enabled new provider " + provider,
                Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(this, "Disabled provider " + provider,
                Toast.LENGTH_SHORT).show();
    }


    private String name, updated_at, description, temperature, feels_like, min_temperature, max_temperature, pressure, wind_speed, humidity;
    private int condition;
    private long update_time, sunset, sunrise;
    private String city = "";
    String main;

    EditText sv;
    TextView city_name, city_temp, city_desc, city_temp_mini, city_temp_max, city_feels_like;
    Button search_btn;
    NotificationManager mNotificationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);



        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);


        if (location != null) {
            System.out.println("Provider " + provider + " has been selected.");
            onLocationChanged(location);
        }

        //initializing instances
        initialize_variable();

        searchCity(curr_city);

        listner();



        search_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                city = sv.getText().toString();

                //searching city
                searchCity(city);




            }
        });

        city_name = findViewById(R.id.City_Name);
        city_name.setText(curr_city);


    }

    private void initialize_variable(){
        sv = findViewById(R.id.City_search);
        city_name = findViewById(R.id.City_Name);
        city_temp = findViewById(R.id.City_temp);
        search_btn = findViewById(R.id.searchbtn);
        city_desc = findViewById(R.id.description);
        city_temp_mini = findViewById(R.id.city_temp_mini);
        city_temp_max = findViewById(R.id.city_temp_max);
        city_feels_like = findViewById(R.id.city_temp_feelslike);

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
                main = response.getJSONArray("daily").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("main");
                Log.e("main",main);
                ImageView iv = (ImageView) findViewById(R.id.imageView2);
                if(main.equals("Rain")){

                    iv.setImageResource(R.drawable.img2);
                }
                else{
                    iv.setImageResource(R.drawable.img1);
                }
                //else if(main=="C")



//                sunrise = response.getJSONArray("daily").getJSONObject(0).getLong("sunrise");
             // sunset = response.getJSONArray("daily").getJSONObject(0).getLong("sunset");
              //Log.e(""+sunset,"sunset");
                description = response.getJSONObject("current").getJSONArray("weather").getJSONObject(0).getString("main");

                temperature = String.valueOf(Math.round(response.getJSONObject("current").getDouble("temp") - 273.15));
                min_temperature = String.format("%.0f", response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getDouble("min") - 273.15);
                max_temperature = String.format("%.0f", response.getJSONArray("daily").getJSONObject(0).getJSONObject("temp").getDouble("max") - 273.15);
                feels_like =  String.valueOf(Math.round(response.getJSONObject("current").getDouble("feels_like") - 273.15));
//                pressure = response.getJSONArray("daily").getJSONObject(0).getString("pressure");
//                wind_speed = response.getJSONArray("daily").getJSONObject(0).getString("wind_speed");
//                humidity = response.getJSONArray("daily").getJSONObject(0).getString("humidity");
                Log.d("informationex", "getTodayWeatherInfo: "+temperature+name);
                curr_city=cityName;
                updateUI();
                sv.setText("");

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
                mBuilder.setSmallIcon(R.drawable.min_temp_icon);
                mBuilder.setContentTitle("Current Temperature is " + temperature);
                mBuilder.setContentText("Feels Like " + feels_like);



                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                {
                    String channelId = "Your_channel_id";
                    NotificationChannel channel = new NotificationChannel(
                            channelId,
                            "Channel human readable title",
                            NotificationManager.IMPORTANCE_HIGH);
                    mNotificationManager.createNotificationChannel(channel);
                    mBuilder.setChannelId(channelId);
                }

// notificationID allows you to update the notification later on.
                mNotificationManager.notify(5, mBuilder.build());


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, null);
        requestQueue.add(jsonObjectRequest);
        Log.i("json_req", "Day 0");
    }

    private void listner(){
        sv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_SEARCH) {
                    city = sv.getText().toString();
                    searchCity(city);
                    return true;
                }
                return false;
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void updateUI(){
        city_name.setText(curr_city.toUpperCase()+"'s Temperature");
        city_temp.setText(temperature + "°C");
        city_desc.setText(description);
        city_temp_max.setText(max_temperature+ "°C");
        city_temp_mini.setText(min_temperature+ "°C");
        city_feels_like.setText("Feels like "+feels_like+ "°C");
    }


    void onClick(){
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(location != null && location.getTime() > Calendar.getInstance().getTimeInMillis() - 2 * 60 * 1000) {
                // Do something with the recent location fix
                //  otherwise wait for the update below
                Log.e(location.getLatitude()+"","check");
            }
//            else {
//                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//            }
        }
        else{
            Log.e("Access Denied","Check Permission");
        }
    }

}