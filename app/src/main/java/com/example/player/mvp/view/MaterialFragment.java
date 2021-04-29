package com.example.player.mvp.view;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
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

import com.example.player.PlayerService;
import com.example.player.R;
import com.example.player.basic.item.Item;
import com.example.player.basic.notification.CreateNotification;
import com.example.player.basic.notification.Track;
import com.example.player.mvp.contracct.MaterialContract;
import com.example.player.mvp.presenter.MaterialPresenter;
import com.gauravk.audiovisualizer.visualizer.BarVisualizer;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.List;

public class MaterialFragment extends Fragment implements MaterialContract.View {

    /*
        - banner ads
    */
    private MaterialContract.Presenter presenter;
    private RippleBackground rippleBackground;
    private ImageView ivAudio;
    private BarVisualizer visualizer;
    private MediaPlayer player;
    private NotificationManager notificationManager;
    private String url;
    private boolean statusPlaying;

    public static MaterialFragment newInstance(String url) {
        MaterialFragment fragment = new MaterialFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url",url);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_material,container,false);
        rippleBackground = v.findViewById(R.id.pulse);
        ivAudio = v.findViewById(R.id.imageViewAudio);
        visualizer = v.findViewById(R.id.visualizer);
        url = getArguments().getString("url");
        presenter = new MaterialPresenter(this);

        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},1);
        }

        createChannel();

        ivAudio.setOnClickListener(v1 -> {
            audio();
        });
        return v;
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CreateNotification.CHANNEL_ID,"MediaPlayer",NotificationManager.IMPORTANCE_LOW);
            notificationManager = getActivity().getSystemService(NotificationManager.class);

            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void audio() {
        if (statusPlaying) {
            statusPlaying = false;
            ivAudio.setImageResource(R.drawable.ic_play_white);
            rippleBackground.stopRippleAnimation();
            stopPlayer();
        } else {
            statusPlaying = true;
            if (player == null) {
                player = MediaPlayer.create(getContext(), Uri.parse(url));
                int audioSessionId = player.getAudioSessionId();
                if (audioSessionId != -1) visualizer.setAudioSessionId(audioSessionId);
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (visualizer != null) visualizer.hide();
                        rippleBackground.stopRippleAnimation();
                        ivAudio.setImageResource(R.drawable.ic_play_white);
                        stopPlayer();
                    }
                });

                List<Track> track = new ArrayList<>();
                track.add(new Track("Track name"));
                CreateNotification.createNotification(getContext(),track.get(0),R.drawable.ic_stop);

//                Intent intent = new Intent(getActivity(), PlayerService.class);
//                intent.putExtra("track",url);
//                getActivity().startService(intent);
            }

            ivAudio.setImageResource(R.drawable.ic_stop_white);
            rippleBackground.startRippleAnimation();
            player.start();
        }
    }

    private void stopPlayer() {
        if (player != null) {
            // getActivity().stopService(new Intent(getActivity(),PlayerService.class));
            statusPlaying = false;
            player.seekTo(0);
            player.stop();
            player.release();
            player = null;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopPlayer();
        if (visualizer != null) visualizer.release();
        presenter.detach();
    }
}
