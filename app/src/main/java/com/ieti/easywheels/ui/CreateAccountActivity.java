package com.ieti.easywheels.ui;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.ieti.easywheels.R;
import com.ieti.easywheels.model.User;
import com.ieti.easywheels.network.Firebase;

public class CreateAccountActivity extends AppCompatActivity {

    private TextInputLayout name;
    private TextInputLayout email;
    private TextInputLayout password;
    private TextInputLayout re_type_password;
    private Button button;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        Toolbar mToolbar =  findViewById(R.id.toolbar);
        name = findViewById(R.id.textInputName);
        email = findViewById(R.id.textInputEmail);
        password = findViewById(R.id.textInputPassword);
        re_type_password = findViewById(R.id.textInputRePassword);
        button = findViewById(R.id.create_button);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void createAccount(final View v){
        if(validateName()&&validateEmail() && validatePassword()) {
            Snackbar.make(v, R.string.wait, Snackbar.LENGTH_SHORT)
                    .show();
            button.setEnabled(false);
            Firebase
                    .getFAuth()
                    .createUserWithEmailAndPassword(email.getEditText().getText().toString(), password.getEditText().getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Firebase
                                    .getFAuth()
                                    .getCurrentUser()
                                    .updateProfile(new UserProfileChangeRequest.Builder()
                                            .setDisplayName(name.getEditText().getText().toString())
                                            .build())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Firebase.createUser(new User(email.getEditText().getText().toString(),name.getEditText().getText().toString()))
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Firebase
                                                                    .getFAuth()
                                                                    .getCurrentUser()
                                                                    .sendEmailVerification()
                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                            Firebase
                                                                                    .getFAuth()
                                                                                    .signOut();
                                                                            buildDialog(getApplicationContext().getResources().getString(R.string.sended_email));
                                                                            alertDialog.show();
                                                                        }
                                                                    });
                                                        }
                                                    });
                                        }
                                    });
                        }
                    });

        }
    }
    private boolean validateEmail() {
        AppCompatEditText emailInput = (AppCompatEditText) email.getEditText();
        email.setError(null);
        if (TextUtils.isEmpty(emailInput.getText().toString())) {
            email.setError(getApplicationContext().getResources().getString(R.string.field_validation_empty));
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput.getText().toString()).matches()) {
            email.setError(getApplicationContext().getResources().getString(R.string.field_validation_email));
            return false;
        } else if (!email.getEditText().getText().toString().endsWith("escuelaing.edu.co")) {
            email.setError(getApplicationContext().getResources().getString(R.string.bad_email));
            return false;
        } else {
            email.setError(null);
            return true;
        }
    }

    private boolean validatePassword() {
        re_type_password.setError(null);
        password.setError(null);
        AppCompatEditText repasswordInput = (AppCompatEditText) re_type_password.getEditText();
        AppCompatEditText passwordInput = (AppCompatEditText) password.getEditText();
        if(TextUtils.isEmpty(passwordInput.getText().toString())){
            passwordInput.setError(getApplicationContext().getResources().getString(R.string.field_validation_empty));
            return false;
        }
        if (TextUtils.isEmpty(repasswordInput.getText().toString())) {
            repasswordInput.setError(getApplicationContext().getResources().getString(R.string.field_validation_empty));
            return false;
        }else if(!re_type_password.getEditText().getText().toString().equals(password.getEditText().getText().toString())){
            repasswordInput.setError(getApplicationContext().getResources().getString(R.string.inequal_password));
            return false;
        } else {
            repasswordInput.setError(null);
            return true;
        }
    }

    private boolean validateName(){
        name.setError(null);
        AppCompatEditText nameInput = (AppCompatEditText) name.getEditText();
        if(TextUtils.isEmpty(nameInput.getText().toString())){
            nameInput.setError(getApplicationContext().getResources().getString(R.string.field_validation_empty));
            return false;
        }else{
            return true;
        }
    }

    private void buildDialog(String message) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setTitle(getApplicationContext().getResources().getString(R.string.create_account_dialog_title));
        builder1.setMessage(message);
        builder1.setCancelable(true);
        builder1.setNegativeButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                        return;
                    }
                });
        alertDialog = builder1.create();
    }
}


