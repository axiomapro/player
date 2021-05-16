package com.example.player.mvp.clock;

import android.util.Log;

import androidx.appcompat.app.AlertDialog;

import com.example.player.basic.backend.Alarm;
import com.example.player.basic.backend.DateTimeManager;
import com.example.player.basic.backend.Dialog;
import com.example.player.basic.list.Item;
import com.example.player.mvp.main.MainActivity;

import java.util.List;

public class ClockPresenter implements ClockContract.Presenter {

    private ClockContract.View view;
    private final ClockModel model;
    private final Dialog dialog;
    private final DateTimeManager dateTimeManager;
    private final Alarm alarm;

    public ClockPresenter(ClockContract.View view) {
        this.view = view;
        this.model = new ClockModel(((ClockFragment) view).getContext());
        this.dialog = new Dialog(((ClockFragment) view).getContext());
        this.dateTimeManager = new DateTimeManager();
        this.alarm = new Alarm(((ClockFragment) view).getContext());
    }

    @Override
    public List<Item> getList() {
        List<Item> list = model.getList();
        for (int i = 0; i < list.size(); i++) {
            String date = list.get(i).getDate();
            list.get(i).setDate(dateTimeManager.getRuDate(date));
        }
        return list;
    }

    @Override
    public void updateStatusClock() {
        model.updateStatus(dateTimeManager.getDateTime());
    }

    @Override
    public void showDeleteDialog(int id,int position) {
        dialog.delete(new Dialog.Delete() {
            @Override
            public void yes() {
                model.delete(id);
                alarm.cancel(id);
                view.removeItem(position);
            }
        });
    }

    @Override
    public void showAddDialog() {
        dialog.clock(model.getAudioList(), new Dialog.Clock() {
            @Override
            public void date(String date, Dialog.ClockByStep listener) {
                if (dateTimeManager.days(date) > 0) view.showMessage("Выбранная дата уже прошла");
                else listener.nextStep();
            }

            @Override
            public void time(String date,String time, Dialog.ClockByStep listener) {
                if (dateTimeManager.days(date) == 0 && !dateTimeManager.time(time)) view.showMessage("Выбранное время уже прошло");
                else listener.nextStep();
            }

            @Override
            public void track(AlertDialog dialog, String name, String date, String time, int media) {
                if (media == 0) view.showMessage("Выберите трек");
                else {
                    String datetime = dateTimeManager.restoreDateTime(date+" "+time);
                    model.add(name, media, datetime, new ClockModel.Model() {
                        @Override
                        public void onSuccess(int id) {
                            dialog.dismiss();
                            view.addItem(new Item(id,name,dateTimeManager.getRuDate(datetime),true));
                            alarm.set(alarm.parseDateTime(datetime),id);
                        }

                        @Override
                        public void onDuplicate() {
                            view.showMessage("Такое напоминание уже есть");
                        }
                    });
                }
            }
        });
    }

    @Override
    public void detach() {
        view = null;
    }
}
