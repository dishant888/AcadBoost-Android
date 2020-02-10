package com.example.acadboost;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class HomeActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;

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
                    selectedFragment = new HomeFragment();
                    break;

                case R.id.videoMenu :
                    selectedFragment = new VideoFragment();
                    break;

                case R.id.userMenu :
                    selectedFragment = new UserFragment();
                    break;
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,selectedFragment).commit();

            return true;
        }
    };

    public void logout() {
        Intent logout = new Intent(getApplicationContext(),LoginActivity.class);
        startActivity(logout);
        finish();
    }

}
