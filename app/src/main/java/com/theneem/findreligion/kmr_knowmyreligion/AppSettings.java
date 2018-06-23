package com.theneem.barterbuddy;

import android.app.Application;

/**
 * Created by deval on 3/8/2017.
 */

public class AppSettings extends Application {

private String relegionName;

    public String getRelegionName() {
        return relegionName;
    }

    public void setRelegionName(String relegionName) {
        this.relegionName = relegionName;
    }
}
