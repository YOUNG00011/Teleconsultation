package com.wxsoft.telereciver.event;

public class UploadPhotoSuccessEvent {

    private String tabId;

    public UploadPhotoSuccessEvent(String tabId) {
        this.tabId = tabId;
    }

    public String getTabId() {
        return tabId;
    }
}
