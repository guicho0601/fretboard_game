package com.lrgt.fretboardgame;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameActivity extends AppCompatActivity {

    private static final String TAG = GameActivity.class.getCanonicalName();

    private static final int PERMISSION_REQUEST_RECORD_AUDIO = 443;

    private Notes mNotes;
    private AudioProcessor mAudioProcessor;
    private ExecutorService mExecutor = Executors.newSingleThreadExecutor();
    private CountDownTimer mCountDownTimer;

    private int level = 0;
    private int time = 10000;
    private int notesCount = 0;
    private int points = 0;
    private boolean keyFound = false;


    private TextView note;
    private TextView score;
    private ProgressBar timeProgressBar;
    private String currentNote;

    private boolean mProcessing = false;


    @Override
    protected void onStart() {
        super.onStart();
        if (Utils.checkPermission(this, Manifest.permission.RECORD_AUDIO)) {
            startGame();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProcessing) {
            mAudioProcessor.stop();
            mProcessing = false;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void requestPermissions() {
        if (!Utils.checkPermission(this, Manifest.permission.RECORD_AUDIO)) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {

                new AlertDialog.Builder(this)
                        .setTitle(R.string.permission)
                        .setMessage(getString(R.string.permission_record_audio))
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(GameActivity.this,
                                        new String[]{Manifest.permission.RECORD_AUDIO},
                                        PERMISSION_REQUEST_RECORD_AUDIO);
                            }
                        })
                        .show();

            } else {

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.RECORD_AUDIO},
                        PERMISSION_REQUEST_RECORD_AUDIO);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_RECORD_AUDIO) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGame();
            }
        }
    }

    private void startAudioProcessing() {
        if (mProcessing)
            return;

        mAudioProcessor = new AudioProcessor();
        mAudioProcessor.init();
        mAudioProcessor.setPitchDetectionListener(new AudioProcessor.PitchDetectionListener() {
            @Override
            public void onPitchDetected(final float freq, double avgIntensity) {

                final int index = mNotes.closestPitchIndex(freq);
                final Pitch pitch = mNotes.pitches.get(index);
                runOnUiThread(new Runnable() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void run() {
                        if (pitch.name != null) {
                            if (currentNote.equals(pitch.name) && !keyFound) {
                                mCountDownTimer.cancel();
                                points += 100;
                                keyFound = true;
                                startNewNote();
                            }
                        }
                    }
                });
            }
        });
        mProcessing = true;
        mExecutor.execute(mAudioProcessor);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mNotes = new Notes();
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            level = extras.getInt("level");
        }
        score = findViewById(R.id.score);
        note = findViewById(R.id.note);
        timeProgressBar = findViewById(R.id.timeBar);
        requestPermissions();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private int randomPosition() {
        return new Random().nextInt(12);
    }

    private void startNewNote() {
        if (notesCount < 30) {
            keyFound = false;
            notesCount++;
            currentNote = mNotes.notes.get(randomPosition());
            note.setText(String.format("%s", currentNote));
            switch (level) {
                case 1:
                    time = 10000;
                    break;
                case 2:
                    time = 5000;
                    break;
                case 3:
                    time = 2000;
                    break;
            }
            score.setText(String.format("%d", points));
            int timeProgress = time / 1000;
            timeProgressBar.setVisibility(View.VISIBLE);
            timeProgressBar.setMax(timeProgress);
            timeProgressBar.setProgress(timeProgress);
            mCountDownTimer = new CountDownTimer(time, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    timeProgressBar.setProgress((int) (millisUntilFinished / 1000));
                }

                @Override
                public void onFinish() {
                    timeProgressBar.setProgress(0);
                    startNewNote();
                }
            };
            mCountDownTimer.start();
        } else {
            note.setText(String.format("%s: %d", getString(R.string.your_score), points));
            timeProgressBar.setVisibility(View.INVISIBLE);
            score.setText("");
        }
    }

    private void startGame() {
        new CountDownTimer(4000, 1000) {

            public void onTick(long millisUntilFinished) {
                if ((int) millisUntilFinished == 0) {
                    note.setText(getString(R.string.go));
                } else if (millisUntilFinished == 4000) {
                    note.setText(getString(R.string.ready));
                } else {
                    note.setText(String.format("%d", (millisUntilFinished / 1000)));
                }
            }

            public void onFinish() {
                startAudioProcessing();
                startNewNote();
            }
        }.start();
    }

}
