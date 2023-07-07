package com.example.hostty.Resources;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.hostty.R;
import com.example.hostty.Resources.CIV.CIVBranch;
import com.example.hostty.Resources.CS.CSBranch;
import com.example.hostty.Resources.Common.CommonBranch;
import com.example.hostty.Resources.ECE.ECEBranch;
import com.example.hostty.Resources.EE.EEBranch;
import com.example.hostty.Resources.ME.MEBranch;
import com.example.hostty.Utils.CommonFunctions;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class ResourceActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener,View.OnClickListener {


    private RelativeLayout csLayout;
    private RelativeLayout eceLayout;
    private RelativeLayout eeLayout;
    private RelativeLayout mechLayout;
    private RelativeLayout civLayout;
    private RelativeLayout otherLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);


        findViewById(R.id.include_res).setVisibility(View.VISIBLE);

        resourceInit();

        csLayout.setOnClickListener(this);
        eceLayout.setOnClickListener(this);
        eeLayout.setOnClickListener(this);
        mechLayout.setOnClickListener(this);
        civLayout.setOnClickListener(this);
        otherLayout.setOnClickListener(this);

    }

    private void resourceInit(){

        NavigationView navigationView = findViewById(R.id.nav_view);
        csLayout=findViewById(R.id.rl_cs);
        eceLayout=findViewById(R.id.rl_ece);
        eeLayout=findViewById(R.id.rl_ee);
        mechLayout=findViewById(R.id.rl_me);
        civLayout=findViewById(R.id.rl_civ);
        otherLayout=findViewById(R.id.rl_others);


        navigationView.setNavigationItemSelectedListener(this);

        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setHomeButtonEnabled(true);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        CommonFunctions.setUser(this);

    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return CommonFunctions.navigationItemSelect(item, this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case (R.id.rl_cs):
                startActivity(new Intent(ResourceActivity.this, CSBranch.class));
                break;
            case (R.id.rl_ece):
                startActivity(new Intent(ResourceActivity.this, ECEBranch.class));
                break;
            case (R.id.rl_ee):
                startActivity(new Intent(ResourceActivity.this, EEBranch.class));
                break;
            case (R.id.rl_me):
                startActivity(new Intent(ResourceActivity.this, MEBranch.class));
                break;
            case (R.id.rl_civ):
                startActivity(new Intent(ResourceActivity.this, CIVBranch.class));
                break;
            case (R.id.rl_others):
                startActivity(new Intent(ResourceActivity.this, CommonBranch.class));
                break;
                default:
                    startActivity(new Intent(ResourceActivity.this,CSBranch.class));
        }
    }
}
