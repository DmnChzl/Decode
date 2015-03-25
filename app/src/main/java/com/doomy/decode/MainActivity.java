/**
 * Copyright (C) 2015 Damien Chazoule
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.doomy.decode;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.ContextThemeWrapper;
import android.view.Window;
import android.view.WindowManager;

public class MainActivity extends Activity {

    private boolean mValue;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setTheme(Utils.setThemeVersion());

        setContentView(R.layout.activity_main);

        // Open "Hello" dialog at the first launch
        openFirstDialog();

        MainFragment mFragment = new MainFragment();
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, mFragment).commit();
    }

    // Create AlertDialog for the first launch
    private void openFirstDialog() {
        mValue = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("mValue", true);

        if (mValue) {
            // ContextThemeWrapper mThemeWrapper = new ContextThemeWrapper(MainActivity.this, R.style.MaterialDialog);
            AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(MainActivity.this, Utils.setThemeDialog());

            mAlertDialog.setTitle(getString(R.string.hello));
            mAlertDialog.setMessage(getString(R.string.message));
            mAlertDialog.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            mAlertDialog.show();

            getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit().putBoolean("mValue", false).commit();
        }
    }
}