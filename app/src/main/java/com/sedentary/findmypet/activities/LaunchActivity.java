package com.sedentary.findmypet.activities;

import android.content.Intent;
import android.os.Bundle;

public class LaunchActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, DashboardActivity.class));
        finish();
    }
}
