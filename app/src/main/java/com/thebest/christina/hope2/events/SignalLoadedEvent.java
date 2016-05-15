package com.thebest.christina.hope2.events;


public class SignalLoadedEvent implements Event{

    public double _latitude;
    public double _longitude;
    public int _asuSignalStrength;
    public int _barSignalStrength;
    public int _dbmSignalStrength;
    public String _cellInfo;
    public String _networkProvider;
    public long _time;

    public SignalLoadedEvent(double latitude, double longitude, long time, int asuSignalStrength, int _barSignalStrength, int _dbmSignalStrength, String cellInfo, String networkProvider) {
        this._latitude = latitude;
        this._longitude = longitude;
        this._time = time;
        this._asuSignalStrength = asuSignalStrength;
        this._barSignalStrength = _barSignalStrength;
        this._dbmSignalStrength = _dbmSignalStrength;
        this._cellInfo = cellInfo;
        this._networkProvider = networkProvider;
    }

    @Override
    public String getType() {
        return "SignalLoadedEvent";
    }
}
