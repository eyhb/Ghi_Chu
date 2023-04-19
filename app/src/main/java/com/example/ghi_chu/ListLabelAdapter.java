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
import android.widget.TextView;
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
            convertView = inflater.inflate(R.layout.list_labels_item, null, true);
            holder = new ViewHolder();
            holder.edLabel = convertView.findViewById(R.id.edLabel);
            holder.imgLabel = convertView.findViewById(R.id.imgLabel);
            holder.imgDelete = convertView.findViewById(R.id.imgDelete);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position == 0) {

            // Set top view of the list
            holder.imgLabel.setImageResource(R.drawable.ic_add);
            holder.imgLabel.setOnClickListener(v -> {
                showKeyboard();
                holder.edLabel.requestFocus();
            });
            holder.edLabel.setHint("Tạo nhãn mới");
            holder.imgDelete.setVisibility(View.INVISIBLE);
            holder.edLabel.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    holder.imgLabel.setImageResource(R.drawable.ic_cancel);
                    holder.imgLabel.setOnClickListener(v1 -> {
                        holder.edLabel.setText("");
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
                            if (db.insertLabel(label)) {
                                label.setId(db.getLabelByLabel(edLabel).getId());
                                list.add(1, label);
                                notifyDataSetChanged();
                                showToast(getContext(), "Đã thêm " + label.getLabel() + " " + label.getId());
                                context.setResult(Activity.RESULT_OK);
                            } else {
                                showToast(getContext(), "Nhãn đã có " + label.getLabel() + " " + label.getId());
                            }
                        }
                        holder.edLabel.setText("");
                        holder.edLabel.clearFocus();
                        closeKeyboard();
                    });
                } else {
                    holder.imgLabel.setImageResource(R.drawable.ic_add);
                    holder.imgLabel.setOnClickListener(v13 -> {
                        showKeyboard();
                        holder.edLabel.requestFocus();
                    });
                    holder.edLabel.setHint("Tạo nhãn mới");
                    holder.imgDelete.setVisibility(View.INVISIBLE);
                    closeKeyboard();
                }
            });
        } else {
            final String[] labelFocus = {list.get(position).getLabel()};
            holder.edLabel.setText(labelFocus[0]);
            holder.imgDelete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Xóa nhãn?");
                builder.setPositiveButton("OK", (dialog, which) -> {
                    if (db.deleteLabel(labelFocus[0])) {
                        list.remove(position);
                        notifyDataSetChanged();
                        showToast(context, "Đã xóa");
                        dialog.dismiss();
                        context.setResult(Activity.RESULT_OK);
                    } else {
                        showToast(context, "Lỗi! Thử lại sau.");
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
                        if (!labelFocus[0].equals(list.get(position).getLabel())) {
                            if (db.updateLabel(list.get(position), labelFocus[0])) {
                                labelFocus[0] = list.get(position).getLabel();
                                showToast(getContext(), "Đã sửa " + list.get(position).getLabel() + " " + list.get(position).getId());
                                notifyDataSetChanged();
                                context.setResult(Activity.RESULT_OK);
                            } else {
                                showToast(getContext(), "Nhãn đã có " + list.get(position).getLabel() + " " + list.get(position).getId());
                                holder.edLabel.setText(labelFocus[0]);
                            }
                        }
                    } else {
                        list.get(position).setLabel(labelFocus[0]);
                        holder.edLabel.setText(labelFocus[0]);
                    }
                    holder.imgDelete.setImageResource(R.drawable.ic_trash);
                    holder.imgDelete.setColorFilter(context.getResources().getColor(R.color.icon), PorterDuff.Mode.SRC_ATOP);
                    holder.imgDelete.setOnClickListener(v15 -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Xóa nhãn?");
                        builder.setPositiveButton("OK", (dialog, which) -> {
                            if (db.deleteLabel(labelFocus[0])) {
                                list.remove(position);
                                notifyDataSetChanged();
                                showToast(context, "Đã xóa");
                                dialog.dismiss();
                                context.setResult(Activity.RESULT_OK);
                            } else {
                                showToast(context, "Lỗi! Thử lại sau.");
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

    private void showToast(Context context, String text) {
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        View view = toast.getView();
        TextView textView = view.findViewById(android.R.id.message);
        textView.setBackgroundColor(Color.TRANSPARENT);
        textView.setTextColor(Color.DKGRAY);
        toast.show();
    }

    class ViewHolder {
        EditText edLabel;
        ImageView imgLabel;
        ImageView imgDelete;
    }

    public void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    public void closeKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
