package com.ieti.easywheels.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.ieti.easywheels.R;
import com.ieti.easywheels.network.Firebase;



public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    private TextInputLayout textInputEmail;
    private TextInputLayout textInputPassword;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textInputEmail = findViewById(R.id.textInputEmail);
        textInputPassword = findViewById(R.id.textInputPassword);
    }

    private void buildDialog(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle(getApplicationContext().getResources().getString(R.string.login_failed_dialog_title));
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setNegativeButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        return;
                    }
                });
        alertDialog = builder1.create();
    }

    private boolean validateEmail() {
        AppCompatEditText emailInput = (AppCompatEditText) textInputEmail.getEditText();
        textInputEmail.setError(null);
        if (TextUtils.isEmpty(emailInput.getText().toString())) {
            textInputEmail.setError(getApplicationContext().getResources().getString(R.string.field_validation_empty));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.getText().toString()).matches()) {
            textInputEmail.setError(getApplicationContext().getResources().getString(R.string.field_validation_email));
            return false;
        } else {
            textInputEmail.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        textInputPassword.setError(null);
        AppCompatEditText passwordInput = (AppCompatEditText) textInputPassword.getEditText();
        if (TextUtils.isEmpty(passwordInput.getText().toString())) {
            textInputPassword.setError(getApplicationContext().getResources().getString(R.string.field_validation_empty));
            return false;
        } else {
            textInputPassword.setError(null);
            return true;
        }
    }

    private void switchToMainView() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateEmail() && !validatePassword()) {
            return;
        }

        Firebase.getFAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = Firebase.getFAuth().getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            FirebaseUser user = Firebase.getFAuth().getCurrentUser();
                            updateUI(user);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user == null) {
            buildDialog(getApplicationContext().getResources().getString(R.string.login_failed_dialog_message));
            alertDialog.show();
        } else {
            if(user.isEmailVerified()){
                switchToMainView();
            }else{
                Firebase.getFAuth().signOut();
                buildDialog(getApplicationContext().getResources().getString(R.string.email_verification_failed_dialog_message));
                alertDialog.show();
            }
        }
    }

    public void login(View v) {
        v.setEnabled(false);
        signIn(textInputEmail.getEditText().getText().toString(), textInputPassword.getEditText().getText().toString());
        v.setEnabled(true);
    }

    public void createAccount(View v){
        Intent intent = new Intent(this, CreateAccountActivity.class);
        startActivity(intent);
    }
}
