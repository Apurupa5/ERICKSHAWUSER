package com.iiitd.apurupa.mcproject.bookmyrickshaw;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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

public class Activity1 extends AppCompatActivity {
    int TimeCounter=0;
    String accepted_status="0";
    public static final String mypreference = "mypref";
    float distance=0;
    private ProgressDialog pDialog,pDialog1;
    private static String url_get_ridestatus="http://192.168.58.165/mc/ride_status.php";
    private ShowMessage toast=new ShowMessage();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);
        TimeCounter=0;
        accepted_status="0";
        pDialog1 = new ProgressDialog(Activity1.this);
            pDialog1.setMessage("Waiting...");
            pDialog1.setIndeterminate(false);
            pDialog1.setCancelable(true);
            pDialog1.show();
        toCallTimer();



        if(accepted_status.equals("0") && TimeCounter==10)
               {
                   Log.d("TS","Not Accepted");
                     toast.showmessage(this,"Not Accepted");
                 }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
    private void toCallTimer() {

        final long period =10000;
        // t.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, period);
        final Timer t1=new Timer();
        t1.scheduleAtFixedRate(new TimerTask() {
            int I=10;
            @Override
            public void run() {
                if(TimeCounter==I)
                {
                    t1.cancel();
                    Log.d("INSIDE CANCEL","Not Accepted");

                    showToast("Not Accepted");
                    //return;
                }
                if(isNetworkConnected()) {
                    String status = "available";
                    if(accepted_status.equals("0")){ new getridestatus().execute();}
                    Log.d("timer status",accepted_status);
                    if(accepted_status.equals("1")){ Log.d("In if timer status",accepted_status);
                        t1.cancel();
                        return; }
                    TimeCounter++;
                    Log.d("timer status",String.valueOf(TimeCounter));
                }



            }


        },0,period);
        Log.d("TIMER RETURN","EGE");

    }

        public void showToast(final String toast)
        {
            runOnUiThread(new Runnable() {
                public void run()
                {
                 //   Toast.makeText(Activity1.this, toast, Toast.LENGTH_SHORT).show();
                    new AlertDialog.Builder(Activity1.this)
                            .setTitle("Ride Not Accepted")
                            .setMessage("Please Try Again")
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finish();
                                            dialog.cancel();
                                        }
                                    })
                            .show();

                }
            });
        }

    class getridestatus extends AsyncTask<String, String, String> {

        /**
         Show Progress Dialog
         * */
        private String authstatus="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            pDialog1 = new ProgressDialog(Activity1.this);
//            pDialog1.setMessage("Waiting...");
//            pDialog1.setIndeterminate(false);
//            pDialog1.setCancelable(true);
//            pDialog1.show();
        }

        /**
         * Creating account
         * */
        protected String doInBackground(String... args) {



            SharedPreferences prefs = getSharedPreferences(mypreference,
                    Context.MODE_PRIVATE);
            String useremail=prefs.getString("email","");
            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("email", useremail));

            URL url = null;
            HttpURLConnection conn = null;
            try {
                url = new URL(url_get_ridestatus);
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
                    Log.d("ride response", json_string);


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
            JSONObject jsonresponse = null;
            try {
                jsonresponse =new JSONObject(response);
                if(jsonresponse.length()!=0)
                {
                    accepted_status=jsonresponse.getString("accepted");
                    String accepted_demail=jsonresponse.getString("by_email");
                    String accepted_name=jsonresponse.getString("by_name");
                    String accepted_phone=jsonresponse.getString("by_mobile");
                    pDialog1.dismiss();
                    new AlertDialog.Builder(Activity1.this)
                            .setTitle("Ride Accepted")
                            .setMessage("Your Rickshaw is on your way"+"\n"+"Driver name:"+accepted_name+"\n"+"Mobile:"+accepted_phone)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            finish();
                                            dialog.cancel();
                                        }
                                    })
                            .show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}
