package com.example.android.questtime.data.firebase;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Luka on 11/09/2017.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference users = database.getReference("users");
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d("Refreshed token: ", refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer (String token) {
        users.child(mAuth.getUid()).child("registrationToken").setValue(token);
    }

}
