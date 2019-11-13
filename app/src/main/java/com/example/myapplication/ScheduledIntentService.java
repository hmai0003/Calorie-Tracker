package com.example.myapplication;

import android.app.IntentService;
import android.content.Intent;

public class ScheduledIntentService extends IntentService {

    public ScheduledIntentService() {
        super("ScheduledIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        updateAtMidnight update = new updateAtMidnight(getApplicationContext());
        update.report();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }
}
