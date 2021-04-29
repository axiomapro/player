package com.example.player.basic;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.player.R;
import com.example.player.basic.item.Item;
import com.example.player.basic.item.RecyclerViewAdapter;
import com.example.player.mvp.view.MainActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.List;

public class Dialog {

    private final Context context;

    public interface Delete {
        void yes();
    }

    public interface Clock {
        void result(AlertDialog dialog, String name, String date, String time, int media);
    }

    public interface Own {
        void result(AlertDialog dialog, String name, String url);
    }

    public Dialog(Context context) {
        this.context = context;
    }

    public void clock(List<Item> list, Clock listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_clock,null);
        TextView tvDate = v.findViewById(R.id.textViewDate);
        TextView tvTime = v.findViewById(R.id.textViewTime);
        DatePicker datePicker = v.findViewById(R.id.datePicker);
        TimePicker timePicker = v.findViewById(R.id.timePicker);
        TextInputLayout tilName = v.findViewById(R.id.textInputLayoutName);
        TextView tvAudioName = v.findViewById(R.id.textViewAudioName);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        AppCompatButton buttonCancel = v.findViewById(R.id.buttonCancel);
        AppCompatButton buttonOk = v.findViewById(R.id.buttonOk);
        builder.setView(v);

        final int[] media = new int[1];
        final String[] date = new String[1];
        final String[] time = new String[1];
        RecyclerViewAdapter adapter = new RecyclerViewAdapter("dialog-audio",list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        dialog.show();

        tvDate.setOnClickListener(v13 -> {
            datePicker.setVisibility(View.VISIBLE);
            timePicker.setVisibility(View.GONE);
        });

        tvTime.setOnClickListener(v14 -> {
            datePicker.setVisibility(View.GONE);
            timePicker.setVisibility(View.VISIBLE);
        });

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        date[0] = year+"-"+(month+1)+"-"+day;
        time[0] = hour+":"+minute+":00";
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date[0] = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
            }
        });

        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minuteOfHour) {
                time[0] = hourOfDay+":"+minuteOfHour+":00";
            }
        });

        adapter.setClickListener(new RecyclerViewAdapter.RecyclerViewItem() {
            @Override
            public void onItemClick(int position) {
                tvAudioName.setText(list.get(position).getName());
                media[0] = list.get(position).getId();
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onFavourite(int position) {

            }
        });

        buttonCancel.setOnClickListener(v1 -> dialog.dismiss());

        buttonOk.setOnClickListener(v12 -> {
            String name = tilName.getEditText().getText().toString().trim();
            Log.d(MainActivity.LOG,"name: "+name+"; date: "+date[0]+"; time: "+time[0]);
            listener.result(dialog,name, date[0], time[0],media[0]);
        });
    }

    public void own(Own listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_own,null);
        TextInputLayout tilName = v.findViewById(R.id.textInputLayoutName);
        TextInputLayout tilUrl = v.findViewById(R.id.textInputLayoutUrl);
        AppCompatButton button = v.findViewById(R.id.button);
        builder.setView(v);

        AlertDialog dialog = builder.create();
        dialog.show();

        button.setOnClickListener(v1 -> {
            String name = tilName.getEditText().getText().toString().trim();
            String url = tilUrl.getEditText().getText().toString().trim();
            Log.d(MainActivity.LOG,"name: "+name+"; url: "+url);
            listener.result(dialog,name,url);
        });
    }

    public void about(String message,String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_about,null);
        TextView tvMessage = v.findViewById(R.id.textViewMessage);
        Button button = v.findViewById(R.id.button);
        tvMessage.setText(message);
        builder.setView(v);

        AlertDialog dialog = builder.create();
        dialog.show();

        button.setOnClickListener(v1 -> {
            dialog.dismiss();
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:")); // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL, email);
            intent.putExtra(Intent.EXTRA_SUBJECT, "Написать разработчику");
            context.startActivity(intent);
        });
    }

    public void delete(Delete listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Удалить запись")
                .setMessage("Вы уверены что хотите удалить запись")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        listener.yes();
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }

}
