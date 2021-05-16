package com.example.player.mvp.own;

import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.appcompat.app.AlertDialog;

import com.example.player.basic.backend.Dialog;
import com.example.player.basic.list.Item;

import java.util.List;

public class OwnPresenter implements OwnContract.Presenter {

    private OwnContract.View view;
    private final OwnModel model;
    private final Dialog dialog;

    public OwnPresenter(OwnContract.View view) {
        this.view = view;
        this.model = new OwnModel(((OwnFragment) view).getContext());
        this.dialog = new Dialog(((OwnFragment) view).getContext());
    }

    @Override
    public List<Item> getList() {
        return model.getList();
    }

    @Override
    public void toggleFavourite(int id,int position) {
        view.updateItem(position,model.toggleFavourite(id));
    }

    @Override
    public void showDeleteDialog(int id,int position) {
        dialog.delete("Удалить аудио","Вы уверены что хотите удалить аудио?",new Dialog.Delete() {
            @Override
            public void yes() {
                model.delete(id);
                view.removeItem(position);
            }
        });
    }

    @Override
    public void showAddDialog() {
        dialog.own(new Dialog.Own() {
            @Override
            public void result(AlertDialog dialog, String name, String url) {
                if (TextUtils.isEmpty(name)) view.showMessage("Напишите название");
                else if (!URLUtil.isValidUrl(url)) view.showMessage("Напишите правильный URL");
                else {
                    model.add(name, url, new OwnModel.Model() {
                        @Override
                        public void onSuccess(int id) {
                            dialog.dismiss();
                            view.addItem(new Item(id,1,name,null,url,false));
                        }

                        @Override
                        public void onDuplicate() {
                            view.showMessage("Такая запись уже есть");
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
