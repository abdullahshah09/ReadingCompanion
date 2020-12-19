package com.abdullah.model;

public class Goal {
    int id;
    Long time,end_time,duration,read_duration;
    int type;

    public Goal(int id, Long time, Long end_time, Long duration, Long read_duration, int type) {
        this.id = id;
        this.time = time;
        this.end_time = end_time;
        this.duration = duration;
        this.read_duration = read_duration;
        this.type = type;
    }

    public Long getRead_duration() {
        return read_duration;
    }

    public int getId() {
        return id;
    }

    public Long getTime() {
        return time;
    }

    public Long getEnd_time() {
        return end_time;
    }

    public Long getDuration() {
        return duration;
    }

    public int getType() {
        return type;
    }
}
