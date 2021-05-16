package com.example.player.basic.backend;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import com.example.player.basic.list.Item;
import com.example.player.basic.list.RecyclerViewAdapter;
import com.example.player.mvp.main.MainActivity;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.List;

public class Dialog {

    private final Context context;

    public interface Delete {
        void yes();
    }

    public interface Clock {
        void date(String date,ClockByStep listener);
        void time(String date,String time,ClockByStep listener);
        void track(AlertDialog dialog,String name,String date, String time, int media);
    }

    public interface Own {
        void result(AlertDialog dialog, String name, String url);
    }

    public interface ClockByStep {
        void nextStep();
    }

    public Dialog(Context context) {
        this.context = context;
    }

    public void clock(List<Item> list, Clock listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_clock,null);
        TextView tvTitle = v.findViewById(R.id.textViewTitle);
        DatePicker datePicker = v.findViewById(R.id.datePicker);
        TimePicker timePicker = v.findViewById(R.id.timePicker);
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        AppCompatButton buttonCancel = v.findViewById(R.id.buttonCancel);
        builder.setView(v);

        final String[] date = new String[1];
        final String[] time = new String[1];
        RecyclerViewAdapter adapter = new RecyclerViewAdapter("dialog-audio",list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        date[0] = year+"-"+(month+1)+"-"+day;
        time[0] = hour+":"+minute+":00";
        final int[] step = {1};
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                date[0] = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                listener.date(date[0], new ClockByStep() {
                    @Override
                    public void nextStep() {
                        step[0] = 2;
                        buttonCancel.setText("Back");
                        datePicker.setVisibility(View.GONE);
                        timePicker.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        timePicker.setIs24HourView(true);
        timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minuteOfHour) {
                time[0] = hourOfDay+":"+minuteOfHour+":00";
                listener.time(date[0], time[0], new ClockByStep() {
                    @Override
                    public void nextStep() {
                        step[0] = 3;
                        tvTitle.setVisibility(View.VISIBLE);
                        timePicker.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    }
                });
            }
        });

        adapter.setClickListener(new RecyclerViewAdapter.RecyclerViewItem() {
            @Override
            public void onItemClick(int position) {
                listener.track(dialog,list.get(position).getName(),date[0],time[0],list.get(position).getId());
            }

            @Override
            public void onLongClick(int position) {

            }

            @Override
            public void onFavourite(int position) {

            }
        });

        buttonCancel.setOnClickListener(v1 -> {
            if (step[0] == 1) dialog.dismiss();
            else if (step[0] == 2) {
                step[0] = 1;
                buttonCancel.setText("Cancel");
                timePicker.setVisibility(View.GONE);
                datePicker.setVisibility(View.VISIBLE);
            }
            else if (step[0] == 3) {
                step[0] = 2;
                tvTitle.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
                timePicker.setVisibility(View.VISIBLE);
            }
        });
    }

    public void own(Own listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_own,null);
        TextInputLayout tilName = v.findViewById(R.id.textInputLayoutName);
        TextInputLayout tilUrl = v.findViewById(R.id.textInputLayoutUrl);
        AppCompatButton buttonCancel = v.findViewById(R.id.buttonCancel);
        AppCompatButton buttonAdd = v.findViewById(R.id.buttonAdd);
        builder.setView(v);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        buttonAdd.setOnClickListener(v1 -> {
            String name = tilName.getEditText().getText().toString().trim();
            String url = tilUrl.getEditText().getText().toString().trim();
            listener.result(dialog,name,url);
        });
    }

    public void about(String message,String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View v = LayoutInflater.from(context).inflate(R.layout.dialog_about,null);
        TextView tvMessage = v.findViewById(R.id.textViewMessage);
        Button button = v.findViewById(R.id.button);
        Button buttonOk = v.findViewById(R.id.buttonOk);
        tvMessage.setText(message);
        builder.setView(v);

        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();

        buttonOk.setOnClickListener(v12 -> {
            dialog.dismiss();
        });

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

        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
        dialog.show();
    }

}
