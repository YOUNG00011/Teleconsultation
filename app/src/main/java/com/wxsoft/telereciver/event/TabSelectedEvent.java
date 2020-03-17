package com.wxsoft.telereciver.event;

/**
 * Created by liping on 2017/10/17.
 */
public class TabSelectedEvent {

    private int position;

    public TabSelectedEvent(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }
}
