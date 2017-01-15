package com.example.hyphen.airconditioner;

import android.os.AsyncTask;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by hyphen on 18/12/16.
 */



public class CommandSender extends AsyncTask<String, Void, Void> {

    protected Void doInBackground(String... url){
        HttpURLConnection c = null;
        try {
            URL u = new URL(url[0]);
            c = (HttpURLConnection) u.openConnection();
            c.setRequestMethod("GET");
            c.setRequestProperty("Content-length", "0");
            c.setUseCaches(false);
            c.setAllowUserInteraction(false);
//            c.setConnectTimeout(timeout);
//            c.setReadTimeout(timeout);
            c.connect();
            int status = c.getResponseCode();
            switch (status) {
                case 200:
                    InputStream is = c.getInputStream();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    while (-1 != (len = is.read(buffer))) {
                        baos.write(buffer, 0, len);
                        baos.flush();
                    }
                    System.out.println(baos.toString());
                    if(MainActivity.flag) {
                        MainActivity.httpResultStr = new String(baos.toString());
                        System.out.println(baos.toString());
                        MainActivity.flag = false;
                    }
                    System.out.println("HTTP 200");
                    break;
                case 201:
                    System.out.println("HTTP 201");
                    return null;
            }
        } catch (MalformedURLException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (c != null) {
                try {
                    c.disconnect();
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return null;
    }

    protected void onProgressUpdate(Void... progress) {
    }

    protected void onPostExecute(Void result) {
        System.out.println("Finished!");
    }

}
