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
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class ResultDialogFragment extends DialogFragment {

    public interface ResultDialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
    }

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

        if(mMessage.substring(0,7).equals("http://")||mMessage.substring(0,8).equals("https://")) {
            SpannableString mSpannable = new SpannableString(mMessage);
            Linkify.addLinks(mSpannable, Linkify.WEB_URLS);
            mTextViewContent.setText(mSpannable);
            mTextViewContent.setLinkTextColor(getResources().getColor(R.color.greyMaterialDark));
            mTextViewContent.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            mTextViewContent.setText(mMessage);
            mTextViewContent.setTextColor(getResources().getColor(R.color.greyMaterialDark));
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
}
