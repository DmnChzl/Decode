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

public class Scan {

    private static int mCount = 1;
    private int ID;
    private String Format;
    private String Content;
    private String Date;

    public Scan() {
        super();
    }

    public Scan (String mFormat, String mContent, String mDate) {
        this.ID = Scan.mCount++;
        this.Format = mFormat;
        this.Content = mContent;
        this.Date = mDate;
    }

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getFormat() {
        return Format;
    }

    public void setFormat(String format) {
        Format = format;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    @Override
    public String toString() {
        return "Scan [ID : " + ID + ", Format : " + Format + "]";
    }
}
