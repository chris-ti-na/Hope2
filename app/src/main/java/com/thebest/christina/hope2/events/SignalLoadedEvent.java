package com.thebest.christina.hope2.events;


public class SignalLoadedEvent implements Event{

    public double _latitude;
    public double _longitude;
    public int _signalStrength;
    public String _cellInfo;
    public String _networkProvider;
    public long _time;

    public SignalLoadedEvent(double latitude, double longitude, long time, int signalStrength, String cellInfo, String networkProvider) {
        this._latitude = latitude;
        this._longitude = longitude;
        this._time = time;
        this._signalStrength = signalStrength;
        this._cellInfo = cellInfo;
        this._networkProvider = networkProvider;
    }

    @Override
    public String getType() {
        return "SignalLoadedEvent";
    }
}
