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
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.style.UnderlineSpan;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ResultDialogFragment extends DialogFragment {

    public interface ResultDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

    private static final String TAG = "ResultDialogFragment";
    private static final String KEY_AUTHORS = "authors";
    private static final String KEY_AUTHOR_DATA = "author_data";
    private static final String KEY_DATA = "data";
    private static final String KEY_ERROR = "error";
    private static final String KEY_ITEMS = "items";
    private static final String KEY_NAME = "name";
    private static final String KEY_TITLE = "title";
    private static final String KEY_TOTALITEMS = "totalItems";
    private static final String KEY_VOLUMEINFO = "volumeInfo";
    private String mTitle;
    private String mMessage;
    private ResultDialogListener mListener;

    public void onCreate(Bundle state) {
        super.onCreate(state);
        setRetainInstance(true);
    }

    public static ResultDialogFragment newInstance(String title, String message, ResultDialogListener listener) {
        ResultDialogFragment mFragment = new ResultDialogFragment();
        mFragment.mTitle = title;
        mFragment.mMessage = message;
        mFragment.mListener = listener;
        return mFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater mLayoutInflater = LayoutInflater.from(getActivity());
        View mView = mLayoutInflater.inflate(R.layout.view_result, null);

        TextView mTextViewFormat = (TextView) mView.findViewById(R.id.textViewFormat);
        TextView mTextViewContent = (TextView) mView.findViewById(R.id.textViewContent);

        if (mTitle.equals("EAN 13") && mMessage.startsWith("978")) {
            GetResponseTask mGetResponseTask = new GetResponseTask();
            mGetResponseTask.execute(mMessage);

            try {
                if(mGetResponseTask.get().equals("")) {
                    mTitle = "ISBN";
                    mTextViewContent.setText(mMessage);
                } else {
                    mTitle = "ISBN";
                    mMessage = mGetResponseTask.get();
                    SpannableString mSpan = new SpannableString(mMessage);
                    mSpan.setSpan(new UnderlineSpan(), 0, mMessage.length(), 0);
                    mTextViewContent.setText(mSpan);
                    mTextViewContent.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            createURLIntent(makeURLSearch(mMessage));
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else if (mMessage.startsWith("www") || mMessage.startsWith("http://") || mMessage.startsWith("https://")) {
            SpannableString mSpan = new SpannableString(mMessage);
            mSpan.setSpan(new UnderlineSpan(), 0, mMessage.length(), 0);
            mTextViewContent.setText(mSpan);
            mTextViewContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    createURLIntent(mMessage);
                }
            });
        } else {
            mTextViewContent.setText(mMessage);
        }


        mTextViewFormat.setText(mTitle);

        AlertDialog.Builder mAlertBuilder = new AlertDialog.Builder(getActivity(), Utils.setThemeDialog());
        
		mAlertBuilder.setTitle(getString(R.string.result))
                .setView(mView)
				.setPositiveButton(getString(R.string.okay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mListener != null) {
                            mListener.onDialogPositiveClick(ResultDialogFragment.this);
                        }
                    }
                });

        return mAlertBuilder.create();
    }

    private String makeURLSearch(String mySearch) {
        String mURL = "https://www.google.com/#q=";
        mySearch = mySearch.replaceAll(" ", "+");
        String mResult = mURL + mySearch;
        return mResult;
    }

    private void createURLIntent(String myURL) {
        Intent mIntent = new Intent();
        mIntent.setAction(Intent.ACTION_VIEW);
        mIntent.addCategory(Intent.CATEGORY_BROWSABLE);
        mIntent.setData(Uri.parse(myURL));
        startActivity(mIntent);
    }

    private class GetResponseTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... myISBN) {
            ServiceHandler mServiceHandler = new ServiceHandler();
            String mISBN = "";

            if(myISBN.length == 1) {
                mISBN = myISBN[0];
            }

            String mURL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
            String mJSONString = mServiceHandler.makeService(mURL+mISBN, ServiceHandler.GET);
            JSONObject mJSONResponse = null;

            try {
                mJSONResponse = new JSONObject(mJSONString);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            String mResult = "";

            try {
                if (!mJSONResponse.getString(KEY_TOTALITEMS).equals("0")) {
                    JSONArray mJSONBook = mJSONResponse.getJSONArray(KEY_ITEMS);
                    for (int i = 0; i < mJSONBook.length(); i++) {
                        JSONObject mBook = mJSONBook.getJSONObject(i);
                        JSONObject mVolumeInfo = mBook.getJSONObject(KEY_VOLUMEINFO);
                        String mTitleBook = mVolumeInfo.getString(KEY_TITLE);
                        JSONArray mAuthors = mVolumeInfo.getJSONArray(KEY_AUTHORS);
                        for (int j = 0; j < mAuthors.length(); j++) {
                            String mAuthorBook = mAuthors.getString(0);
                            mResult = mAuthorBook + " : " + mTitleBook;
                        }
                    }
                } else {
                    mResult = "";
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return mResult;
        }
    }
}
