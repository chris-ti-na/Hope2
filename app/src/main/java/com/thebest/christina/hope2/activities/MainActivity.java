package com.thebest.christina.hope2.activities;

import android.app.Activity;
import android.content.Context;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.widget.TextView;

import com.thebest.christina.hope2.NetworkStateReceiver;
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

import java.util.List;

public class MainActivity extends Activity implements NetworkStateReceiver.NetworkStateReceiverListener {

    public Context _context;
    public TextView _locationTextView;
    public TextView _excText;

    public GeoSignalService _geoSignalService;
    public HttpService _httpService;
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

        _locationTextView = (TextView) findViewById(R.id.geoDataView);
        _excText = (TextView) findViewById(R.id.excText);



        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

        _eventBus.addEventListener("SignalLoadedEvent", new InternalListener() {
            @Override
            public void perform(Event a) {
                SignalLoadedEvent event = (SignalLoadedEvent) a;
                dao.createFrame(new Frame(
                        event._latitude, event._longitude, event._time, event._asuSignalStrength, event._barSignalStrength, event._dbmSignalStrength, event._cellInfo, event._networkProvider));
                _locationTextView.setText(String.valueOf(event._asuSignalStrength));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        _geoSignalService = new GeoSignalService(_locationManager, _telephonyManager, _eventBus);
//        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        if (networkInfo != null && networkInfo.isConnected()) {
//            _excText.setText("Connection !");
//            _httpService = new HttpService(_eventBus);
//        } else {
//            System.out.println("Network is unable to connect.");
//            _excText.setText("Network is unable to connect.");
//        }
    }

    public void networkAvailable() {
        _excText.setText("Connection !");
        _httpService = new HttpService(_eventBus);
        List<Frame> frames = dao.getAllFrames();
        if(frames.size() > 0) {
            for (Frame frame : frames) {
                _excText.setText(String.valueOf(frame._asuSignalStrength));
                _eventBus.dispatchEvent(new SendFrameEvent(frame));
            }
        }
    }

    @Override
    public void networkUnavailable() {
        _excText.setText("Network is unable to connect.");
    }
}