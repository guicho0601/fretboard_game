package com.lrgt.fretboardgame;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.lrgt.fretboardgame.Utils.totalNotes;

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
    private int successes = 0;
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
            mCountDownTimer.cancel();
            notesCount = 100;
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
                                displayNoteFound(true);
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
        note.setTypeface(Utils.getTypeface(this, Utils.FONTAWESOME));
        timeProgressBar = findViewById(R.id.timeBar);
        requestPermissions();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private int randomPosition() {
        return new Random().nextInt(12);
    }

    private void startNewNote() {
        String levelName = Utils.easyLevelName;
        switch (level) {
            case 1:
                time = 10000;
                break;
            case 2:
                levelName = Utils.mediumLevelName;
                time = 6000;
                break;
            case 3:
                levelName = Utils.hardLevelName;
                time = 3000;
                break;
        }
        note.setTextColor(getResources().getColor(R.color.colorInfo));
        if (notesCount < totalNotes) {
            keyFound = false;
            notesCount++;
            currentNote = mNotes.notes.get(randomPosition());
            note.setText(String.format("%s", currentNote));
            score.setText(String.format(Locale.getDefault(), "%d/%d", successes, notesCount));
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
                    displayNoteFound(false);
                }
            };
            mCountDownTimer.start();
        } else {
            note.setText(String.format(Locale.getDefault(), "%s: %d/%d", getString(R.string.your_score), successes, totalNotes));
            timeProgressBar.setVisibility(View.INVISIBLE);
            score.setText("");
            SharedPreferences prefs = Utils.getPrefs(this);
            if (prefs.getInt(levelName, 0) < successes) {
                SharedPreferences.Editor editor = prefs.edit();
                editor.putInt(levelName, successes);
                editor.apply();
            }
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    onBackPressed();
                }
            }, 3000);
        }
    }

    private void startGame() {
        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                note.setText(String.format(Locale.getDefault(), "%d", ((millisUntilFinished / 1000) + 1)));
            }

            public void onFinish() {
                startAudioProcessing();
                startNewNote();
            }
        }.start();
    }

    private void displayNoteFound(boolean success) {
        timeProgressBar.setVisibility(View.INVISIBLE);
        MediaPlayer mediaPlayer;
        if (success) {
            mCountDownTimer.cancel();
            successes++;
            keyFound = true;
            note.setTextColor(getResources().getColor(R.color.colorSuccess));
            note.setText(R.string.fa_check_circle);
            mediaPlayer = MediaPlayer.create(this, R.raw.success);
        } else {
            note.setTextColor(getResources().getColor(R.color.colorDanger));
            note.setText(R.string.fa_times_circle);
            mediaPlayer = MediaPlayer.create(this, R.raw.buzzer);
        }
        mediaPlayer.start();
        new Handler().postDelayed(new Runnable() {
            public void run() {
                startNewNote();
            }
        }, 2000);
    }

}
