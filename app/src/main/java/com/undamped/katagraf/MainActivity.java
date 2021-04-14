package com.undamped.katagraf;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.undamped.katagraf.fragments.InventoryFragment;
import com.undamped.katagraf.fragments.ProfileFragment;
import com.undamped.katagraf.fragments.ScanFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.bottomNavigation) BottomNavigationView bottomNavigation;
    @BindView(R.id.nav_host_frame) FrameLayout nav_host_frame;
    @BindView(R.id.scanFloatingBtn) FloatingActionButton scanFloatingBtn;
    @BindView(R.id.toolbar) Toolbar main_toolbar;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        scanFloatingBtn.setOnClickListener(view -> {
            getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_frame, new ScanFragment()).commit();
        });

        bottomNavigation.setSelectedItemId(R.id.action_inventory);
        getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_frame, new InventoryFragment()).commit();

        bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.action_profile:
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_frame, new ProfileFragment()).commit();
                    return true;

                case R.id.action_inventory:
                    getSupportFragmentManager().beginTransaction().replace(R.id.nav_host_frame, new InventoryFragment()).commit();
                    return true;
            }
            return false;
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}