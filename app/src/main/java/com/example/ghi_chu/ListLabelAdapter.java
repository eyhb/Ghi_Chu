package com.example.ghi_chu;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.ghich.R;

import java.util.List;

public class ListLabelAdapter extends ArrayAdapter<Label> {
    Activity context;
    List<Label> list;
    LayoutInflater inflater;
    DBHelper db;

    public ListLabelAdapter(Activity context, List<Label> list) {
        super(context, R.layout.list_labels_item, list);
        this.context = context;
        this.list = list;
        list.add(0, new Label());
        db = new DBHelper(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Label getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            inflater = context.getLayoutInflater();
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.list_labels_item, null, true);
            holder.edLabel = convertView.findViewById(R.id.edLabel);
            holder.imgLabel = convertView.findViewById(R.id.imgLabel);
            holder.imgDelete = convertView.findViewById(R.id.imgDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {
            holder.imgLabel.setImageResource(R.drawable.ic_add);
            holder.imgLabel.setOnClickListener(v -> {
//                    showKeyboard();
                holder.edLabel.requestFocus();
            });
            holder.edLabel.setHint("Tạo nhãn mới");
            holder.imgDelete.setVisibility(View.INVISIBLE);
            holder.edLabel.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    holder.imgLabel.setImageResource(R.drawable.ic_cancel);
                    holder.imgLabel.setOnClickListener(v1 -> {
                        holder.edLabel.clearFocus();
                        closeKeyboard();
                    });
                    holder.imgDelete.setImageResource(R.drawable.ic_done);
                    holder.imgDelete.setVisibility(View.VISIBLE);
                    holder.imgDelete.setOnClickListener(v12 -> {
                        String edLabel = holder.edLabel.getText().toString().trim();
                        if (!edLabel.equals("")) {
                            Label label = new Label();
                            label.setLabel(edLabel);
                            if (db.insertLable(label)) {
                                list.add(1, label);
                                notifyDataSetChanged();
                                Toast toast = Toast.makeText(getContext(), "Đã thêm", Toast.LENGTH_SHORT);
                                toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                                toast.show();
                                context.setResult(Activity.RESULT_OK);
                            } else {
                                Toast toast = Toast.makeText(getContext(), "Nhãn đã có", Toast.LENGTH_SHORT);
                                toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                                toast.show();
                            }
                        }
                        holder.edLabel.setText("");
                        holder.edLabel.clearFocus();
                        closeKeyboard();
                    });
                } else {
                    holder.imgLabel.setImageResource(R.drawable.ic_add);
                    holder.imgLabel.setOnClickListener(v13 -> {
//                                showKeyboard();
                        holder.edLabel.requestFocus();
                    });
                    holder.edLabel.setHint("Tạo nhãn mới");
                    holder.imgDelete.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            final String[] label = {list.get(position).getLabel()};
            holder.edLabel.setText(label[0]);
            holder.imgDelete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xóa nhãn?");
                builder.setPositiveButton("OK", (dialog, which) -> {
                    if (db.deleteLabel(label[0])) {
                        list.remove(position);
                        notifyDataSetChanged();
                        Toast toast = Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT);
                        toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.DKGRAY);
                        toast.show();
                        dialog.dismiss();
                        context.setResult(Activity.RESULT_OK);
                    } else {
                        Toast toast = Toast.makeText(context, "Lỗi! Thử lại sau.", Toast.LENGTH_SHORT);
                        toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                        toast.show();
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
                AlertDialog dialog = builder.create();
                dialog.show();
            });
            holder.edLabel.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    holder.edLabel.setSelection(holder.edLabel.getText().length());
                    holder.edLabel.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            if (!s.toString().trim().equals("")) {
                                list.get(position).setLabel(s.toString().trim());
                            }
                        }
                    });
                    holder.imgDelete.setImageResource(R.drawable.ic_done);
                    holder.imgDelete.setColorFilter(context.getResources().getColor(R.color.yellow), PorterDuff.Mode.SRC_ATOP);
                    holder.imgDelete.setOnClickListener(v14 -> {
                        holder.edLabel.clearFocus();
                        closeKeyboard();
                    });
                } else {
                    if (!holder.edLabel.getText().toString().trim().equals("")) {
                        if (!label[0].equals(list.get(position).getLabel())) {
                            if (db.updateLabel(list.get(position), label[0])) {
                                label[0] = list.get(position).getLabel();
                                Toast toast = Toast.makeText(getContext(), "Đã sửa", Toast.LENGTH_SHORT);
                                toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                                toast.show();
                                context.setResult(Activity.RESULT_OK);
                            } else {
                                Toast toast = Toast.makeText(getContext(), "Nhãn đã có", Toast.LENGTH_SHORT);
                                toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                                toast.show();
                                holder.edLabel.setText(label[0]);
                            }
                        } else {
                            holder.edLabel.setText(label[0]);
                        }
                    } else {
                        list.get(position).setLabel(label[0]);
                        holder.edLabel.setText(label[0]);
                    }
                    holder.imgDelete.setImageResource(R.drawable.ic_trash);
                    holder.imgDelete.setColorFilter(context.getResources().getColor(R.color.icon), PorterDuff.Mode.SRC_ATOP);
                    holder.imgDelete.setOnClickListener(v15 -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Xóa nhãn?");
                        builder.setPositiveButton("OK", (dialog, which) -> {
                            if (db.deleteLabel(label[0])) {
                                list.remove(position);
                                notifyDataSetChanged();
                                Toast toast = Toast.makeText(context, "Đã xóa", Toast.LENGTH_SHORT);
                                toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                                toast.show();
                                dialog.dismiss();
                                context.setResult(Activity.RESULT_OK);
                            } else {
                                Toast toast = Toast.makeText(context, "Lỗi! Thử lại sau.", Toast.LENGTH_SHORT);
                                toast.getView().findViewById(android.R.id.message).setBackgroundColor(Color.TRANSPARENT);
                                toast.show();
                                dialog.dismiss();
                            }
                        });
                        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    });
                }
            });
        }
        return convertView;
    }

    class ViewHolder {
        EditText edLabel;
        ImageView imgLabel;
        ImageView imgDelete;
    }

//    public void showKeyboard() {
//        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
//    }

    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, InputMethodManager.SHOW_IMPLICIT);
    }
}
