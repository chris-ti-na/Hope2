package com.thebest.christina.hope2.activities;

import android.app.Activity;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import com.thebest.christina.hope2.R;
import com.thebest.christina.hope2.database.FrameDao;
import com.thebest.christina.hope2.events.Event;
import com.thebest.christina.hope2.events.EventBus;
import com.thebest.christina.hope2.events.InternalListener;
import com.thebest.christina.hope2.events.SendFrameEvent;
import com.thebest.christina.hope2.events.SignalLoadedEvent;
import com.thebest.christina.hope2.model.Frame;
import com.thebest.christina.hope2.service.GeoSignalService;
import com.thebest.christina.hope2.service.HttpService;

import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {

    public Context _context;
    public TextView _locationTextView;
    public TextView _frameTextView;
    public TextView _excText;

    public GeoSignalService _geoSignalService;
    public HttpService _httpService;
    public EventBus _eventBus;
    public LocationManager _locationManager;
    public TelephonyManager _telephonyManager;
    private FrameDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _context = this;
        _locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        _telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        _eventBus = new EventBus();

        dao = new FrameDao(this);

        _locationTextView = (TextView) findViewById(R.id.geoDataView);
        _frameTextView = (TextView) findViewById(R.id.frameText);
        _excText = (TextView) findViewById(R.id.excText);

        _eventBus.addEventListener("SignalLoadedEvent", new InternalListener() {
            @Override
            public void perform(Event a) {
                SignalLoadedEvent event = (SignalLoadedEvent) a;
                dao.createFrame(new Frame(
                        event._latitude,
                        event._longitude,
                        event._time,
                        event._signalStrength,
                        event._cellInfo,
                        event._networkProvider));
                String time = String.format("%1$tF %1$tT", new Date(event._time));
                String textViewString = "Coordinates:\nlatitude = " + event._latitude
                        + "\nlongitude = " + event._longitude
                        + "\ntime = " + time
                        + "\nsignal = " + event._signalStrength
                        + "\ncellInfo = " + event._cellInfo
                        + "\nnetwork provider = " + event._networkProvider;
                _locationTextView.setText(textViewString);
                List<Frame> frames = dao.getAllFrames();
                Frame frame = frames.get(frames.size() - 1);
                String frameTextString = "Coordinates:\nlatitude = " + frame._latitude
                        + "\nlongitude = " + frame._longitude
                        + "\ntime = " + String.format("%1$tF %1$tT", new Date(frame._time))
                        + "\nsignal = " + frame._signalStrength
                        + "\ncellInfo = " + frame._cellInfo
                        + "\nnetwork provider = " + frame._networkProvider;
                _frameTextView.setText(frameTextString);
                _eventBus.dispatchEvent(new SendFrameEvent(new Frame(
                        event._latitude,
                        event._longitude,
                        event._time,
                        event._signalStrength,
                        event._cellInfo,
                        event._networkProvider)));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        _geoSignalService = new GeoSignalService(_locationManager, _telephonyManager, _eventBus);
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            _httpService = new HttpService(_eventBus);
        } else {
            System.out.println("Network is unable to connect.");
            _excText.setText("Network is unable to connect.");
        }
    }
}