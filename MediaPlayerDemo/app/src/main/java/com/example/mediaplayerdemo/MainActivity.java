package com.example.mediaplayerdemo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;

import com.example.mediaplayerdemo.adapter.SongAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SongAdapter.OnNoteListener {
    RecyclerView rv_songs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        rv_songs = findViewById(R.id.rv_list_song);

        runtimePermission();
    }

    private void displaySongs() {
        rv_songs.setLayoutManager(new LinearLayoutManager(this));
        rv_songs.setAdapter(new SongAdapter(findSong(Environment.getExternalStorageDirectory()), this));
    }

    public void runtimePermission() {
        Dexter.withContext(this).withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        displaySongs();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                }).check();
    }

    public ArrayList<File> findSong(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        for (File fileSingle : files) {
            if (fileSingle.isDirectory() && !fileSingle.isHidden()) {
                arrayList.addAll(findSong(fileSingle));
            } else {
                if (fileSingle.getName().endsWith(".mp3")) {
                    arrayList.add(fileSingle);
                }
            }
        }
        return arrayList;
    }

    @Override
    public void onNoteClick(int i) {
    }
}