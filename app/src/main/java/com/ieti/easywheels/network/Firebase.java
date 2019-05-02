package com.ieti.easywheels.network;

import com.google.firebase.auth.FirebaseAuth;

public class Firebase {
    private static FirebaseAuth FAuth;


    public static FirebaseAuth getFAuth() {
        if (FAuth == null) {
            FAuth = FirebaseAuth.getInstance();
        }
        return FAuth;
    }

}
