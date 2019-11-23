package com.lrgt.fretboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Locale;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private String userNameString = "username";

    private TextView usernameTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        usernameTV = findViewById(R.id.username_text);
        Button easyBtn = findViewById(R.id.easy_btn);
        Button mdmBtn = findViewById(R.id.mdm_btn);
        Button hardBtn = findViewById(R.id.hard_btn);

        easyBtn.setOnClickListener(this);
        mdmBtn.setOnClickListener(this);
        hardBtn.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = Utils.getPrefs(this);
        String userName = prefs.getString(userNameString, "");
        int easyScore = prefs.getInt(Utils.easyLevelName, 0);
        int mediumScore = prefs.getInt(Utils.mediumLevelName, 0);
        int hardScore = prefs.getInt(Utils.hardLevelName, 0);
        if (userName.length() == 0) {
            createUserNameDialog().show();
        } else {
            usernameTV.setText(userName);
        }
        TextView scores;
        if (easyScore > 0) {
            scores = findViewById(R.id.easy_score);
            scores.setVisibility(View.VISIBLE);
            scores.setText(String.format(Locale.getDefault(), "%d/%d", easyScore, Utils.totalNotes));
        }
        if (mediumScore > 0) {
            scores = findViewById(R.id.medium_score);
            scores.setVisibility(View.VISIBLE);
            scores.setText(String.format(Locale.getDefault(), "%d/%d", mediumScore, Utils.totalNotes));
        }
        if (hardScore > 0) {
            scores = findViewById(R.id.hard_score);
            scores.setVisibility(View.VISIBLE);
            scores.setText(String.format(Locale.getDefault(), "%d/%d", hardScore, Utils.totalNotes));
        }
    }

    @Override
    public void onClick(View view) {
        int level;
        switch (view.getId()) {
            case R.id.easy_btn:
                level = 1;
                break;
            case R.id.mdm_btn:
                level = 2;
                break;
            case R.id.hard_btn:
                level = 3;
                break;
            default:
                level = 0;
                break;
        }
        if (level > 0) {
            Intent i = new Intent(getBaseContext(), GameActivity.class);
            i.putExtra("level", level);
            startActivity(i);
        }
    }

    public AlertDialog createUserNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.dialog_user_name, null);
        final EditText username = v.findViewById(R.id.username);
        final SharedPreferences.Editor editor = Utils.getPrefs(this).edit();
        builder.setView(v)
                .setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String un = username.getText().toString();
                        if (!un.isEmpty()) {
                            un = un.replace(" ", "_");
                            editor.putString(userNameString, un);
                            editor.apply();
                            dialog.cancel();
                            usernameTV.setText(un);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        createUserNameDialog().show();
                    }
                });

        return builder.create();
    }

}
