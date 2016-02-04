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
        SharedPreferences preferences=getSharedPreferences("SharedParams", getApplicationContext().MODE_PRIVATE);
        String keyDataURL=preferences.getString("isPageUrl", null);
        String  keyData=preferences.getString("keyDataURL", null);
        String  notificationMsg = "Service registered message earms"; //preferences.getString("notificationMessage", null);
        if(keyDataURL != "" && keyDataURL != null)
        {
            launchUrl = launchUrl + "#/" + keyDataURL + "?toolId=" + keyData + "&msg=" + notificationMsg + "&action=Call GTRC";
            System.out.print("url coming ==" + launchUrl);
            preferences.edit().clear().commit();
        }
        loadUrl(launchUrl);
    }
}
