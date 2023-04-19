package com.example.ghi_chu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.ghich.R;

import java.util.ArrayList;
import java.util.List;

public class AddLabelActivity extends AppCompatActivity {
    ImageView imgLabel, imgDelete;
    EditText edLabel;
    List<Label> list;
    ListView lvLabels;
    ListLabelAdapter adapter;
    DBHelper db;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        db = new DBHelper(this);
        setContentView(R.layout.activity_add_label);
        setToolBar();
//        setTopListView();

        lvLabels = findViewById(R.id.lvLabels);
        lvLabels.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
        list = new ArrayList<Label>();
        list = db.getAllLabels();
        adapter = new ListLabelAdapter(this, list);
        lvLabels.setAdapter(adapter);

//        setEdLabelOnFocusChangeListener();
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

    public void setToolBar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Chỉnh sửa nhãn");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void setTopListView() {
        imgLabel = findViewById(R.id.imgLabel);
        imgDelete = findViewById(R.id.imgDelete);
        edLabel = findViewById(R.id.edLabel);
    }

    private void setEdLabelOnFocusChangeListener() {
        edLabel.setOnFocusChangeListener((v, hasFocus) -> {

        });
    }
}
