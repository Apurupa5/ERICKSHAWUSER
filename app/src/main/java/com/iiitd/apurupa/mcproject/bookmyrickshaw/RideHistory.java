package com.iiitd.apurupa.mcproject.bookmyrickshaw;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

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
import java.util.Date;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by NB VENKATESHWARULU on 11/28/2016.
 */
public class RideHistory {
    private static String url_ride_history = "http://192.168.58.165/mc/ride_history.php";
    org.json.JSONArray jsonresponsearray =new org.json.JSONArray();
    public  ArrayList<RideList> taskList = new ArrayList<RideList>();
    public ArrayList<RideList> getAllRides(String useremail) {
       Log.d("RH","IngetAllRides");
        new getridehistory().execute(useremail);
        Log.d("hi1234",useremail);
        Log.d("TASKLIST",String.valueOf(taskList.size()));
        return taskList;
    }
    //Get Ride details
    class getridehistory extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Creating account
         * */
        protected String doInBackground(String... args) {



           String useremail=args[0];
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("email", useremail));

            URL url = null;
            HttpURLConnection conn = null;
            try {
                url = new URL(url_ride_history);
                Log.d("url", String.valueOf(url));
                conn = (java.net.HttpURLConnection) url.openConnection();
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


            int responseCode = 0;

            String json_string ="";
            try {
                responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        json_string += line;

                    }
                    Log.d("RIDE HISTORY", json_string);


                } else {
                    json_string = "";

                }

                return json_string;
            } catch (IOException e) {
                e.printStackTrace();
            }

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
            Log.d("GET QUERY",result.toString());
            return result.toString();
        }
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String response) {
            // dismiss the dialog once done

            Log.d("POST PUSH",response);
            try {
                jsonresponsearray=new JSONArray(response);
                Log.d("RIDEJSON",jsonresponsearray.toString());
                for(int i=0;i<jsonresponsearray.length();i++)
                {
                    JSONObject json=jsonresponsearray.getJSONObject(i);
                    Log.d("RIDEJSONOBJ",json.toString());
                    RideList taskitem = new RideList();
                   // taskitem.setRidedate((Date) json.get("Date"));
                    taskitem.setPickup(json.getString("pickup"));
                    taskitem.setDestination(json.getString("destination"));
                    taskitem.setDrivername(json.getString("driver_name"));
                    taskList.add(taskitem);
                    Log.d("TASKLISTITEM",taskList.get(0).getDrivername().toString());
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.d("TASKLISTITEMOUT",taskList.size()+"");
        }
    }
}

