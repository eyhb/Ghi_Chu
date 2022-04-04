package com.example.ghich;

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
        int p = position;
        title = list.get(p).getTitle();
        label = list.get(p).getLabel();
        if (!title.equals("")) {
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(list.get(p).getTitle());
        } else {
            holder.tvTitle.setVisibility(View.GONE);
        }
        holder.tvNote.setText(list.get(p).getNote());
        if (!label.equals("")) {
            holder.cvLabel.setVisibility(View.VISIBLE);
            holder.tvLabel.setText(list.get(p).getLabel());
        } else {
            holder.cvLabel.setVisibility(View.GONE);
        }
        holder.layout.requestLayout();
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (list.get(p).getTrash() == 0) {
                    Intent intent = new Intent(context, AddNoteActivity.class);
                    intent.putExtra("noteId", list.get(p).getId());
                    ((Activity) context).startActivityForResult(intent, 10001);
                }
            }
        });
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (list.get(p).getTrash() == 1) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Khôi phục?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            list.get(p).setTrash(0);
                            if (db.updateNote(list.get(p))) {
                                list.remove(p);
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
                        }
                    });
                    builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
                return false;
            }
        });
        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                if (list.get(p).getTrash() == 0) {
                    builder.setTitle("Chuyển vào thùng rác?");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            list.get(p).setTrash(1);
                            if (db.updateNote(list.get(p))) {
                                list.remove(p);
                                notifyDataSetChanged();
                                Toast toast = Toast.makeText(context, "Đã chuyển vào thùng rác", Toast.LENGTH_SHORT);
                                toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                                toast.show();
                                dialog.dismiss();
                            } else {
                                Toast toast = Toast.makeText(context, "Lỗi", Toast.LENGTH_SHORT);
                                toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                                toast.show();
                                dialog.dismiss();
                            }
                        }
                    });
                } else {
                    builder.setTitle("Xác nhận xóa");
                    builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (db.deleteNote(list.get(p).getId())) {
                                list.remove(p);
                                notifyDataSetChanged();
                                Toast toast = Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT);
                                toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                                toast.show();
                                dialog.dismiss();
                            } else {
                                Toast toast = Toast.makeText(context, "Lỗi", Toast.LENGTH_SHORT);
                                toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                                toast.show();
                                dialog.dismiss();
                            }
                        }
                    });
                }
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
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
