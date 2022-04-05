package com.example.ghi_chu;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ghich.R;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class TrashFragment extends Fragment {
    List<Note> list;
    DBHelper db;
    RecyclerView rvNotes;
    RecyclerViewAdapter adapter;
    StaggeredGridLayoutManager layoutManager;

    public TrashFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_trash, container, false);
        rvNotes = view.findViewById(R.id.rvNotes);
        list = new ArrayList<Note>();
        db = new DBHelper(getContext());
        list = db.getAllNotes(1);
        layoutManager = new StaggeredGridLayoutManager(
                this.getActivity().getSharedPreferences("VIEW", MODE_PRIVATE).getInt("column", 1),
                StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        rvNotes.setLayoutManager(layoutManager);
        adapter = new RecyclerViewAdapter(getContext(), list);
        rvNotes.setAdapter(adapter);
        return view;
    }

}
