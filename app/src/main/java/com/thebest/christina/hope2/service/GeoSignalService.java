package com.thebest.christina.hope2.service;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;

import com.thebest.christina.hope2.events.EventBus;
import com.thebest.christina.hope2.events.SignalLoadedEvent;

import java.util.List;

public class GeoSignalService implements LocationListener {

    public LocationManager _locationManager;
    public TelephonyManager _telephonyManager;
    public EventBus _eventBus;

    public GeoSignalService(LocationManager locationManager, TelephonyManager telephonyManager, EventBus eventBus) {
        this._eventBus = eventBus;
        this._locationManager = locationManager;
        this._telephonyManager = telephonyManager;
        try {
            _locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, this);
            _locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 10, this);
        }catch(SecurityException c){
            new RuntimeException("Exception");
            System.out.println("!");
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        int _signalStrength = -1;
        String cellInfoType = "UNABLE INFO";
        try {
            List<CellInfo> cellInfoList = _telephonyManager.getAllCellInfo();
            //Unavalible on Samsung S4
            if(cellInfoList !=null) {
                for (final CellInfo info : cellInfoList) {
                    if (info.isRegistered()) {
                        if (info instanceof CellInfoGsm) {
                            final CellSignalStrengthGsm gsm = ((CellInfoGsm) info).getCellSignalStrength();
                            _signalStrength = gsm.getAsuLevel();
                            cellInfoType = "GSM";
                        } else if (info instanceof CellInfoCdma) {
                            final CellSignalStrengthCdma cdma = ((CellInfoCdma) info).getCellSignalStrength();
                            _signalStrength = cdma.getAsuLevel();
                            cellInfoType = "CDMA";
                        } else if (info instanceof CellInfoWcdma) {
                            final CellSignalStrengthWcdma wcdma = ((CellInfoWcdma) info).getCellSignalStrength();
                            _signalStrength = wcdma.getAsuLevel();
                            cellInfoType = "WCDMA";
                        } else if (info instanceof CellInfoLte) {
                            final CellSignalStrengthLte lte = ((CellInfoLte) info).getCellSignalStrength();
                            _signalStrength = lte.getAsuLevel();
                            cellInfoType = "LTE";
                        }
                    }
                }
            }
            _eventBus.dispatchEvent(new SignalLoadedEvent(location.getLatitude(),
                    location.getLongitude(),
                    location.getTime(),
                    _signalStrength,
                    cellInfoType,
                    _telephonyManager.getNetworkOperatorName()));
        }catch(SecurityException c) {
                new RuntimeException("Exception");
                System.out.println("!");
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
