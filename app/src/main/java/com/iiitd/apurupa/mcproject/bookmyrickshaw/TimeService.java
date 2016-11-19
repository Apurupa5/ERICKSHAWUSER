package com.iiitd.apurupa.mcproject.bookmyrickshaw;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

public class TimeService extends Service {
    // constant
    public static final long NOTIFY_INTERVAL = 5 * 1000; // 10 seconds

    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    // timer handling
    private Timer mTimer = null;
    private ProgressDialog pDialog;
    private static String url_get_driverlocation = "http://192.168.58.165/mc/available.php";
    public ArrayList<String> longitudelist=new ArrayList<String>();
    public ArrayList<String> latitudelist=new ArrayList<String>();
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        // cancel if already existed
        if (mTimer != null) {
            mTimer.cancel();
        } else {
            // recreate new
            mTimer = new Timer();
        }
        // schedule task
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
    }

    class TimeDisplayTimerTask extends TimerTask {

        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {

                    String status="available";
                    new getdriverslocation().execute(status);
                    Intent I = new Intent("location_update");
                    I.putStringArrayListExtra("longitude",longitudelist);
                    I.putStringArrayListExtra("latitude",latitudelist);
                    sendBroadcast(I);

                }

            });
        }


    }
    class getdriverslocation extends AsyncTask<String, String, String> {

        /**
         Show Progress Dialog
         * */
        private String authstatus="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog = new ProgressDialog(TrialMapsActivity.this);
//            pDialog.setMessage("Getting Details..");
//            pDialog.setIndeterminate(false);
//            pDialog.setCancelable(true);
//            pDialog.show();
        }

        /**
         * Creating account
         * */
        protected String doInBackground(String... args) {

            String status = args[0];


            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("availability", status));

            URL url = null;
            HttpURLConnection conn=null;
            try {
                url = new URL(url_get_driverlocation);
                Log.d("url", String.valueOf(url));
                conn= (java.net.HttpURLConnection ) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            OutputStream os = null;
            try {
                os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();

                conn.connect();

            } catch (IOException e) {
                e.printStackTrace();
            }
            String json_string="";
            //Getting response from api
            int responseCode= 0;
            JSONObject jsonresponse=null;
            org.json.JSONArray jsonresponsearray =new org.json.JSONArray();
            try {
                responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        json_string += line;

                    }
                    Log.d("Create Response in try", json_string);
                    jsonresponsearray=new org.json.JSONArray(json_string);
                    Log.d("Json Array",jsonresponsearray.toString());
//
//                    jsonresponse = new JSONObject(json_string);
//                    Log.d("json Response in array", jsonresponse.toString());
//
//                    authstatus = jsonresponse.getString("success");
//                    Log.d("AuthStatus Response", authstatus);



                }
                else {
                    json_string="";

                }
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }

            Log.d("Create Response", json_string);

            //Log.d("Json Response",jsonresponse.toString());
            return json_string;

        }
        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
        {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params)
            {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String response) {
            // dismiss the dialog once done
           // pDialog.dismiss();
            JSONObject jsonresponse=null;
            org.json.JSONArray jsonresponsearray =new org.json.JSONArray();
            try {
                latitudelist.clear();
                longitudelist.clear();
                jsonresponsearray=new JSONArray(response);
                for(int i=0;i<jsonresponsearray.length();i++)
                {
                    JSONObject json=jsonresponsearray.getJSONObject(i);
                    Double lat=Double.parseDouble(json.getString("latitude"));
                    Double lng=Double.parseDouble(json.getString("longitude"));
                    Log.d("Latitude:",lat.toString());
                    latitudelist.add(json.getString("latitude").toString());
                    longitudelist.add(json.getString("longitude").toString());
                    Log.d("In Time service",latitudelist.get(0));
                    Log.d("In Time service",json.getString("latitude").toString());


                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}