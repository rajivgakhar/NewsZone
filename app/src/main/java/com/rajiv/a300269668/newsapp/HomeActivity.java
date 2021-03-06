package com.rajiv.a300269668.newsapp;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.internal.BottomNavigationMenuView;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.UUID;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private Toolbar mToolbar;
    private TabFragment selectedFragment = null;
    private SectionFragment sectionFragment = null;
    private DrawerLayout mDrawerLayout;
    ActionMode actionMode;
    BottomNavigationMenuView menuView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupToolbarMenu();
        setupNavigationDrawerMenu();

        final BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation);
         menuView = (BottomNavigationMenuView) bottomNavigationView.getChildAt(0);

        selectedFragment = new TabFragment(100);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, selectedFragment);
        transaction.commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.top_stories:
                        selectedFragment = new TabFragment(100);
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        mToolbar.setTitle("Top Stories");
                        if(actionMode!=null)
                        actionMode.finish();
                        break;
                    case R.id.saved:
                        selectedFragment = new TabFragment(11);
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        mToolbar.setTitle("Saved News");
                        if(actionMode!=null)
                        actionMode.finish();
                        break;
                    case R.id.sections:
                        sectionFragment = new SectionFragment();
                        transaction.replace(R.id.frame_layout, sectionFragment);
                        mToolbar.setTitle("Categories");
                        if(actionMode!=null)
                        actionMode.finish();
                        break;
                }
                transaction.commit();
                return true;
            }
        });

        generateUniqueId();

    }

    private void generateUniqueId() {
         String android_id = UUID.randomUUID().toString();
        SharedPreferences pref=getSharedPreferences("_androidId",MODE_PRIVATE);
        String existing_id=pref.getString("android_id","0");
        if(existing_id.equals("0")) {
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("android_id", android_id);
            editor.apply();
        }
    }

    private void setupToolbarMenu(){
        mToolbar=(Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle("Top Stories");
        mToolbar.inflateMenu(R.menu.menu_main);

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            class ContextualCallback implements ActionMode.Callback {
                EditText editText;
                @Override
                public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                    actionMode.getMenuInflater().inflate(R.menu.contextual_menu,menu);

                    editText=new EditText(getApplicationContext());
                    editText.setTextColor(Color.WHITE);
                    editText.setHint("Search here..");
                    editText.setHintTextColor(Color.WHITE);

                    actionMode.setCustomView(editText);
                    editText.requestFocus();
                    editText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            if(count>0) {
                                String searchText = editText.getText().toString().trim();
                                selectedFragment = new TabFragment(searchText, 10);
                                // TODO Auto-generated method stub
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame_layout, selectedFragment);
                                transaction.commit();
                            }else{
                                selectedFragment = new TabFragment( 10);
                                // TODO Auto-generated method stub
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame_layout, selectedFragment);
                                transaction.commit();
                            }
                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                           // TODO Auto-generated method stub
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                            // TODO Auto-generated method stub
                        }
                    });

                    return true;
                }

                @Override
                public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {


                    return false;
                }

                @Override
                public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.search:
                            String searchText = editText.getText().toString().trim();
                            if(searchText.length()>0) {
                                selectedFragment = new TabFragment(searchText, 10);
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.frame_layout, selectedFragment);
                                transaction.commit();
                            }else{
                                Toast.makeText(getApplicationContext(),"Type something to search",Toast.LENGTH_SHORT).show();
                            }
                            break;

                    }
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, selectedFragment);
                    transaction.commit();
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode actionMode) {
                    selectedFragment = new TabFragment( 10);
                    // TODO Auto-generated method stub
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, selectedFragment);
                    transaction.commit();
                }
            }

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.search:
                        actionMode=HomeActivity.this.startActionMode(new ContextualCallback());
                        break;
                }
                return true;
            }
        });

    }
    private void setupNavigationDrawerMenu(){
        NavigationView navigationView=(NavigationView)findViewById(R.id.navigationView);
        mDrawerLayout=(DrawerLayout)findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle drawerToggle=new ActionBarDrawerToggle(this,
                mDrawerLayout,mToolbar,
                R.string.drawer_open,
                R.string.drawer_close);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();//mainitain the state of drawer if open make it open
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        closeDrawer();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        switch (item.getItemId()){
            case R.id.top_stories:
                selectedFragment = new TabFragment(100);
                mToolbar.setTitle("Top Stories");
                menuView.getChildAt(0).performClick();
                transaction.replace(R.id.frame_layout, selectedFragment);
                break;
            case R.id.saved:
                selectedFragment = new TabFragment(11);
                transaction.replace(R.id.frame_layout, selectedFragment);
                menuView.getChildAt(1).performClick();
                mToolbar.setTitle("Saved News");
                break;
            case R.id.business:
                selectedFragment = new TabFragment(0);
                mToolbar.setTitle("Business");
                transaction.replace(R.id.frame_layout, selectedFragment);
                menuView.getChildAt(2).performClick();
                break;
            case R.id.entertainment:
                selectedFragment = new TabFragment(1);
                mToolbar.setTitle("Entertainment");
                transaction.replace(R.id.frame_layout, selectedFragment);
                menuView.getChildAt(2).performClick();
                break;
            case R.id.health:
                selectedFragment = new TabFragment(3);
                mToolbar.setTitle("Health");
                transaction.replace(R.id.frame_layout, selectedFragment);
                menuView.getChildAt(2).performClick();
                break;
            case R.id.science:
                selectedFragment = new TabFragment(4);
                mToolbar.setTitle("Science");
                transaction.replace(R.id.frame_layout, selectedFragment);
                menuView.getChildAt(2).performClick();
                break;
            case R.id.sports:
                selectedFragment = new TabFragment(5);
                mToolbar.setTitle("Sports");
                transaction.replace(R.id.frame_layout, selectedFragment);
                menuView.getChildAt(2).performClick();
                break;
            case R.id.technology:
                selectedFragment = new TabFragment(6);
                mToolbar.setTitle("Technology");
                transaction.replace(R.id.frame_layout, selectedFragment);
                menuView.getChildAt(2).performClick();
                break;
            case R.id.settings:
                startActivity(new Intent(HomeActivity.this,SettingActivity.class));
                finish();
                break;
            case R.id.sendfeedback:
                startActivity(new Intent(HomeActivity.this,FeedbackActivity.class));
                break;
        }

        transaction.commit();
        return true;
    }
    private void closeDrawer(){
        mDrawerLayout.closeDrawer(GravityCompat.START);
    }
    private void showDrawer(){
        mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public void onBackPressed() {
        if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
            closeDrawer();
        else
            super.onBackPressed();
    }
}
