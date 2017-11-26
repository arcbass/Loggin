package com.example.loggin.other;



public class WsMsg {
    private String username;
    private String signalcarrier;

    public WsMsg(String username, String signalcarrier) {
        this.username = username;
        this.signalcarrier = signalcarrier;
    }

    public String getUsername() {
        return username;
    }

    public String getSignalcarrier() {
        return signalcarrier;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSignalcarrier(String signalcarrier) {
        this.signalcarrier = signalcarrier;
    }


}