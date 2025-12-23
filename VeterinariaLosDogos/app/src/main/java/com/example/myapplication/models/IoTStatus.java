package com.example.myapplication.models;

public class IoTStatus {

    private double temperature;
    private int heartRate;
    private String activityLevel;
    private boolean doorOpen;
    private boolean buzzerActive;
    private String state;

    public IoTStatus() {
    }

    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature = temperature; }

    public int getHeartRate() { return heartRate; }
    public void setHeartRate(int heartRate) { this.heartRate = heartRate; }

    public String getActivityLevel() { return activityLevel; }
    public void setActivityLevel(String activityLevel) { this.activityLevel = activityLevel; }

    public boolean isDoorOpen() { return doorOpen; }
    public void setDoorOpen(boolean doorOpen) { this.doorOpen = doorOpen; }

    public boolean isBuzzerActive() { return buzzerActive; }
    public void setBuzzerActive(boolean buzzerActive) { this.buzzerActive = buzzerActive; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}


