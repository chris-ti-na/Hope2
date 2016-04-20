package com.thebest.christina.hope2.events;


import com.thebest.christina.hope2.model.Frame;

public class SendFrameEvent implements Event {
    public Frame _frame;

    public SendFrameEvent(Frame frame) {
        this._frame = frame;
    }

    @Override
    public String getType() {
        return "SendFrameEvent";
    }
}
