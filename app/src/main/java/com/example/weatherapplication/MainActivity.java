package com.example.weatherapplication;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private TextView cityNameText, temperatureText, humidityText, windText, descriptionText;
    private ImageView weatherIcon;
    private static final String API_KEY = "a213868eda12cd0f4a02247153d985a0";
    private EditText cityNameInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityNameText = findViewById(R.id.cityNameText);
        temperatureText = findViewById(R.id.temperatureText);
        humidityText = findViewById(R.id.humidityText);
        windText = findViewById(R.id.windText);
        descriptionText = findViewById(R.id.descriptionText);
        weatherIcon = findViewById(R.id.weatherIcon);
        cityNameInput = findViewById(R.id.cityNameInput);
        Button weatherButton = findViewById(R.id.WeatherButton);

        weatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String cityName = cityNameInput.getText().toString();
                if(!cityName.isEmpty()){

                    FetchWeatherData(cityName);

                }else{

                    cityNameInput.setError("Please enter city name");

                }

            }
        });


        FetchWeatherData("Welkom");

    }

    private void FetchWeatherData(String cityName) {

        String url = "https:api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API_KEY
                + "&units=metric";
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() ->{

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder().url(url).build();

            try {
                Response response = client.newCall(request).execute();
                String results = response.body().string();
                runOnUiThread(() -> updateUI(results));

            }catch(IOException e){

                e.printStackTrace();

            }

        });


    }

    private void updateUI(String results) {

        if(results != null){
            try {

                JSONObject jsonObject = new JSONObject(results);
                JSONObject main = jsonObject.getJSONObject("main");

                double temperature = main.getDouble("temp");
                double humidity = main.getDouble("humidity");
                double windSpeed = jsonObject.getJSONObject("wind").getDouble("speed");

                String description = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                String iconCode = jsonObject.getJSONArray("weather").getJSONObject(0).getString("icon");

                /**String resourceName = "ic_" + iconCode;
                int resId = getResources().getIdentifier(resourceName,"drawable", getPackageName());
                weatherIcon.setImageResource(resId);**/

                // Build the icon URL from the icon code
                String iconUrl = "http://openweathermap.org/img/wn/" + iconCode + "@2x.png";

                // Use Glide to load the icon dynamically
                Glide.with(this)
                        .load(iconUrl)  // Load the image from the URL
                        .placeholder(R.drawable.ic_01d)  // Show a placeholder image while loading
                        .error(R.drawable.error_icon)  // Show an error image if loading fails
                        .into(weatherIcon);  // Set it into your ImageView

                cityNameText.setText(jsonObject.getString("name"));
                temperatureText.setText(String.format("%.0f°", temperature));
                humidityText.setText(String.format("%.0f°", humidity));
                windText.setText(String.format("%.0f", windSpeed));
                descriptionText.setText(description);


            }catch (JSONException e){

                e.printStackTrace();

            }
        }

    }
}