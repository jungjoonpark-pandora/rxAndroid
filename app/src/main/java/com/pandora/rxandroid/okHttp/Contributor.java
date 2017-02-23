package com.pandora.rxandroid.okHttp;

/**
 * Created by jungjoon on 2017. 2. 24..
 */

public class Contributor {
    String login;
    String url;
    int id;

    public String getLogin() {
        return login;
    }

    public String getUrl() {
        return url;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "login : " + login + "id : " + id + "url : " + url;
    }
}