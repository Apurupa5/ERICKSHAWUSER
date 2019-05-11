package com.iiitd.apurupa.mcproject.bookmyrickshaw;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import org.json.simple.parser.JSONParser;

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
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

public class TrialMapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button msearchButton;
    private Button mestimateButton;
    private Button mrideNowButton;
    private TextView mestimateTextView;
    private BroadcastReceiver broadcastReceiver;
    private Intent serviceintent;
    public static final String mypreference = "mypref";
    float distance=0;
    private ProgressDialog pDialog,pDialog1,pDialog2;
    JSONParser jsonParser = new JSONParser();
    private ShowMessage toast=new ShowMessage();
    private Handler mHandler = new Handler();
    int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;
    int TimeCounter=0;
    private static String url_get_driverlocation = "http://192.168.58.165/mc/available.php";
    private static String url_push_notification="http://192.168.58.165/mc/push_notification.php";
    private static String url_get_ridestatus="http://192.168.58.165/mc/ride_status.php";
    private static String url_check_journeystatus="http://192.168.58.165/mc/journey_status.php";
    final Timer t=new Timer();
    private boolean timer_status=true;
    String accepted_status="0";
    private DrawerLayout mdrawerlayout;
    private ActionBarDrawerToggle mactiontoggle;
    private String journey_status;
    private NavigationView mnavigationview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trial_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mestimateTextView= (TextView)findViewById(R.id.estimatetextView);
        mestimateButton=(Button)findViewById(R.id.estimateButton);
        mdrawerlayout=(DrawerLayout)findViewById(R.id.drawerlayout);
        mnavigationview=(NavigationView)findViewById(R.id.navigationview);
        mactiontoggle=new ActionBarDrawerToggle(this,mdrawerlayout,R.string.open,R.string.close);
        mdrawerlayout.addDrawerListener(mactiontoggle);
        mactiontoggle.syncState();
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mnavigationview.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch(item.getItemId())
                {
                    //Logout Clicked
                    case R.id.logout: toast.showmessage(getApplicationContext(),"Logout Clicked");
                        SharedPreferences preferences = getSharedPreferences(mypreference,
                                Context.MODE_PRIVATE);

                        SharedPreferences.Editor edt = preferences.edit();
                        edt.putBoolean("isloggedin",false);
                        edt.apply();
                        edt.commit();
                        Intent i1=new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(i1);
                        break;
                    case R.id.profile:

                        Intent i2=new Intent(getApplicationContext(),ViewProfileActivity.class);
                        startActivity(i2);
                        break;

                    case R.id.ridehistory:

                        Intent i3=new Intent(getApplicationContext(),RideHistoryActivity.class);
                        startActivity(i3);
                        break;
                }
                return false;
            }
        });
        mestimateButton.setEnabled(false);

    }

@Override
public boolean onOptionsItemSelected(MenuItem item)
{
    if(mactiontoggle.onOptionsItemSelected(item)){

        return true;
    }
    return super.onOptionsItemSelected(item);

}



    private void toCallTimer() {

        final long period =10000;
       // t.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, period);
      final  Timer t1=new Timer();
//        if(TimeCounter==10 || accepted_status.equals("1")) {
//            Log.d("toCallTimer","Not Accepted");
//            Intent i =new Intent(this,Activity1.class);
//            startActivity(i);
//
//             return;
//            }

        t1.scheduleAtFixedRate(new TimerTask() {
            int I=10;
            @Override
            public void run() {
                if(TimeCounter==I)
                {
                   t1.cancel();
                    return;
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
    class TimeDisplayTimerTask extends TimerTask {

        int I=10;
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {

                @Override
                public void run() {
                    if(TimeCounter==I)
                    {
                        Log.d("TimeCounter","Not Accepted");
//                        if(timer_status==true) {
//                            toast.showmessage(getApplicationContext(), "Not Accepted");
//                            TimeCounter = 0;
//                            timer_status=false;
//                            // t.cancel();
//                        }
                        t.cancel();
                      return;
                    }
                    if(isNetworkConnected()) {
                        String status = "available";
                        if(accepted_status.equals("0")){ new getridestatus().execute();  }
                        Log.d("timer status",accepted_status);

                        if(accepted_status.equals("1")){ Log.d("In if timer status",accepted_status);t.cancel();
                             return;
                        }
                        TimeCounter++;
                        Log.d("timer status",String.valueOf(TimeCounter));
                    }
                }

            });
        }


    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    ArrayList<String> latlist=intent.getExtras().getStringArrayList("latitude");
                    ArrayList<String> lnglist=intent.getExtras().getStringArrayList("longitude");
                    mMap.clear();
                    Log.d("INtent",intent.getExtras().getStringArrayList("latitude").get(0));
                    Log.d("latlist:",latlist.get(0));
                    for(int i=0;i<latlist.size();i++)
                    {
                        double latitude=Double.parseDouble(latlist.get(i));
                        double longitude=Double.parseDouble(lnglist.get(i));

                        Log.d("Before Driver","GE");
                        Log.d("Driver", String.valueOf(latitude));
                        Log.d("After Driver","GE");
                        LatLng iiitd = new LatLng(latitude,longitude);

                         mMap.addMarker(new MarkerOptions().position(iiitd).title("Marker in IIITD"));

                        mMap.moveCamera(CameraUpdateFactory.newLatLng(iiitd));
                    }




                   // moveToCurrentLocation(iiitd);


                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("In Trail Maps Activity","Service");
    //  if(broadcastReceiver != null){
      // unregisterReceiver(broadcastReceiver);
     //stopService(serviceintent);
     //}
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;


        LatLng iiitd = new LatLng(28.5473,77.2732);
        mMap.addMarker(new MarkerOptions().position(iiitd).title("Marker in IIITD"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(iiitd));
        moveToCurrentLocation(iiitd);
    }

    private void moveToCurrentLocation(LatLng currentLocation)
    {
       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation,15));
        // Zoom in, animating the camera.
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);


    }
    public void estimate(View view)
    {
      float km=distance/1000;
        float cost=km*30;

        mestimateTextView.setText( "  Rs :  " + Float.toString(cost));

    }

    public void onRideClick(View view)
    {
        EditText sLocation = (EditText)this.findViewById(R.id.MapsDestEditText);
        String location = sLocation.getText().toString();
        mMap.clear();
        List<android.location.Address> addressList= null;

        if (location != null || location.length()>0) {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            if(geocoder.isPresent()) {
                Log.d("Service:","Service Present");
             //   Toast.makeText(getApplicationContext(), "Service Present" ,Toast.LENGTH_LONG).show();
                try {
                    Log.d("Hello:","Hello");
                    addressList = geocoder.getFromLocationName(location, 1);
                    Log.d("Hello1:","Hello");
                    Log.d("addressList:",addressList.toString());


                } catch (IOException e) {
                    Log.d("IN catch:","Error");
                    e.printStackTrace();
                }
             try {
                 android.location.Address address = addressList.get(0);
                 String locality = address.getLocality();
                 //    Toast.makeText(getApplicationContext(), locality, Toast.LENGTH_LONG).show();
                 double latitude = address.getLatitude();
                 double longitude = address.getLongitude();
                 LatLng dstlng= new LatLng(latitude, longitude);
                 mMap.addMarker(new MarkerOptions().position(dstlng).title("Marker"));
                 mMap.animateCamera(CameraUpdateFactory.newLatLng(dstlng));
                 Location locationA = new Location("point A");

                 locationA.setLatitude(28.5473);
                 locationA.setLongitude(77.2732);

                 Location locationB = new Location("point B");

                 locationB.setLatitude(latitude);
                 locationB.setLongitude(longitude);

                 distance = locationA.distanceTo(locationB);
                 Toast.makeText(this,"Distance is: "+distance+" meters ",Toast.LENGTH_SHORT).show();
                 mestimateButton.setEnabled(true);
                 SharedPreferences prefs = getSharedPreferences(mypreference,
                         Context.MODE_PRIVATE);
                 String useremail=prefs.getString("email","");
//                 new checkjourneystatus().execute(useremail);
//                 Log.d("JOURNEY STATUS",journey_status);
//                if(journey_status.equals("started"))
//                 {
//                     new AlertDialog.Builder(TrialMapsActivity.this)
//                             .setTitle("You Cannot Make a Ride Request")
//                             .setMessage("You are already on a ride.Try Again Later")
//                             .show();
//                     return;
//                 }

                 new sendPushNotification().execute(useremail,"IIITD",location);
                 Intent i=new Intent(this,Activity1.class);
                 startActivity(i);

                }
                catch(IndexOutOfBoundsException e)
                {
                    Toast.makeText(this,"Please enter a Valid Location",Toast.LENGTH_SHORT).show();
                }
             catch (NullPointerException e)
             {
         Toast.makeText(this,"Please enter  Valid Location",Toast.LENGTH_SHORT).show();
}
                }
            else
            {
                Log.d("Service:","Service Not Present");
                Toast.makeText(getApplicationContext(), "Service N0t Present", Toast.LENGTH_LONG).show();
            }
          /*  try {

                addressList = geocoder.getFromLocationName(location, 1);
            } catch (IOException e) {

                e.printStackTrace();
            }
*/

        }

    }

   /* public static float distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;
    }*/
   /* public void onSearch(View view)
    {
       // EditText sLocation = (EditText)getActivity().findViewById(R.id.MapsDestEditText);

        EditText mdestination=(EditText)findViewById(R.id.MapsDestEditText);
        String location=mdestination.getText().toString();
        List<android.location.Address> addresslist=null;
        Log.d("location",location);
        if(location!=null||!location.equals(""))
        {
            Geocoder geocoder = new Geocoder(getApplicationContext());
            try {
                addresslist= geocoder.getFromLocationName(location,1);
                Log.d("Inside try",addresslist.toString());
            } catch (IOException e) {
                Log.d("After try",addresslist.toString());
                e.printStackTrace();
            }
            android.location.Address addr=addresslist.get(0);
            LatLng dstlng= new LatLng(addr.getLatitude(), addr.getLongitude());
            mMap.addMarker(new MarkerOptions().position(dstlng).title("Marker"));
            mMap.animateCamera(CameraUpdateFactory.newLatLng(dstlng));
        }
    }*/
    private void setUpMapIfNeeded()
    {
        if(mMap==null)
        {
         //  mMap=((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMapAsync();
        }
    }
    public void riderequest(View view) {


    }
/*
    @Override
    public void onClick(View view) {
        onSearch(view);
    }*/


    //Backend code
    class sendPushNotification extends AsyncTask<String, String, String> {

        /**
         Show Progress Dialog
         * */
        private String authstatus="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(TrialMapsActivity.this);
            pDialog.setMessage("Sending Request..");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        /**
         * Creating account
         * */
        protected String doInBackground(String... args) {

            String useremail = args[0];
            String pickup = args[1];
            String dest = args[2];

            // Building Parameters
            List<NameValuePair> params = new ArrayList<NameValuePair>();

            params.add(new BasicNameValuePair("email", useremail));

            params.add(new BasicNameValuePair("pickup", pickup));

            params.add(new BasicNameValuePair("destination", dest));


            URL url = null;
            HttpURLConnection conn = null;
            try {
                url = new URL(url_push_notification);
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
            JSONObject jsonresponse = null;
            String json_string = null;
            try {
                responseCode = conn.getResponseCode();
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        json_string += line;

                    }
                    Log.d("PUSH response", json_string);


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
            pDialog.dismiss();
            Log.d("POST PUSH",response);

        }

    }

    class getridestatus extends AsyncTask<String, String, String> {

        /**
         Show Progress Dialog
         * */
        private String authstatus="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog1 = new ProgressDialog(TrialMapsActivity.this);
            pDialog1.setMessage("Waiting...");
            pDialog1.setIndeterminate(false);
            pDialog1.setCancelable(true);
            pDialog1.show();
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
                    new AlertDialog.Builder(TrialMapsActivity.this)
                            .setTitle("Ride Accepted")
                            .setMessage("Your Rickshaw is on your way"+"\n"+"Driver name:"+accepted_name+"\n"+"Mobile:"+accepted_phone)
                            .show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog1.dismiss();
        }

    }

    class checkjourneystatus extends AsyncTask<String, String, String> {

        /**
         Show Progress Dialog
         * */
        private String authstatus="";
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog2 = new ProgressDialog(TrialMapsActivity.this);
            pDialog2.setMessage("Waiting...");
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(true);
            pDialog2.show();
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
                url = new URL(url_check_journeystatus);
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
                    journey_status=jsonresponse.getString("status");


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            pDialog2.dismiss();
        }

    }
}
