package com.kimjinhwan.android.cookieclient;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(R.id.textView);

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                return urlNetworking();
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                textView.setText(result);
            }
        }.execute();

    }

    private final String COOKIE_HEADER = "Set-Cookie";


    public String urlNetworking() {

        try {
            URL url = new URL("http://192.168.1.4:7890/");
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            InputStream is = httpConn.getInputStream();
            InputStreamReader isreader = new InputStreamReader(is);
            BufferedReader bfreader = new BufferedReader(isreader);

            Map<String, List<String>> headers = httpConn.getHeaderFields();

            Iterator<String> iterator = headers.keySet().iterator();
            Log.e("HEADERS", headers.toString());
            while (iterator.hasNext()) {
                Log.e("HEADERS", "head="+iterator.next());
            }
            for(String head : headers.keySet()) {
                if (head != null) {
                    Log.e("HEADER", head);
                }
            }
                List<String> cookies = headers.get("Set-Cookies");



            //SharedPreference를 사용하여 쿠키를 넣을 때!
            //SharedPreferences sharedPreferences = this.getSharedPreferences("MySharedPreference", MODE_PRIVATE);
            //SharedPreferences.Editor editor = sharedPreferences.edit();
            //editor.putString("cookie", headers.get("Set-Cookie").get(0));


            CookieHandler.setDefault(new CookieManager());
            CookieManager cookieManager = (CookieManager) CookieManager.getDefault();
            URI uri= new URI(url.toString());
            cookieManager.getCookieStore().add(uri, HttpCookie.parse(cookies.get(0)).get(0));
            httpConn.addRequestProperty("Cookies", cookieManager.getCookieStore().get(uri).get(0).toString());

                return bfreader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return "Error";
    }

}
