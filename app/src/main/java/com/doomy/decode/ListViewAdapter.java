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
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Scan> implements Filterable {

    // Declare your view and variables
    private Activity mActivity;
    private List<Scan> mItems, mDatas;
    private int mRow;
    private Scan mScan;
    private SearchFilter mFilter;

    public ListViewAdapter(Activity activity, int row, List<Scan> items) {
        super(activity, row, items);

        this.mActivity = activity;
        this.mRow = row;
        this.mItems = items;
        this.mDatas = items;
    }

    public int getCount()
    {
        return mItems.size();
    }

    public Scan getItem(int position)
    {
        return mItems.get(position);
    }

    public long getItemId(int position)
    {
        return mItems.get(position).getID();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View mView = convertView;
        ViewHolder mHolder;
        if (mView == null) {
            LayoutInflater mInflater = (LayoutInflater) mActivity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = mInflater.inflate(mRow, null);

            mHolder = new ViewHolder();
            mView.setTag(mHolder);
        } else {
            mHolder = (ViewHolder) mView.getTag();
        }

        if ((mItems == null) || ((position + 1) > mItems.size()))
            return mView;

        mScan = mItems.get(position);

        mHolder.myFormat = (TextView) mView.findViewById(R.id.textViewFormat);
        mHolder.myDate = (TextView) mView.findViewById(R.id.textViewDate);
        mHolder.myContent = (TextView) mView.findViewById(R.id.textViewContent);

        if (mHolder.myFormat != null && null != mScan.getFormat()
                && mScan.getFormat().trim().length() > 0) {
            mHolder.myFormat.setText(Html.fromHtml(mScan.getFormat()));
        }
        if (mHolder.myDate != null && null != mScan.getDate()
                && mScan.getDate().trim().length() > 0) {
            mHolder.myDate.setText(Html.fromHtml(mScan.getDate()));
        }
        if (mHolder.myContent != null && null != mScan.getContent()
                && mScan.getContent().trim().length() > 0) {
            mHolder.myContent.setText(Html.fromHtml(mScan.getContent()));
        }
        return mView;
    }

    public class ViewHolder {
        public TextView myFormat, myDate, myContent;
    }

    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new SearchFilter();
        }
        return mFilter;
    }

    private class SearchFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint)
        {
            String mConstraint = constraint.toString().toLowerCase();

            FilterResults mResults = new FilterResults();

            List<Scan> mFilteredScans;

            if (constraint != null && constraint.length() > 0) {

                mFilteredScans = new ArrayList<>();

                for (Scan mScan : mDatas)
                {
                    if (mScan.getFormat().toLowerCase().contains(mConstraint))
                    {
                        mFilteredScans.add(mScan);
                    }
                }
            } else {
                mFilteredScans = mDatas;
            }

            synchronized (this)
            {
                mResults.count = mFilteredScans.size();
                mResults.values = mFilteredScans;
            }

            return mResults ;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results)
        {
            List<Scan> mFilteredScan = (List<Scan>) results.values;
            mItems = mFilteredScan;
            notifyDataSetChanged();
        }
    }
}
