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

//        String model = android.os.Build.MODEL;
//        String device = android.os.Build.DEVICE;
//        String board = android.os.Build.BOARD;
//        String brand = android.os.Build.BRAND;
//        String fingerprint = android.os.Build.FINGERPRINT;
//        String hardware = android.os.Build.HARDWARE;
//        String manufacturer = android.os.Build.MANUFACTURER;
//        String product = android.os.Build.PRODUCT;
//        String serial = android.os.Build.SERIAL;
//        String type = android.os.Build.TYPE;
//        String radio = android.os.Build.getRadioVersion();
//
//        String frameTextString = "model: " + model + "\ndevice: " + device +
//                "\nboard: " + board + "\nbrand: " + brand +
//                "\nfingerprint: " + fingerprint + "\nhardware: " + hardware +
//                "\nmanufacturer: " + manufacturer + "\nproduct: " + product +
//                "\nserial: " + serial + "\ntype: " + type + "\nradio: " + radio;
//
//
//        _frameTextView.setText(frameTextString);

        _frameTextView.setText("place");

        _eventBus.addEventListener("SignalLoadedEvent", new InternalListener() {
            @Override
            public void perform(Event a) {
                SignalLoadedEvent event = (SignalLoadedEvent) a;

                dao.createFrame(new Frame(
                        event._latitude,
                        event._longitude,
                        event._time,
                        event._asuSignalStrength,
                        event._barSignalStrength,
                        event._dbmSignalStrength,
                        event._cellInfo,
                        event._networkProvider));
                String time = String.format("%1$tF %1$tT", new Date(event._time));
                String textViewString = "Coordinates:\nlatitude = " + event._latitude
                        + "\nlongitude = " + event._longitude
                        + "\ntime = " + time
                        + "\nsignal = " + event._asuSignalStrength
                        + "\ncellInfo = " + event._cellInfo
                        + "\nnetwork provider = " + event._networkProvider;
                _frameTextView.setText(textViewString);

                _eventBus.dispatchEvent(new SendFrameEvent(new Frame(
                        event._latitude,
                        event._longitude,
                        event._time,
                        event._asuSignalStrength,
                        event._barSignalStrength,
                        event._dbmSignalStrength,
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
            _excText.setText("Connection !");
            _httpService = new HttpService(_eventBus);
        } else {
            System.out.println("Network is unable to connect.");
            _excText.setText("Network is unable to connect.");
        }
    }
}