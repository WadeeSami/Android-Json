package com.example.wadee.jsondemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;

public class MainActivity extends AppCompatActivity {
    String city = "";
    ListView list;
    final String[] cities = {"Nablus", "London" , "Gaza" ,"Haifa" , "Jerusalem" , "Janin" , "Jericho" ,"Tulkarm"} ;
    ArrayList<String> names;
    Spinner s;
    int selectedCityIndex = 0;
    boolean firstTime = true;
    String weatherDesciption ="";
    String main = "";
    String temp="";
    String pressure ="";
    String humidity="";
    String windSpeed="";
    EditText descTxt;
   /* EditText humTxt;
    EditText speedTxt;
    EditText tempTxt;


    EditText descLbl;
    EditText humLbl;
    EditText speedLbl;
    EditText tempLbl;*/
    Button returnBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        s = (Spinner) findViewById(R.id.spinner);
        descTxt = (EditText)findViewById(R.id.description);
        //returnBtn = (Button)findViewById(R.id.returnBtn);
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, cities);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    Log.i("DEBUG", selectedCityIndex + " ++ First time " + firstTime);

                    //returnBtn.setVisibility(View.VISIBLE);

                    Toast.makeText(MainActivity.this, cities[position], Toast.LENGTH_LONG).show();
                    selectedCityIndex = position;
                    //s.setVisibility(View.INVISIBLE);

                    String result = downloadWeatherData(cities[position]);
                    //Log.i("DEBUG" , result);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


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

    class DataLoader extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            try {
                String KEY = "f72527cbd67a10f4cd939ed813ba74b8";
                city = params[0];
                String urlData = String.format("http://api.openweathermap.org/data/2.5/weather?q=%s&APPID=f72527cbd67a10f4cd939ed813ba74b8", city);
                URL url = new URL(urlData);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream is = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(is);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    // Log.i("DEBUG" , "The current character " +current );
                    data = reader.read();
                }


                return result;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public void cityClicked(View view) {
        Log.i("DEBUG", "City CLicked");

    }

    public String downloadWeatherData(String cityName) {
        DataLoader loader = new DataLoader();
        try {
            String result = loader.execute(cityName).get();
            //Log.i("DEBUG" , result);
            //String []  splitted = result.split(",");
            JSONObject jsonObj = new JSONObject(result);
            String weather = jsonObj.getString("weather");
            String main = jsonObj.getString("main");
            String wind = jsonObj.getString("wind");
            String clouds = jsonObj.getString("clouds");
            JSONArray ar = new JSONArray(weather);

             Log.i("DEBUG" ,"This is the weather : " +weather + " and weatherDescription is :");
             Log.i("DEBUG" , "This is the main : " +main);
             Log.i("DEBUG" , "This is the wind : " +wind);
            for(int i = 0 ; i < ar.length() ; i++){
                JSONObject jsonPart = ar.getJSONObject(i);
                String description = jsonPart.getString("description");
                String main2 = jsonPart.getString("main");
                MainActivity.this.weatherDesciption =description;
                MainActivity.this.main =main2;

            }
            //JSONObject tempObject = new JSONObject("main");
            String temp = jsonObj.getJSONObject("main").getString("temp");
            String humidity = jsonObj.getJSONObject("main").getString("humidity");
            String windSpeed = jsonObj.getJSONObject("wind").getString("speed");
            Log.i("DEBUG" , "temp , humidity , windSpeed" + temp + "  " + humidity + "  " + windSpeed);
            MainActivity.this.temp = temp;

            descTxt.setText("Description : " + weatherDesciption + ", Temp. :" + temp);
            MainActivity.this.humidity = humidity;
            MainActivity.this.windSpeed = windSpeed;
            return "";

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void returnHandler(View view){
        //s.setVisibility(View.VISIBLE);
        /*tempTxt.setVisibility(View.INVISIBLE);
        humTxt.setVisibility(View.INVISIBLE);
        speedTxt.setVisibility(View.INVISIBLE);
        descTxt.setVisibility(View.INVISIBLE);
        returnBtn.setVisibility(View.INVISIBLE);
        tempLbl.setVisibility(View.INVISIBLE);
        humLbl.setVisibility(View.INVISIBLE);
        speedLbl.setVisibility(View.INVISIBLE);
        descLbl.setVisibility(View.INVISIBLE);
*/
    }
}
/*tempLbl.setVisibility(View.VISIBLE);
                    humLbl.setVisibility(View.VISIBLE);
                    speedLbl.setVisibility(View.VISIBLE);
                    descLbl.setVisibility(View.VISIBLE);

                    tempTxt.setVisibility(View.VISIBLE);
                    humTxt.setVisibility(View.VISIBLE);
                    speedTxt.setVisibility(View.VISIBLE);
                    descTxt.setVisibility(View.VISIBLE);
                    */