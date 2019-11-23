/*
 * Copyright 2016 andryr
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lrgt.fretboardgame;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Build;

import androidx.core.content.ContextCompat;

/**
 * Created by andry on 23/04/16.
 */
class Utils {

    static int totalNotes = 30;
    static String easyLevelName = "easy";
    static String mediumLevelName = "medium";
    static String hardLevelName = "hard";
    static final String ROOT = "fonts/";
    static final String FONTAWESOME = ROOT + "fontawesome-webfont.ttf";

    static boolean checkPermission(Context context, String permission) {
        return ContextCompat.checkSelfPermission(context,
                permission) == PackageManager.PERMISSION_GRANTED || Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    static SharedPreferences getPrefs(Context context) {
        String PREFERENCES_NAME = "preferences";
        return context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    public static Typeface getTypeface(Context context, String font) {
        return Typeface.createFromAsset(context.getAssets(), font);
    }

}
