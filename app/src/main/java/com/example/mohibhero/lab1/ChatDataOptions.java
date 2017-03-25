package com.example.mohibhero.lab1;

/**
 * Created by MohibHero on 3/21/17.
 */

public class ChatDataOptions {
    private String message;
    private long _id;

    public ChatDataOptions() {
        message = "";
        _id = 0;
    }

    public ChatDataOptions(String message, long _id) {
        this.message = message;
        this._id = _id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long get_id() {
        return _id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

}
