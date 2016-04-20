package com.thebest.christina.hope2.service;

import com.thebest.christina.hope2.events.Event;
import com.thebest.christina.hope2.events.EventBus;
import com.thebest.christina.hope2.events.InternalListener;
import com.thebest.christina.hope2.events.SendFrameEvent;
import com.thebest.christina.hope2.model.Frame;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpService {
    public EventBus _eventBus;

    public HttpService(EventBus eventBus){
        this._eventBus = eventBus;

        eventBus.addEventListener("SendFrameEvent", new InternalListener() {
            @Override
            public void perform(Event a) {
                final SendFrameEvent sendFrameEvent = (SendFrameEvent) a;
                new Thread() {
                    @Override
                    public void run() {
                        pushData(sendFrameEvent._frame);
                    }
                }.start();
            }
        });
    }


    public  void pushData(Frame f){
        HttpURLConnection httpURLConnection = null;
        try {
            URL url = new URL("http://172.20.10.5:8080/GeoMinerServlet");
            httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            httpURLConnection.setReadTimeout(10000);
            httpURLConnection.setConnectTimeout(15000);
            httpURLConnection.connect();
            JSONObject json = new JSONObject();
            json.put("latitude", f._latitude);
            json.put("longitude", f._longitude);
            json.put("signalStrength", f._signalStrength);
            json.put("cellInfo", f._cellInfo);
            json.put("networkProvider", f._networkProvider);
            json.put("time", f._time);
            PrintWriter writer = new PrintWriter(httpURLConnection.getOutputStream(), true);
            writer.println(json.toString());
            //DON'T DELETE THIS LINE
            System.out.println("" + httpURLConnection.getResponseCode());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if(httpURLConnection != null){
                httpURLConnection.disconnect();
            }
        }
    }
}
