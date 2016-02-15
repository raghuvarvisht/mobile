// Ionic Starter App
// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
var db = null;
var userId;

angular.module('app', ['ionic', 'app.controllers', 'app.routes', 'app.services', 'app.directives', 'ngCordova','ngSanitize'])
.run(function($ionicPlatform, $cordovaSQLite, $cordovaPush, $q, $http, $cordovaOauth) 
{
  $ionicPlatform.ready(function() 
  {
        if(window.cordova && window.cordova.plugins.Keyboard) 
        {
            cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
        }
        if(window.StatusBar) 
        {
            StatusBar.styleDefault();
        }
        
        function parsMe(browserRef,event,deferred,redirect_url) 
        {
            if((event.url).indexOf(redirect_url) === 0) 
            {
                browserRef.removeEventListener("exit",function(event){});
                browserRef.close();
                var callbackResponse = (event.url).split("#")[1];
                var responseParameters = (callbackResponse).split("&");
                console.log("Call Back Response " + callbackResponse);
                console.log("Response Parameters " + responseParameters);
                var parameterMap = [];
                for(var i = 0; i < responseParameters.length; i++) 
                {
                    parameterMap[responseParameters[i].split("=")[0]] = responseParameters[i].split("=")[1];
                    
                }
                if(parameterMap.access_token !== undefined && parameterMap.access_token !== null)
                {
                    deferred.resolve({ access_token: parameterMap.access_token, expires_in: parameterMap.expires_in });
                    window.localStorage.setItem("access_token", parameterMap.access_token);
                    fetchUserDetails();
                } 
                else 
                {
                    if ((event.url).indexOf("error_code=100") !== 0)
                    {
                        deferred.reject("Login returned error_code=100: Invalid permissions");
                    }
                    else
                    {
                        deferred.reject("Problem authenticating");
                    }
                }
            }
         } 
    
       function isInAppBrowserInstalled(cordovaMetadata)
       {
            var inAppBrowserNames = ["cordova-plugin-inappbrowser", "org.apache.cordova.inappbrowser"];
            return inAppBrowserNames.some(function(name)
            {
                return cordovaMetadata.hasOwnProperty(name);
            });
       }
       
       function fetchUserDetails()
       {
            $http.defaults.headers.common.Authorization = "Bearer " + window.localStorage.getItem("access_token");
            $http.defaults.headers.common.Accept = "";
            console.log("Header " + JSON.stringify($http.defaults.headers.common));
            //$http.get("https://api.cisco.com/dft/common/userprofile/uid")
             $http.get("https://api.cisco.com/dft/common/teamdata")
            .success(function(data) 
            {
                displayLogs("Fetched User details" +JSON.stringify(data.uid));
            })
            .error(function(error) 
            {
                displayLogs("Error in API " +error);
            });
      }
       
       var login_cisco = function login() 
       {
            var deferred = $q.defer();
            var redirect_url = "http://localhost/callback";
            var flowUrl = "https://cloudsso.cisco.com/as/authorization.oauth2?response_type=token"
                +"&client_id=tx9gc8jgdhf23qupkzsqsvcu&scope=cecauth"+
                "&redirect_uri=http://localhost/callback";
            if(window.cordova) 
            {
                var cordovaMetadata = cordova.require("cordova/plugin_list").metadata;
                if(isInAppBrowserInstalled(cordovaMetadata) === true) 
                {
                    var browserRef = window.open(flowUrl, '_blank', 'location=no,clearsessioncache=yes,clearcache=yes');
                    browserRef.addEventListener('loadstart', function(event) 
                    {
                        parsMe(browserRef,event,deferred,redirect_url);
                    });
                    browserRef.addEventListener('loadstop', function(event) 
                    {
                        parsMe(browserRef,event,deferred,redirect_url);
                    });
                    browserRef.addEventListener('exit', function(event) 
                    {
                        deferred.reject("The sign in flow was canceled");
                    });
                }
                else 
                {
                    alert("required libraries not found in app");
                }
          }
          else 
          {
            alert("cordova not installed in window");
          }
    };
    
    //login_cisco();
        
    db = window.sqlitePlugin.openDatabase({name: "CSGTools", location: 1});	
    $cordovaSQLite.execute(db, "SELECT * FROM USER_INFO").then(function(res) 
    {
        if(res.rows.length > 0) 
        {
            console.log("SELECTED -> " + res.rows.item(0).user_id);
            userId = res.rows.item(0).user_id;
        } 
        else 
        {
            console.log("No results found For User");
        }
        }, 
        function (err) 
        {
            console.error(err);
        });
    
        var query1 = "INSERT INTO TOOL_CONFIG (tool_name, config, version_no, date) VALUES (?, ?, ?, ?)";
        $cordovaSQLite.execute(db, query1, ["eARMS", '{"tool_name":"eARMS","value":"testValue","server":{"earms-app-vm":"http://earms-app-vm/","earms-app-2-vm":"http://earms-app-2-vm/"}}', "1", "06/01/2016"]).then(function(res) 
        {
            console.log("insertId for tool_config: " + res.insertId);
        }, 
        function (err) 
        {
            console.error(err);
        });	
        
        var query2 = "INSERT INTO TOOL_CONFIG (tool_name, config, version_no, date) VALUES (?, ?, ?, ?)";
        $cordovaSQLite.execute(db, query2, ["eZcommit", '{"tool_name":"eZcommit","value":"testValue","server":{"ezcommit-app-vm":"http://ezcommit-app-vm/","ezcommit-app-2-vm":"http://ezcommit-app-2-vm/"}}', "1", "06/01/2016"]).then(function(res) 
        {
            console.log("insertId for tool_config: " + res.insertId);
        }, 
        function (err) 
        {
            console.error(err);
        });
        
        var query3 = "INSERT INTO TOOL_CONFIG (tool_name, config, version_no, date) VALUES (?, ?, ?, ?)";
        $cordovaSQLite.execute(db, query3, ["ATS", '{"tool_name":"ATS","value":"testValue","server":{"ats-app-vm":"http://ats-app-vm/","ats-app-2-vm":"http://ats-app-2-vm/"}}', "1", "06/01/2016"]).then(function(res) 
        {
            console.log("insertId for tool_config: " + res.insertId);
        }, 
        function (err) 
        {
            console.error(err);
        });
        
        var query4 = "INSERT INTO TOOL_CONFIG (tool_name, config, version_no, date) VALUES (?, ?, ?, ?)";
        $cordovaSQLite.execute(db, query4, ["CFLOW", '{"tool_name":"CFLOW","value":"testValue","server":{"cflow-app-vm":"http://cflow-app-vm/","cflow-app-2-vm":"http://cflow-app-2-vm/"}}', "1", "06/01/2016"]).then(function(res) 
        {
            console.log("insertId for tool_config: " + res.insertId);
        }, 
        function (err) 
        {
            console.error(err);
        });
    });
})