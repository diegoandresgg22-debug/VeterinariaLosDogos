package com.example.myapplication.models;

public class IoTCommand {

    private String command;
    private long timestamp;

    public IoTCommand() {}

    public IoTCommand(String command, long timestamp) {
        this.command = command;
        this.timestamp = timestamp;
    }

    public String getCommand() { return command; }
    public void setCommand(String command) { this.command = command; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}

