package com.example.ghi_chu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.ghich.R;

import java.util.ArrayList;
import java.util.List;

public class AddLabelActivity extends AppCompatActivity {
    List<Label> list;
    ListView lvLabels;
    ListLabelAdapter adapter;
    DBHelper db;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_label);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Chỉnh sửa nhãn");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lvLabels = findViewById(R.id.lvLabels);
        lvLabels.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        db = new DBHelper(this);
        list = new ArrayList<Label>();
        list = db.getAllLabels();
        adapter = new ListLabelAdapter(this, list);
        lvLabels.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
