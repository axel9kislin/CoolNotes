package com.axel.coolnotes;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class Settings extends AppCompatActivity {

    public static final String SETTING_CLOSE = "settings_close";

    private DialogDeleteAll mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDialog = new DialogDeleteAll();

        EventBus.getDefault().register(this);
    }

    @Subscribe
    public void onEvent(UpdateData event)
    {
        String str = event.getData();
        if (str.equals(SETTING_CLOSE))
        {
            finish();
        }
    }

    public void OnClearAll(View view) {
        mDialog.show(getSupportFragmentManager(), null);

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }
}
