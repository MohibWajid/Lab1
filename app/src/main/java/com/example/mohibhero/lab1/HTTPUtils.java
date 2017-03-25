package com.example.mohibhero.lab1;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by MohibHero on 3/14/17.
 */


class HTTPUtils {
    public static Bitmap getImage(URL url) {
        HttpURLConnection connectionURL = null;
        try {
            connectionURL = (HttpURLConnection) url.openConnection();
            connectionURL.connect();
            int responseCode = connectionURL.getResponseCode();
            if (responseCode == 200) {
                return BitmapFactory.decodeStream(connectionURL.getInputStream());
            } else
                return null;
        } catch (Exception e) {
            return null;
        } finally {
            if (connectionURL != null) {
                connectionURL.disconnect();
            }
        }
    }
    public static Bitmap getImage(String urlString) {
        try {
            URL url = new URL(urlString);
            return getImage(url);
        } catch (MalformedURLException e) {
            return null;
        }
    }
}
