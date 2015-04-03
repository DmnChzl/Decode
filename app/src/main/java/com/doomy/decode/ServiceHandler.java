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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class ServiceHandler {

    static String mResponse = null;
    public final static int GET = 1;
    public final static int POST = 2;

    public ServiceHandler() {

    }

    public String makeService(String myURL, int myMethod) {
        return this.makeService(myURL, myMethod, null);
    }

    public String makeService(String myURL, int myMethod,
                                  List<NameValuePair> myParams) {
        try {
            DefaultHttpClient mHTTPClient = new DefaultHttpClient();
            HttpEntity mHTTPEntity = null;
            HttpResponse mHTTPResponse = null;

            if (myMethod == POST) {
                HttpPost httpPost = new HttpPost(myURL);
                if (myParams != null) {
                    httpPost.setEntity(new UrlEncodedFormEntity(myParams));
                }

                mHTTPResponse = mHTTPClient.execute(httpPost);

            } else if (myMethod == GET) {
                if (myParams != null) {
                    String mParamString = URLEncodedUtils.format(myParams, "utf-8");
                    myURL += "?" + mParamString;
                }
                HttpGet httpGet = new HttpGet(myURL);

                mHTTPResponse = mHTTPClient.execute(httpGet);
            }

            mHTTPEntity = mHTTPResponse.getEntity();
            mResponse = EntityUtils.toString(mHTTPEntity);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return mResponse;
    }
}