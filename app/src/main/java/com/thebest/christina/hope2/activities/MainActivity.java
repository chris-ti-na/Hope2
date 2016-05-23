package com.thebest.christina.hope2.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.PopupWindow;

import com.thebest.christina.hope2.R;
import com.thebest.christina.hope2.database.FrameDao;
import com.thebest.christina.hope2.events.Event;
import com.thebest.christina.hope2.events.EventBus;
import com.thebest.christina.hope2.events.InternalListener;
import com.thebest.christina.hope2.events.SignalLoadedEvent;
import com.thebest.christina.hope2.model.Frame;
import com.thebest.christina.hope2.service.GeoSignalService;
import com.thebest.christina.hope2.service.NetworkStateReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends Activity implements NetworkStateReceiver.NetworkStateReceiverListener {

    public Context _context;
    Button gowebButton;
    Button sysinfoButton;

    public GeoSignalService _geoSignalService;
    public EventBus _eventBus;
    public LocationManager _locationManager;
    public TelephonyManager _telephonyManager;
    private FrameDao dao;

    private NetworkStateReceiver networkStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _context = this;
        _locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        _telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        _eventBus = new EventBus();

        dao = new FrameDao(this);

        sysinfoButton = (Button) findViewById(R.id.sysinfo);
        gowebButton = (Button) findViewById(R.id.goweb);


        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        sysinfoButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                LayoutInflater inflater = (LayoutInflater)_context.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                PopupWindow pw = new PopupWindow(
                        inflater.inflate(R.layout.info_popup, null, false),
                        100,
                        100,
                        true);
                // The code below assumes that the root container has an id called 'main'
                pw.showAtLocation(findViewById(R.id.sysinfo), Gravity.CENTER, 0, 0);
            }
        });


        gowebButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://gracheva.geosamara.ru/signalmap_war/")));
            }
        });

        _eventBus.addEventListener("SignalLoadedEvent", new InternalListener() {
            @Override
            public void perform(Event a) {
                SignalLoadedEvent event = (SignalLoadedEvent) a;
                dao.createFrame(new Frame(
                        event._latitude, event._longitude, event._time, event._asuSignalStrength, event._barSignalStrength, event._dbmSignalStrength, event._cellInfo, event._networkProvider));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        _geoSignalService = new GeoSignalService(_locationManager, _telephonyManager, _eventBus);
    }

    private class SendDataTask extends AsyncTask<String, Void, Void> {

        String respCode;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... arg) {
            HttpURLConnection httpURLConnection = null;
            try {
                URL url = new URL("http://gracheva.geosamara.ru/signalmap_war/GeoMinerServlet");
                httpURLConnection= (HttpURLConnection) url.openConnection();
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                httpURLConnection.setReadTimeout(10000);
                httpURLConnection.setConnectTimeout(15000);
                httpURLConnection.connect();

                OutputStream out = httpURLConnection.getOutputStream();
                PrintWriter writer = new PrintWriter(out, true);

                writer.println(getJsonData());

                //DON'T DELETE THIS LINE

                respCode += " rc: " + httpURLConnection.getResponseCode() + "  " + httpURLConnection.getResponseMessage();
                dao.deleteAllFrames();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

        private JSONObject getJsonData(){
            JSONObject jsonObject = new JSONObject();
            try {

                JSONArray jsonArray = new JSONArray();

                List<Frame> frames = dao.getAllFrames();
                if(frames.size() > 0) {
                    for (Frame frame : frames) {
                        respCode = "frame " + String.valueOf(frame._asuSignalStrength);
                        jsonArray.put(frameToJson(frame));
                    }
                }

                jsonObject.put("signals", jsonArray);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return jsonObject;
        }

        private  JSONObject frameToJson(Frame f){
            JSONObject json = new JSONObject();
            try {
                json.put("latitude", f._latitude);
                json.put("longitude", f._longitude);
                json.put("asuSignalStrength", f._asuSignalStrength);
                json.put("barSignalStrength", f._barSignalStrength);
                json.put("dbmSignalStrength", f._dbmSignalStrength);
                json.put("cellInfo", f._cellInfo);
                json.put("networkProvider", f._networkProvider);
                json.put("time", f._time);
                json.put("model", android.os.Build.MODEL);
                json.put("producer", android.os.Build.MANUFACTURER);
                json.put("osVesion", Build.VERSION.SDK_INT);
                json.put("radioVersion", android.os.Build.getRadioVersion());
                return json;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return json;
        }
    }

    public void networkAvailable() {
        SendDataTask sendDataTask = new SendDataTask();
        sendDataTask.execute();
    }

    @Override
    public void networkUnavailable() {
    }

}