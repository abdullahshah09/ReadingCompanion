package com.abdullah.model;

public class Stats {
    long duration;
    int id;
    long time;
    int day,month;

    public Stats(long duration, int id, long time, int day, int month) {
        this.duration = duration;
        this.id = id;
        this.time = time;
        this.day = day;
        this.month = month;
    }

    public void add_duration(long dur){
        duration+=dur;
    }

    public long getDuration() {
        return duration;
    }

    public int getId() {
        return id;
    }

    public long getTime() {
        return time;
    }

    public int getDay() {
        return day;
    }

    public int getMonth() {
        return month;
    }
}
