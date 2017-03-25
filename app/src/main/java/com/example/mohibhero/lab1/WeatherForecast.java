package com.example.mohibhero.lab1;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;



public class WeatherForecast extends AppCompatActivity {

    private ForecastQuery forecastQuery;

    private ProgressBar progressWeatherBar;

    private TextView currentTemperature;
    private TextView minimumTemperature;
    private TextView maximumTemperature;

    private ImageView weatherStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_forecast);

        progressWeatherBar = (ProgressBar) findViewById(R.id.progressWeatherBar);

        currentTemperature = (TextView) findViewById(R.id.currentTemperature);
        minimumTemperature = (TextView) findViewById(R.id.minimumTemperature);
        maximumTemperature = (TextView) findViewById(R.id.maximumTemperature);

        weatherStatus = (ImageView) findViewById(R.id.weatherImage);

        forecastQuery = new ForecastQuery();
        forecastQuery.execute();
    }

    private class ForecastQuery extends AsyncTask<String, Integer, String> {

        private String currentTemperature2;
        private String minimumTemperature2;
        private String maximumTemperature2;
        private String iconName;
        private String iconFileName;
        private Bitmap weatherImage;

       // private static final String OttawaWeatherURL = "http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric";

        private boolean fileExists(String fileName){
            File file = getBaseContext().getFileStreamPath(fileName);
            return file.exists();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            weatherStatus.setImageBitmap(weatherImage);

            String currentText = currentTemperature2 + "\u2103";
            String minimumText = minimumTemperature2 + "\u2103";
            String maximumText = maximumTemperature2 + "\u2103";

            currentTemperature.setText(currentText);
            minimumTemperature.setText(minimumText);
            maximumTemperature.setText(maximumText);

            progressWeatherBar.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            progressWeatherBar.setVisibility(View.VISIBLE);
            progressWeatherBar.setProgress(values[0]);

            FileInputStream fis;
            Log.i("Info", iconFileName);
            if(fileExists(iconFileName)) {
                Log.i("Info", iconFileName + " was found");
                try {
                    File f = getFileStreamPath(iconFileName);
                    fis = new FileInputStream(f);
                    weatherImage = BitmapFactory.decodeStream(fis);
                }
                catch (FileNotFoundException e) {
                    Log.e("Error", "Could not find image " + iconFileName);
                }
            }
        }

        @Override
        protected String doInBackground(String... params) {
            URL url;
            XmlPullParser parser;
            int progress = 0;

            try {
                url = new URL("http://api.openweathermap.org/data/2.5/weather?q=ottawa,ca&APPID=d99666875e0e51521f0040a3d97d0f6a&mode=xml&units=metric");
                HttpURLConnection conn;
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();

                parser = Xml.newPullParser();
                parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                parser.setInput(conn.getInputStream(), null);
                parser.nextTag();

                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                    String name = parser.getName();
                    if (name.equals("temperature")) {
                        currentTemperature2 = parser.getAttributeValue(null, "value");
                        minimumTemperature2 = parser.getAttributeValue(null, "min");
                        maximumTemperature2 = parser.getAttributeValue(null, "max");
                    } else if(name.equals("weather")) {
                        iconName = "11d";//parser.getAttributeValue(null, "icon");
                    }

                    progress += 1;
                    publishProgress(progress);
                }

            } catch (XmlPullParserException | IOException e) {
                e.printStackTrace();
            }

            publishProgress(100);

            String imageUrl = "http://openweathermap.org/img/w/" + iconName + ".png";
            iconFileName =  iconName + ".PNG";

            Bitmap image = HTTPUtils.getImage(imageUrl);
            FileOutputStream outputStream;
            try {
                outputStream = openFileOutput( iconFileName, Context.MODE_PRIVATE);
                if(image != null)
                    image.compress(Bitmap.CompressFormat.PNG, 80, outputStream);

                outputStream.flush();
                outputStream.close();
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
