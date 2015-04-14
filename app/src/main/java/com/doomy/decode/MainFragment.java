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

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;

import com.doomy.zxing.ZXingScannerView;

import com.getbase.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainFragment extends Fragment implements ZXingScannerView.ResultHandler,
		ResultDialogFragment.ResultDialogListener, FormatDialogFragment.FormatDialogListener {
    
	private static final String FLASH_STATE = "FLASH_STATE";
    private static final String AUTO_FOCUS_STATE = "AUTO_FOCUS_STATE";
    private static final String SELECTED_FORMATS = "SELECTED_FORMATS";
    private ZXingScannerView mScannerView;
    private DataBase mDB;
    private boolean mMedia;
    private boolean mFlash;
    private boolean mAutoFocus;
    private ArrayList<Integer> mSelectedIndices;
    private FloatingActionButton mFABHistory;
    private FloatingActionButton mFABSettings;
    private FloatingActionButton mFABFlash;
    private FloatingActionButton mFABFocus;
    private ImageView mImageViewPlay;
    private ImageView mImageViewPause;
    private ImageView mImageViewState;
    private SharedPreferences mPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle state) {
        mScannerView = new ZXingScannerView(getActivity());

        mDB = new DataBase(getActivity());

        mPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        final View mView = inflater.inflate(R.layout.activity_main, null);

        mImageViewPlay = (ImageView) mView.findViewById(R.id.imageViewPlay);
        mImageViewPause = (ImageView) mView.findViewById(R.id.imageViewPause);

        mMedia = true;

        mImageViewState = (ImageView) mView.findViewById(R.id.imageViewState);
        mImageViewState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mMedia) {
                    mImageViewPause.setAlpha(1f);
                    mImageViewPause.animate().alpha(0f).setDuration(1000);
                    mScannerView.stopCamera();
                    mMedia = false;
                } else {
                    mImageViewPlay.setAlpha(1f);
                    mImageViewPlay.animate().alpha(0f).setDuration(1000);
                    mScannerView.startCamera();
                    mMedia = true;
                }
            }
        });

        mFABHistory = (FloatingActionButton) mView.findViewById(R.id.actionHistory);
        mFABHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(getActivity(), ScanActivity.class);
                getActivity().startActivity(mIntent);
            }
        });

        mFABSettings = (FloatingActionButton) mView.findViewById(R.id.actionSettings);
        mFABSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment mFragment = FormatDialogFragment.newInstance(MainFragment.this, mSelectedIndices);
                mFragment.show(getActivity().getFragmentManager(), "format");
            }
        });

        mFABFlash = (FloatingActionButton) mView.findViewById(R.id.actionFlash);
        mFABFlash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean mPrefFlash = mPreferences.getBoolean("mPrefFlash", false);
                if (mPrefFlash) {
                    mFlash = !mFlash;
                    if (mFlash) {
                        mFABFlash.setIcon(R.drawable.ic_flash_on);
                    } else {
                        mFABFlash.setIcon(R.drawable.ic_flash_off);
                    }
                    mScannerView.setFlash(mFlash);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.flash), Toast.LENGTH_SHORT).show();
                }
            }
        });

        mFABFocus = (FloatingActionButton) mView.findViewById(R.id.actionFocus);
        mFABFocus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAutoFocus = !mAutoFocus;
                if(mAutoFocus) {
                    mFABFocus.setIcon(R.drawable.ic_focus_on);
                    Toast.makeText(getActivity(), getString(R.string.autofocus_on), Toast.LENGTH_SHORT).show();
                } else {
                    mFABFocus.setIcon(R.drawable.ic_focus_off);
                    Toast.makeText(getActivity(), getString(R.string.autofocus_off), Toast.LENGTH_SHORT).show();
                }
                mScannerView.setAutoFocus(mAutoFocus);
            }
        });

        if(state != null) {
            mFlash = state.getBoolean(FLASH_STATE, false);
            mAutoFocus = state.getBoolean(AUTO_FOCUS_STATE, true);
            mSelectedIndices = state.getIntegerArrayList(SELECTED_FORMATS);
        } else {
            mFlash = false;
            mAutoFocus = true;
            mSelectedIndices = null;
        }
		
        setupFormats();
        mScannerView.addView(mView);
        return mScannerView;
    }

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setHasOptionsMenu(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        mMedia = true;
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
        outState.putBoolean(AUTO_FOCUS_STATE, mAutoFocus);
        outState.putIntegerArrayList(SELECTED_FORMATS, mSelectedIndices);
    }

    @Override
    public void handleResult(Result rawResult) {
        showMessageDialog(Utils.renameFormat(rawResult.getBarcodeFormat().toString()), rawResult.getText());
    }

    public void showMessageDialog(String myTitle, String myMessage) {
        SimpleDateFormat mDate = new SimpleDateFormat("dd/MM/yyyy");
        String myDate = mDate.format(new Date());
        Log.d("maDate", myDate);
        Scan mScan = new Scan(myTitle, myMessage, myDate);
        mDB.addOne(mScan);
        DialogFragment mFragment = ResultDialogFragment.newInstance(myTitle, myMessage, this);
        mFragment.show(getActivity().getFragmentManager(), "result");
    }

    public void closeMessageDialog() {
        closeDialog("result");
    }

    public void closeFormatsDialog() {
        closeDialog("format");
    }

    public void closeDialog(String dialogName) {
        FragmentManager mFragmentManager = getActivity().getFragmentManager();
        DialogFragment mFragment = (DialogFragment) mFragmentManager.findFragmentByTag(dialogName);
        if(mFragment != null) {
            mFragment.dismiss();
        }
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        mScannerView.startCamera();
        mScannerView.setFlash(mFlash);
        mScannerView.setAutoFocus(mAutoFocus);
    }

    @Override
    public void onFormatsSaved(ArrayList<Integer> selectedIndices) {
        mSelectedIndices = selectedIndices;
        setupFormats();
    }

    public void setupFormats() {
        List<BarcodeFormat> mFormats = new ArrayList<>();
        if(mSelectedIndices == null || mSelectedIndices.isEmpty()) {
            mSelectedIndices = new ArrayList<>();
            for(int i = 0; i < ZXingScannerView.ALL_FORMATS.size(); i++) {
                mSelectedIndices.add(i);
            }
        }

        for(int index : mSelectedIndices) {
            mFormats.add(ZXingScannerView.ALL_FORMATS.get(index));
        }
        if(mScannerView != null) {
            mScannerView.setFormats(mFormats);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMedia = false;
        mScannerView.stopCamera();
        closeMessageDialog();
        closeFormatsDialog();
    }
}
