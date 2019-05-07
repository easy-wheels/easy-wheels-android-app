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
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.ieti.easywheels.R;
import com.ieti.easywheels.network.Firebase;
import com.ieti.easywheels.ui.fragments.ProgramTripFragment;
import com.ieti.easywheels.ui.fragments.ProgramWeekFragment;
import com.ieti.easywheels.ui.fragments.TripsFragment;
import com.ieti.easywheels.ui.fragments.WalletFragment;
import com.ieti.easywheels.ui.fragments.steps.DayStep;
import com.ieti.easywheels.ui.fragments.steps.DestinationStep;
import com.ieti.easywheels.ui.fragments.steps.HourStep;
import com.ieti.easywheels.ui.fragments.steps.TypeStep;

import ernestoyaquello.com.verticalstepperform.VerticalStepperFormView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private VerticalStepperFormView stepperFormView;
    private FragmentTransaction fragmentTransaction;

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
        navigationView.setCheckedItem(R.id.nav_program_trip);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        setTextsOfUser();
        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.main_container,new ProgramTripFragment());
        fragmentTransaction.commit();
        getSupportActionBar().setTitle("Programa Tu viaje");
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
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container,new ProgramTripFragment());
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Programa Tu viaje");
        } else if (id == R.id.nav_program_week) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container,new ProgramWeekFragment());
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Tu Semana");
        } else if (id == R.id.nav_wallet) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container,new WalletFragment());
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Monedero");
        } else if (id == R.id.nav_trips) {
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container,new TripsFragment());
            fragmentTransaction.commit();
            getSupportActionBar().setTitle("Viajes");
        } else if (id == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawers();
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




}
