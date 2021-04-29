package com.example.player.mvp.presenter;

import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import com.example.player.basic.Alarm;
import com.example.player.basic.DateTimeManager;
import com.example.player.basic.Dialog;
import com.example.player.basic.item.Item;
import com.example.player.mvp.view.ClockFragment;
import com.example.player.mvp.contracct.ClockContract;
import com.example.player.mvp.model.ClockModel;

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
            public void result(AlertDialog dialog, String name, String date, String time, int media) {
                if (TextUtils.isEmpty(name)) view.showMessage("Напишите название");
                else if (media == 0) view.showMessage("Выберите трек");
                else if (date == null) view.showMessage("Выберите дату");
                else if (dateTimeManager.days(date) > 0) view.showMessage("Дата уже прошла");
                else if (time == null) view.showMessage("Укажите время");
                else if (dateTimeManager.days(date) <= 0) {
                    if (dateTimeManager.time(time) || dateTimeManager.days(date) < 0) {
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
                    } else view.showMessage("Время уже прошло");
                }
            }
        });
    }

    @Override
    public void detach() {
        view = null;
    }
}
