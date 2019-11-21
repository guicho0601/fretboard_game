package com.lrgt.fretboardgame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    private SharedPreferences prefs;

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
        prefs = getSharedPreferences(Utils.PREFERENCES_NAME, Context.MODE_PRIVATE);
        String userName = prefs.getString("user_name", "");
        if (userName.length() == 0) {
            createUserNameDialog().show();
        } else {
            usernameTV.setText(userName);
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
        builder.setView(v)
                .setPositiveButton(R.string.enter, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String un = username.getText().toString();
                        if(!un.isEmpty()) {
                            un = un.replace(" ", "_");
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("username", un);
                            editor.apply();
                            dialog.cancel();
                            usernameTV.setText(un);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }
}
