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
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends Activity implements OnItemClickListener, ResultDialogFragment.ResultDialogListener {

    // Declare your view and variables
    private static final String TAG = "ScanActivity";
    private DataBase mDB;
    private ListView mListView;
    private ListViewAdapter mAdapter;
    private List<Scan> mList = new ArrayList<>();
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(Utils.setThemeBar());

        setContentView(R.layout.listview);

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

        mAdapter = new ListViewAdapter(
                ScanActivity.this, R.layout.activity_scan, mList);
        mListView.setAdapter(mAdapter);
    }

    public void deleteRows() {

        Toast.makeText(this, getString(R.string.done), Toast.LENGTH_LONG).show();

        invalidateOptionsMenu();

        mList.clear();

        mAdapter = new ListViewAdapter(ScanActivity.this, R.layout.activity_scan, mList);
        mListView.setAdapter(mAdapter);

        mDB.deleteAll();

        mRelativeLayout.setAlpha(1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);

        SearchManager mSearchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView mSearchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        mSearchView.setSearchableInfo(mSearchManager.getSearchableInfo(getComponentName()));
        mSearchView.setIconifiedByDefault(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mAdapter.getFilter().filter(newText);
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem mItemClear = menu.findItem(R.id.action_clear);
        MenuItem mItemSearch = menu.findItem(R.id.action_search);

        if (mList.size() == 0) {
            mItemClear.setVisible(false);
            mItemSearch.setVisible(false);
        } else {
            mItemClear.setVisible(true);
            mItemSearch.setVisible(true);
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

        DialogFragment mFragment = ResultDialogFragment.newInstance(myFormat, myContent, this);
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

        ImageView mImageViewMrDoomy = (ImageView) mView.findViewById(R.id.imageViewMrDoomy);
        ImageView mImageViewStudio = (ImageView) mView.findViewById(R.id.imageViewStudio);
        ImageView mImageViewGitHub = (ImageView) mView.findViewById(R.id.imageViewGitHub);
        Drawable mMrDoomy = mImageViewMrDoomy.getDrawable();
        Drawable mStudio = mImageViewStudio.getDrawable();
        Drawable mGitHub = mImageViewGitHub.getDrawable();
        mMrDoomy.setColorFilter(getResources().getColor(R.color.greenDark), PorterDuff.Mode.SRC_ATOP);
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
