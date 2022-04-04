package com.example.ghich;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle toggle;
    Menu menu, optionsMenu;
    List<Label> list;
    DBHelper db;
    Intent intent;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Fragment fragment;
    FloatingActionButton fab;
    int delayMillis = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setContentView(R.layout.activity_main);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        toggle.setDrawerSlideAnimationEnabled(false);
        toggle.getDrawerArrowDrawable().setColor(getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        menu = navigationView.getMenu();
        list = new ArrayList<Label>();
        db = new DBHelper(this);
        list = db.getAllLabels();
        for (Label label : list) {
            menu.add(R.id.grLabel, label.getId(), 1, label.getLabel());
            menu.findItem(label.getId()).setIcon(R.drawable.ic_label_outline);
        }
        sharedPreferences = getSharedPreferences("VIEW", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        if (savedInstanceState == null) {
            fragment = new NotesFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    fragment).commit();
            navigationView.setCheckedItem(R.id.notes);
        }
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), AddNoteActivity.class), 10001);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_menu, menu);
        optionsMenu = menu;
        if (sharedPreferences.getInt("column", 1) == 1)
            menu.getItem(0).setIcon(R.drawable.ic_two_column);
        else
            menu.getItem(0).setIcon(R.drawable.ic_one_column);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.changeView:
                if (sharedPreferences.getInt("column", 1) == 1) {
                    editor.putInt("column", 2);
                    editor.apply();
                    item.setIcon(R.drawable.ic_one_column);
                    getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                } else {
                    editor.putInt("column", 1);
                    editor.apply();
                    item.setIcon(R.drawable.ic_two_column);
                    getSupportFragmentManager().beginTransaction().detach(fragment).attach(fragment).commit();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int itemId = menuItem.getItemId();
        switch (itemId) {
            case R.id.notes:
                toolbar.setTitle("Ghi Chú");
                delayMillis = 0;
                fragment = new NotesFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragment).commit();
                fab.show();
                break;
            case R.id.addLabel:
                delayMillis = 500;
                startActivityForResult(new Intent(getApplicationContext(), AddLabelActivity.class), 10001);
                break;
            case R.id.trash:
                toolbar.setTitle("Thùng rác");
                delayMillis = 0;
                fragment = new TrashFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                        fragment).commit();
                fab.hide();
                break;
            default:
                delayMillis = 500;
                for (Label label : list) {
                    if (itemId == label.getId()) {
                        intent = new Intent(this, ShowNotesByLabelActivity.class);
                        intent.putExtra("label", label.getLabel());
                        startActivityForResult(intent, 10001);
                        break;
                    }
                }
                break;
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                drawerLayout.closeDrawer(GravityCompat.START);
            }
        }, delayMillis);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode == 10001) && (resultCode == Activity.RESULT_OK)) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.detach(fragment).attach(fragment).commit();
            if (sharedPreferences.getInt("column", 1) == 1)
                optionsMenu.getItem(0).setIcon(R.drawable.ic_two_column);
            else
                optionsMenu.getItem(0).setIcon(R.drawable.ic_one_column);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                for (Label label : list)
                    menu.removeItem(label.getId());
                list = db.getAllLabels();
                for (Label label : list) {
                    menu.add(R.id.grLabel, label.getId(), 1, label.getLabel());
                    menu.findItem(label.getId()).setIcon(R.drawable.ic_label_outline);
                    Log.i("count ", String.valueOf(label.getId()));
                    Log.i("label ", String.valueOf(label.getLabel()));
                }
            }
        });
    }
}
