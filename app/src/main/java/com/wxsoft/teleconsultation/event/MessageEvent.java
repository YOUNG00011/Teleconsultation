package com.wxsoft.teleconsultation.event;

public class MessageEvent {

    private int unReadMsgCount;

    public MessageEvent(int unReadMsgCount) {
        this.unReadMsgCount = unReadMsgCount;
    }

    public int getUnReadMsgCount() {
        return unReadMsgCount;
    }
}
