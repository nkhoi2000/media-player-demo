package com.example.mediaplayerdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {
    private Button btn_play, btn_prev, btn_rewind, btn_forward, btn_next;
    private TextView txt_name, txt_start, txt_stop;
    private SeekBar seekBar;

    private static MediaPlayer mediaPlayer;
    private int position;
    Thread thread;
    private ArrayList<File> songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        containsComponent();
        setMediaPlayer();
    }

    private void containsComponent() {
        btn_forward = findViewById(R.id.btn_forward);
        btn_next = findViewById(R.id.btn_next);
        btn_play = findViewById(R.id.btn_play);
        btn_prev = findViewById(R.id.btn_prev);
        btn_rewind = findViewById(R.id.btn_rewind);

        txt_name = findViewById(R.id.txt_name);
        txt_start = findViewById(R.id.txt_sstart);
        txt_stop = findViewById(R.id.txt_sstop);

        seekBar = findViewById(R.id.seekbar);
    }

    private void setMediaPlayer() {
        if (mediaPlayer != null) {
            stopMusic();
        }
        /* get songs */
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList) bundle.getParcelableArrayList("songs"); //get song lists
        position = bundle.getInt("pos", 0); //get song position in lists

        /* play music */
        playMusic(songs, position);


        /* button play */
        btn_play.setOnClickListener(view -> pauseMusic());

        /* button next */
        btn_next.setOnClickListener(view -> nextMusic());

        /* button previous */
        btn_prev.setOnClickListener(view -> previousMusic());

        /* button fast forward */
        btn_forward.setOnClickListener(view -> fastForward());

        /* button rewind */
        btn_rewind.setOnClickListener(view -> rewind());
    }

    private void setSongName(String name) {
        txt_name.setText(name.replace(".mp3", ""));
        txt_name.setSelected(true);
        txt_name.setEllipsize(TextUtils.TruncateAt.MARQUEE);
    }

    private void stopMusic() {
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    private void nextMusic() {
        stopMusic();
        position = ((position + 1) % songs.size());
        playMusic(songs, position);
    }

    private void pauseMusic() {
        if (mediaPlayer.isPlaying()) {
            btn_play.setBackgroundResource(R.drawable.ic_baseline_pause);
            mediaPlayer.pause();
        } else {
            btn_play.setBackgroundResource(R.drawable.ic_baseline_play);
            mediaPlayer.start();
            mediaPlayer.setLooping(true);
        }
    }

    private void fastForward() {
        int currentPos = mediaPlayer.getCurrentPosition();
        int duration = mediaPlayer.getDuration();
        if (currentPos < duration) {
            mediaPlayer.seekTo(currentPos + 10000);
        }
    }

    private void rewind() {
        int currentPos = mediaPlayer.getCurrentPosition();
        if (currentPos > 0) {
            mediaPlayer.seekTo(currentPos - 10000);
        }
    }

    private void previousMusic() {
        stopMusic();
        if (position == 0) {
            position = songs.size() - 1;
        } else {
            position--;
        }
        playMusic(songs, position);
    }

    private void playMusic(ArrayList<File> songs, int position) {
        setSongName(songs.get(position).getName());
        btn_play.setBackgroundResource(R.drawable.ic_baseline_play);

        Uri uri = Uri.parse(songs.get(position).toString()); //get location of song

        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        mediaPlayer.start();

        updateSeekbar();

        mediaPlayer.setOnCompletionListener(mediaPlayer -> nextMusic());
    }

    private void updateSeekbar() {
        seekBar.setMax(mediaPlayer.getDuration());
        thread = new Thread() {
            @Override
            public void run() {
                int currentPos = mediaPlayer.getCurrentPosition();
                int totalDuration = mediaPlayer.getDuration();
                while (currentPos < totalDuration) {
                    try {
                        sleep(500);
                        currentPos = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPos);

                    } catch (InterruptedException | IllegalStateException e) {
                        Log.d("seekbar", "run: " + e.toString());
                    }
                }
            }
        };
        thread.start();
        timer();
        seekbarChange();
    }

    private void timer() {
        txt_stop.setText(time(mediaPlayer.getDuration()));
        final Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                txt_start.setText(time(mediaPlayer.getCurrentPosition()));
                handler.postDelayed(this, 500);
            }
        }, 500);
    }

    private void seekbarChange() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    private String time(int duration) {
        String timer = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timer += min + ":";
        if (sec < 10) {
            timer += "0";
        }
        timer += sec;

        return timer;
    }
}