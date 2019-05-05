package com.ieti.easywheels.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ieti.easywheels.R;
import com.ieti.easywheels.network.Firebase;
import com.ieti.easywheels.ui.steps.DayStep;
import com.ieti.easywheels.ui.steps.DestinationStep;
import com.ieti.easywheels.ui.steps.HourStep;
import com.ieti.easywheels.ui.steps.TypeStep;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;
import ernestoyaquello.com.verticalstepperform.listener.StepperFormListener;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, StepperFormListener {

    private VerticalStepperFormView stepperFormView;

    private TypeStep typeStep;
    private DestinationStep destinationStep;
    private DayStep dayStep;
    private HourStep hourStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setTextsOfUser();
        setVerticalStepper();
    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_program_trip) {
            // Handle the camera action
        } else if (id == R.id.nav_program_week) {

        } else if (id == R.id.nav_wallet) {

        } else if (id == R.id.nav_trips) {

        } else if (id == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setTextsOfUser(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View  view=navigationView.getHeaderView(0);
        TextView name=view.findViewById(R.id.nav_header_name);
        name.setText(String.valueOf(Firebase.getFAuth().getCurrentUser().getDisplayName()));
        TextView email=view.findViewById(R.id.nav_header_email);
        email.setText(String.valueOf(Firebase.getFAuth().getCurrentUser().getEmail()));
    }

    private void setVerticalStepper() {
        typeStep = new TypeStep("Modalidad");
        destinationStep = new DestinationStep("Destino");
        dayStep = new DayStep("Dia");
        hourStep = new HourStep("Hora");

        stepperFormView = findViewById(R.id.stepper_form);
        stepperFormView
                .setup(this,typeStep,destinationStep, dayStep, hourStep)
                .init();
    }

    @Override
    public void onCompletedForm() {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCancelledForm() {

    }


}
