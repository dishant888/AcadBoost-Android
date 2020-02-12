package com.example.acadboost;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

import org.w3c.dom.Text;

import java.util.HashMap;


public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    SessionManager session;
    Fragment homeFragment = new HomeFragment();
    Fragment videoFragment = new VideoFragment();
    Fragment userFragment = new UserFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //NavBarDrawer Toggler
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Bottom Menu and Fragments
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListner);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,new HomeFragment()).commit();

        //Navigation Drawer
        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(drawerListner);

        //Session
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> sessionData = session.getSessionData();
        //Set session data to nav header
        View navigationHeaderView = navigationView.getHeaderView(0);
        TextView navBarHeaderUserName = navigationHeaderView.findViewById(R.id.navBarHeaderUserName);
        TextView navBarHeaderUserEmail = navigationHeaderView.findViewById(R.id.navBarHeaderUserEmail);
        navBarHeaderUserName.setText(sessionData.get(SessionManager.NAME));
        navBarHeaderUserEmail.setText(sessionData.get(SessionManager.EMAIL));

    }


    private NavigationView.OnNavigationItemSelectedListener drawerListner = new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.myCourses:
                    Toast.makeText(HomeActivity.this, "My Progress", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.progress:
                    Toast.makeText(HomeActivity.this,"Progress",Toast.LENGTH_SHORT).show();
                    break;
                case R.id.helpAndSupport:
                    Toast.makeText(HomeActivity.this, "Help and Support", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.logout:
                    logout();
                    break;
            }
            return true;
        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener navListner = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.homeMenu :
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,homeFragment).commit();
                    break;

                case R.id.videoMenu :
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,videoFragment).commit();
                    break;

                case R.id.userMenu :
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,userFragment).commit();
                    break;
            }

//            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,selectedFragment).commit();

            return true;
        }
    };

    public void logout() {

        session.endSession();
        Intent login = new Intent(getApplicationContext(),LoginActivity.class);
        login.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        login.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(login);
        finish();
    }

    //Drawer toggler
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
