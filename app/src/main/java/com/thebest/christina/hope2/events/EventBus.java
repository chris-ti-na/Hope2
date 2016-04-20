package com.thebest.christina.hope2.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EventBus {
    Map<String, List<InternalListener>> map = new HashMap<>();

    public void addEventListener (String eventType, InternalListener listener){
        List<InternalListener> list = map.get(eventType);
        if(list == null){
            map.put(eventType, list = new ArrayList<InternalListener>());
        }
        list.add(listener);
    }

    public void dispatchEvent(Event event){
        String type = event.getType();
        List<InternalListener> listeners = map.get(type);
        if(listeners == null) return;
        for (InternalListener x : listeners) {
            x.perform(event);
        }
    }

}
