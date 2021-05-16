package com.example.player.mvp.material;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.player.basic.backend.Constant;
import com.example.player.basic.service.PlayerService;
import com.example.player.R;
import com.example.player.basic.backend.Param;
import com.example.player.mvp.main.MainActivity;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.skyfishjy.library.RippleBackground;

public class MaterialFragment extends Fragment implements MaterialContract.View {

    /*
        - banner ads
    */
    private MaterialContract.Presenter presenter;
    private RippleBackground rippleBackground;
    private ImageView ivAudio;
    private BarVisualizer visualizer;
    private static int sessionPlayerId;

    public static MaterialFragment newInstance(int id,String title) {
        MaterialFragment fragment = new MaterialFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("id",id);
        bundle.putString("title",title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_material,container,false);
        getActivity().setTitle(getArguments().getString("title"));
        rippleBackground = v.findViewById(R.id.pulse);
        ivAudio = v.findViewById(R.id.imageViewAudio);
        visualizer = v.findViewById(R.id.visualizer);
        presenter = new MaterialPresenter(this);
        int id = getArguments().getInt("id");
        Param param = new Param(getContext());

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},1);
        }

        if (id == param.getInt(Constant.PARAM_TRACK)) {
            if (PlayerService.statusRunning) play(sessionPlayerId);
            else audio();
        } else {
            param.setInt(Constant.PARAM_TRACK,id);
            if (PlayerService.statusRunning) {
                PlayerService.statusRunning = false;
                getActivity().stopService(new Intent(getActivity(),PlayerService.class));
            }
            audio();
        }

        ivAudio.setOnClickListener(v1 -> {
            audio();
        });
        return v;
    }

    public void resetPlayer() {
        PlayerService.statusRunning = false;
        getActivity().stopService(new Intent(getActivity(),PlayerService.class));
        ivAudio.setImageResource(R.drawable.ic_play_white);
        if (visualizer != null) visualizer.hide();
        rippleBackground.stopRippleAnimation();

        Intent intent = new Intent(getActivity(),PlayerService.class);
        intent.putExtra("reset",true);
        getActivity().startService(intent);
    }

    public void stop() {
        ivAudio.setImageResource(R.drawable.ic_play_white);
        if (MainActivity.visual == 1) rippleBackground.stopRippleAnimation();
        if (MainActivity.visual == 2 && visualizer != null) visualizer.hide();
    }

    public void play(int session) {
        sessionPlayerId = session;
        ivAudio.setImageResource(R.drawable.ic_pause_white);
        if (MainActivity.visual == 1) rippleBackground.startRippleAnimation();
        if (MainActivity.visual == 2 && session != -1) visualizer.setAudioSessionId(session);
    }

    @Override
    public void audio() {
        if (PlayerService.statusRunning) getActivity().stopService(new Intent(getActivity(),PlayerService.class));
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) getActivity().startForegroundService(new Intent(getActivity(), PlayerService.class));
            else getActivity().startService(new Intent(getActivity(),PlayerService.class));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (visualizer != null) visualizer.release();
        presenter.detach();
    }
}
