package com.thebest.christina.hope2.model;


public class Frame {
    public double _latitude;
    public double _longitude;
    public long _time;
    public int _signalStrength;
    public String _cellInfo;
    public String _networkProvider;

    public Frame(double latitude, double longitude, long time, int signalStrength, String cellInfo, String networkProvider) {
        this._latitude = latitude;
        this._longitude = longitude;
        this._time = time;
        this._signalStrength = signalStrength;
        this._cellInfo = cellInfo;
        this._networkProvider = networkProvider;
    }
}
