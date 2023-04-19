package com.example.ghi_chu;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ghich.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    Context context;
    LayoutInflater inflater;
    List<Note> list;
    DBHelper db;
    String title, label;

    public RecyclerViewAdapter(Context context, List<Note> list) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
        this.list = list;
        db = new DBHelper(context);
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_notes_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {
        title = list.get(position).getTitle();
        label = list.get(position).getLabel();

        // Set title view
        if (!title.equals("")) {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(list.get(position).getTitle());
        } else {
            holder.tvTitle.setVisibility(View.GONE);
        }

        // Set note view
        holder.tvNote.setText(list.get(position).getNote());

        // Set label view
        if (label != null && !label.equals("")) {
            holder.cvLabel.setVisibility(View.VISIBLE);
            holder.tvLabel.setText(list.get(position).getLabel());
        } else {
            holder.cvLabel.setVisibility(View.GONE);
        }

        holder.layout.requestLayout();
        holder.layout.setOnClickListener(v -> {
            if (list.get(position).getTrash() == 0) {
                Intent intent = new Intent(context, AddNoteActivity.class);
                intent.putExtra("noteId", list.get(position).getId());
                ((Activity) context).startActivityForResult(intent, 10001);
            }
        });
        holder.layout.setOnLongClickListener(v -> {
            if (list.get(position).getTrash() == 1) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Khôi phục?");
                builder.setPositiveButton("OK", (dialog, which) -> {
                    list.get(position).setTrash(0);
                    if (db.updateNote(list.get(position))) {
                        list.remove(position);
                        notifyDataSetChanged();
                        Toast toast = Toast.makeText(context, "Đã khôi phục", Toast.LENGTH_SHORT);
                        toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(context, "Lỗi", Toast.LENGTH_SHORT);
                        toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                        toast.show();
                    }
                    dialog.dismiss();
                });
                builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            }
            return false;
        });
        holder.imgDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            if (list.get(position).getTrash() == 0) {
                builder.setTitle("Chuyển vào thùng rác?");
                builder.setPositiveButton("OK", (dialog, which) -> {
                    list.get(position).setTrash(1);
                    if (db.updateNote(list.get(position))) {
                        list.remove(position);
                        notifyDataSetChanged();
                        Toast toast = Toast.makeText(context, "Đã chuyển vào thùng rác", Toast.LENGTH_SHORT);
                        toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(context, "Lỗi", Toast.LENGTH_SHORT);
                        toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                        toast.show();
                    }
                    dialog.dismiss();
                });
            } else {
                builder.setTitle("Xác nhận xóa");
                builder.setPositiveButton("Xóa", (dialog, which) -> {
                    if (db.deleteNote(list.get(position).getId())) {
                        list.remove(position);
                        notifyDataSetChanged();
                        Toast toast = Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT);
                        toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                        toast.show();
                    } else {
                        Toast toast = Toast.makeText(context, "Lỗi", Toast.LENGTH_SHORT);
                        toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                        toast.show();
                    }
                    dialog.dismiss();
                });
            }
            builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layout;
        public TextView tvTitle, tvNote, tvLabel;
        public CardView cvLabel;
        public ImageView imgDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            layout = itemView.findViewById(R.id.item_container);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvNote = itemView.findViewById(R.id.tvNote);
            tvLabel = itemView.findViewById(R.id.tvLabel);
            cvLabel = itemView.findViewById(R.id.cvLabel);
            imgDelete = itemView.findViewById(R.id.imgDelete);
        }
    }
}
