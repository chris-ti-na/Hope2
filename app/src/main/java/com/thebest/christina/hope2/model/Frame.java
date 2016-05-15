package com.thebest.christina.hope2.model;


public class Frame {
    public double _latitude;
    public double _longitude;
    public long _time;
    public int _asuSignalStrength;
    public int _barSignalStrength;
    public int _dbmSignalStrength;
    public String _cellInfo;
    public String _networkProvider;

    public Frame(double latitude, double longitude, long time, int asuSignalStrength, int _barSignalStrength, int _dbmSignalStrength, String cellInfo, String networkProvider) {
        this._latitude = latitude;
        this._longitude = longitude;
        this._time = time;
        this._asuSignalStrength = asuSignalStrength;
        this._barSignalStrength = _barSignalStrength;
        this._dbmSignalStrength = _dbmSignalStrength;
        this._cellInfo = cellInfo;
        this._networkProvider = networkProvider;
    }
}
