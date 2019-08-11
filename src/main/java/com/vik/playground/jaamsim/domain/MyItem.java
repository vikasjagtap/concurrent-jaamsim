package com.vik.playground.jaamsim.domain;

public class MyItem {

    private long id;

    private long runDuration;


    public MyItem() {

    }

    public MyItem(long id, long runDuration) {
        this.id = id;
        this.runDuration = runDuration;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getRunDuration() {
        return runDuration;
    }

    public void setRunDuration(long runDuration) {
        this.runDuration = runDuration;
    }
}

