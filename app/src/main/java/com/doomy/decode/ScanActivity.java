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

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends ActionBarActivity implements AdapterView.OnItemClickListener, ResultDialogFragment.ResultDialogListener {

    // Declare your view and variables
    private static final String TAG = "ScanActivity";
    private DataBase mDB;
    private ListView mListView;
    private ListViewAdapter mListViewAdapter;
    private List<Scan> mList = new ArrayList<>();
    private RelativeLayout mRelativeLayout;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private String[] mNavMenuTitles;
    private TypedArray mNavMenuIcons;
    private ArrayList<Item> mNavDrawer;
    private DrawerAdapter mDrawerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.listview);

        mNavMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);
        mNavMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

        mDrawerList = (ListView)findViewById(R.id.listViewDrawer);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawerLayout);

        int mWidth = getResources().getDisplayMetrics().widthPixels/2;
        DrawerLayout.LayoutParams mParams = (android.support.v4.widget.DrawerLayout.LayoutParams) mDrawerList.getLayoutParams();
        mParams.width = mWidth;
        mDrawerList.setLayoutParams(mParams);

        mActivityTitle = getTitle().toString();

        addDrawerItems();
        setupDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDB = new DataBase(this);

        mRelativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);
        mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(this);

        if (mDB.getRowsCount() > 0) {
            mRelativeLayout.setAlpha(0);
            for (int i = 0; i < mDB.getRowsCount(); i++) {
                mList.add(mDB.showOne(mDB.getRowsCount() - i));
            }
        } else {
            mRelativeLayout.setAlpha(1);
        }

        mListViewAdapter = new ListViewAdapter(ScanActivity.this, R.layout.activity_scan, mList);
        mListView.setAdapter(mListViewAdapter);
    }

    private void addDrawerItems() {

        mNavDrawer = new ArrayList<>();

        mNavDrawer.add(new Item(mNavMenuTitles[0], mNavMenuIcons.getResourceId(0, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[1], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[2], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[3], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[4], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[5], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[6], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[7], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[8], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[9], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[10], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[11], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[12], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[13], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[14], mNavMenuIcons.getResourceId(1, -1)));
        mNavDrawer.add(new Item(mNavMenuTitles[15], mNavMenuIcons.getResourceId(1, -1)));

        mNavMenuIcons.recycle();

        mDrawerAdapter = new DrawerAdapter(getApplicationContext(), mNavDrawer);
        mDrawerList.setAdapter(mDrawerAdapter);

        mDrawerList.setItemChecked(0, true);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0 : filterHistory("");
                        break;
                    case 1 : filterHistory("Aztec");
                        break;
                    case 2 : filterHistory("UPC A");
                        break;
                    case 3 : filterHistory("UPC E");
                        break;
                    case 4 : filterHistory("EAN 8");
                        break;
                    case 5 : filterHistory("EAN 13");
                        break;
                    case 6 : filterHistory("ISBN");
                        break;
                    case 7 : filterHistory("RSS 14");
                        break;
                    case 8 : filterHistory("Code 39");
                        break;
                    case 9 : filterHistory("Code 93");
                        break;
                    case 10 : filterHistory("Code 128");
                        break;
                    case 11 : filterHistory("ITF");
                        break;
                    case 12 : filterHistory("Codabar");
                        break;
                    case 13 : filterHistory("QR Code");
                        break;
                    case 14 : filterHistory("Data Matrix");
                        break;
                    case 15 : filterHistory("PDF 417");
                        break;
                }
                mDrawerLayout.closeDrawers();
            }
        });
    }

    private void filterHistory(String mFormat) {
        mListViewAdapter.getFilter().filter(mFormat);

        int mCount = 0;
        for(Scan mScan : mList) {
            if (mScan.getFormat().contains(mFormat)) {
                mCount++;
            }
        }
        if (mList.size() > 0 && mCount == 0) {
            Toast.makeText(ScanActivity.this, getString(R.string.no) + " " + mFormat + " " + getString(R.string.no_barcode), Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteRows() {

        Toast.makeText(this, getString(R.string.done), Toast.LENGTH_LONG).show();

        invalidateOptionsMenu();

        mList.clear();

        mListViewAdapter = new ListViewAdapter(ScanActivity.this, R.layout.activity_scan, mList);
        mListView.setAdapter(mListViewAdapter);

        mDB.deleteAll();

        mRelativeLayout.setAlpha(1);
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle(getString(R.string.filter));
                invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // Creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem mItemClear = menu.findItem(R.id.action_clear);

        if (mList.size() == 0) {
            mItemClear.setVisible(false);
        } else {
            mItemClear.setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /**
         * Handle action bar item clicks here. The action bar will
         * automatically handle clicks on the Home/Up button, so long
         * as you specify a parent activity in AndroidManifest.xml.
         */
        int id = item.getItemId();

        // NoInspection SimplifiableIfStatement
        if (id == R.id.action_about) {
            openAboutDialog();
            return true;
        }
        if (id == R.id.action_clear) {
            openDeleteDialog();
            return true;
        }
        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        Log.i(TAG, "onResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onItemClick(AdapterView<?> listview, View v, int position, long id) {
        Scan myContact = (Scan) listview.getItemAtPosition(position);

        String myFormat = myContact.getFormat().toString();
        String myContent = myContact.getContent().toString();

        DialogFragment mFragment = ResultDialogFragment.newInstance(myFormat, myContent, false, this);
        mFragment.show(ScanActivity.this.getFragmentManager(), "result");
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {

    }

    // Create AlertDialog for delete messages
    private void openDeleteDialog() {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(ScanActivity.this, R.style.MaterialDialog);

        String mText = getString(R.string.empty);

        mAlertDialog.setMessage(mText);
        mAlertDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteRows();
            }
        });
        mAlertDialog.setNegativeButton("Non", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mAlertDialog.show();
    }

    // Create AlertDialog for the about view
    private void openAboutDialog() {
        LayoutInflater mLayoutInflater = LayoutInflater.from(ScanActivity.this);
        View mView = mLayoutInflater.inflate(R.layout.view_about, null);

        ImageView mImageViewAuthor = (ImageView) mView.findViewById(R.id.imageViewAuthor);
        ImageView mImageViewStudio = (ImageView) mView.findViewById(R.id.imageViewStudio);
        ImageView mImageViewGitHub = (ImageView) mView.findViewById(R.id.imageViewGitHub);
        Drawable mAuthor = mImageViewAuthor.getDrawable();
        Drawable mStudio = mImageViewStudio.getDrawable();
        Drawable mGitHub = mImageViewGitHub.getDrawable();
        mAuthor.setColorFilter(getResources().getColor(R.color.greenDark), PorterDuff.Mode.SRC_ATOP);
        mStudio.setColorFilter(getResources().getColor(R.color.green), PorterDuff.Mode.SRC_ATOP);
        mGitHub.setColorFilter(getResources().getColor(R.color.greyMaterialDark), PorterDuff.Mode.SRC_ATOP);

        mImageViewGitHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent();
                mIntent.setAction(Intent.ACTION_VIEW);
                mIntent.addCategory(Intent.CATEGORY_BROWSABLE);
                mIntent.setData(Uri.parse(getString(R.string.url)));
                startActivity(mIntent);
            }
        });

        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(ScanActivity.this, Utils.setThemeDialog());

        mAlertDialog.setTitle(getString(R.string.about));
        mAlertDialog.setView(mView);
        mAlertDialog.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        mAlertDialog.show();
    }
}
