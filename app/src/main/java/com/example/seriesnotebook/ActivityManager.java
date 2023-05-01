package com.example.seriesnotebook;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityManager extends Application {
    private FirebaseUser firebaseUser;

    @Override
    public void onCreate() {
        super.onCreate();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if(firebaseUser == null){
            Intent intent = new Intent(this, AuthActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }
}
