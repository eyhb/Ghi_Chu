package com.example.ghi_chu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ghich.R;

import java.util.ArrayList;
import java.util.List;

public class ShowNotesByLabelActivity extends AppCompatActivity {
    List<Note> list;
    DBHelper db;
    RecyclerView rvNotes;
    RecyclerViewAdapter adapter;
    StaggeredGridLayoutManager layoutManager;
    Toolbar toolbar;
    Intent intent;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notes_by_label);
        intent = getIntent();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(intent.getStringExtra("label"));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedPreferences = getSharedPreferences("VIEW", MODE_PRIVATE);
        editor = sharedPreferences.edit();
        rvNotes = findViewById(R.id.rvNotes);
        list = new ArrayList<Note>();
        db = new DBHelper(this);
        list = db.getAllNotesByLabel(intent.getStringExtra("label"));
        layoutManager = new StaggeredGridLayoutManager(
                this.getSharedPreferences("VIEW", MODE_PRIVATE).getInt("column", 1),
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rvNotes.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(this, list);
        rvNotes.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_menu, menu);
        if (sharedPreferences.getInt("column", 1) == 1)
            menu.getItem(0).setIcon(R.drawable.ic_two_column);
        else
            menu.getItem(0).setIcon(R.drawable.ic_one_column);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.changeView:
                if (sharedPreferences.getInt("column", 1) == 1) {
                    editor.putInt("column", 2);
                    editor.apply();
                    item.setIcon(R.drawable.ic_one_column);
                } else {
                    editor.putInt("column", 1);
                    editor.apply();
                    item.setIcon(R.drawable.ic_two_column);
                }
                layoutManager = new StaggeredGridLayoutManager(
                        this.getSharedPreferences("VIEW", MODE_PRIVATE).getInt("column", 1),
                        StaggeredGridLayoutManager.VERTICAL);
                layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
                rvNotes.setLayoutManager(layoutManager);
                adapter = new RecyclerViewAdapter(this, list);
                rvNotes.setAdapter(adapter);
                setResult(Activity.RESULT_OK);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        list = new ArrayList<Note>();
        list = db.getAllNotesByLabel(intent.getStringExtra("label"));
        layoutManager = new StaggeredGridLayoutManager(
                this.getSharedPreferences("VIEW", MODE_PRIVATE).getInt("column", 1),
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rvNotes.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(this, list);
        rvNotes.setAdapter(adapter);
        setResult(Activity.RESULT_OK);
    }
}
