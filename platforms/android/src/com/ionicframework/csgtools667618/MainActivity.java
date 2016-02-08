/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.ionicframework.csgtools667618;

import android.content.SharedPreferences;
import android.os.Bundle;
import org.apache.cordova.*;

public class MainActivity extends CordovaActivity
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Set by <content src="index.html" /> in config.xml
        SharedPreferences preferences=getSharedPreferences("ServerParams", getApplicationContext().MODE_PRIVATE);
        String toolId = preferences.getString("toolId", null);

        if(toolId != "" && toolId != null)
        {
            String info = preferences.getString("info", null);
            String action = preferences.getString("action", null);
            String topic = preferences.getString("topic", null);
            String redirectServerURL = preferences.getString("redirectServerURL", null);
            String redirectClientScreenURL = preferences.getString("redirectClientScreenURL", null);
            String notificationMsg = preferences.getString("notificationMessage", null);

            launchUrl = launchUrl + "#/" + redirectClientScreenURL + "?toolId=" + toolId + "&info=" + info + "&action=" + action + "&topic=" + topic + "&redirectServerURL=" + redirectServerURL + "&notificationMsg=" + notificationMsg;
            System.out.print("url coming ==" + launchUrl);
            preferences.edit().clear().commit();
        }
        loadUrl(launchUrl);
    }
}
