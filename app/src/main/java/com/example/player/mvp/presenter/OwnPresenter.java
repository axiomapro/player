package com.example.player.mvp.presenter;

import android.text.TextUtils;
import android.webkit.URLUtil;

import androidx.appcompat.app.AlertDialog;

import com.example.player.basic.Dialog;
import com.example.player.basic.item.Item;
import com.example.player.mvp.view.MainActivity;
import com.example.player.mvp.contracct.OwnContract;
import com.example.player.mvp.model.AudioModel;
import com.example.player.mvp.model.OwnModel;
import com.example.player.mvp.view.OwnFragment;

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
        dialog.delete(new Dialog.Delete() {
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
                            view.addItem(new Item(id,1,name,url,false));
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
