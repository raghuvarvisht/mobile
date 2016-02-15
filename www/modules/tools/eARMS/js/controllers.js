var db = null;
var data;
var userConfig = {};
var userConfigJson;
var toolName;
var serverResponse;
var nextAction;
var isEdit = "false";
var rowId;
var serverListArray; 
var specified_tool;
var toolData;
var action = "insert";
var userId;

angular.module('app.controllers', [])    
.run(function($ionicPlatform, $cordovaSQLite, $cordovaPush, $q, $http, $cordovaOauth, $rootScope) 
{
    $rootScope.createTopic = function createTopic(arguments)
    {
       return  encodeURI(arguments.join('%%'));  
    }
})
.controller('toolListCtrl', function($scope, $cordovaSQLite, $location, $ionicPlatform, DBFactory, $cordovaOauth) 
{   
    //$scope.isFavorite= false;
    $ionicPlatform.ready(function() 
	{
		  
	});
	
	function call_back_db(configuration) 
	{
	   $scope.listItems = configuration;
       //code for testing added by raghuvar
       for(var i=0 ;i<$scope.listItems.length; i++ ){
           $scope.listItems[i].is_favorite =false;           
       }
       //code for testing
       
	   console.log("Configuration value for call_back_db" + configuration);
	}	
    
    $scope.callFavorite=function($event,flag,$index,toolname)
    {
        $event.preventDefault();
        $scope.listItems[$index].is_favorite = (flag == true) ? false: true;
        
       // alert(flag+"=="+$index+"=="+toolname);
    }
    	
	DBFactory.getToolResults(call_back_db);
})

.controller('earmsMyConfigsCtrl', function($scope, $cordovaSQLite, $location, $ionicPlatform, DBFactory) 
{
	$ionicPlatform.ready(function() 
	{
     
	});
    
    init(); 
    function init() 
	{
        $scope.getAllToolConfigs = DBFactory.getToolConfigInfo(-1, specified_tool.toLowerCase(), null, call_back_display_my_configs);   
        function call_back_display_my_configs(screenData, config)
        {
            console.log("My Configs " + JSON.stringify(config));
            $scope.toolConfigs = config;  
            console.log("Inside Call back of MyConfigs Init method");            
        }
    }
      
    $scope.handleEdit = function(data, configRowId) 
	{
		action = "edit";
        console.log("Edit Called and data value " + data);
        rowId = configRowId;
        var dataString = JSON.stringify(data);
        console.log("DataString value in Edit " + dataString);
		$location.url('/side-menu21/user_config?tooldata='+dataString);
        isEdit = "true";
	};
	
	$scope.handleDelete = function(data, rowId) 
	{
       console.log("Delete Called for row Id " + rowId);
       function call_back_delete(rowId) 
       {
            alert("Selected row is deleted successfully!!");
       }		
	   DBFactory.deleteCustomToolConfig(call_back_delete, rowId);
       init();
	};
})

.controller('earmsConfigrationCtrl', function($scope, $cordovaSQLite, $location, $ionicPlatform, DBFactory, $stateParams, $rootScope) 
{  		
    $ionicPlatform.ready(function() 
	{
         
    });
    
    $scope.rowId = -1; 
       
    if (isEdit == "false")
    {
	    init();
        $scope.toolName = specified_tool;
    }
    else
    {
        displayEditValues(); 
    }
     
    function displayEditValues()
    {
        console.log("My Config Edit called "+ $stateParams.tooldata);
        $scope.selectedItem = JSON.parse($stateParams.tooldata);
        console.log("servervalue in My Config " + $scope.selectedItem.server);
        console.log("groupname in My Config" + $scope.selectedItem.group);
        console.log("reqid in My Config" + $scope.selectedItem.reqid);
        console.log("serverListArray value " + serverListArray);
        $scope.rowId = rowId;
        $scope.serverList = serverListArray;
        $scope.serverName = $scope.selectedItem.server;
        $scope.groupname = $scope.selectedItem.group;
        $scope.reqid = $scope.selectedItem.reqid;
        isEdit = "false";
        action = "edit";        
    }
     
    function call_back_db(data) 
    {
        serverListArray = new Array();
        $scope.standardToolConfigurations = data;
        $scope.serverList = {"dummykey":"dummyservervalue"};
        
        angular.forEach(data, function(dataItem, index2) 
        {  
            $scope.serverList = dataItem.config.server; 
            serverListArray = $scope.serverList;       
        });
        console.log("tool Configuration value " + data);
    } 
    
    function init() 
	{
         $scope.rowId = -1;
         console.log("Inside Init function");
         console.log("Type of $stateParams.tooldata " + typeof $stateParams.tooldata);
                      	
         if(typeof $stateParams.tooldata == "undefined") 
         {
            function call_back_db(data) 
            {
                serverListArray = new Array();
                $scope.standardToolConfigurations = data;
                $scope.serverList = {"dummykey":"dummyservervalue"};
               
                angular.forEach(data, function(dataItem, index2) 
                {  
                    $scope.serverList = dataItem.config.server; 
                    serverListArray = $scope.serverList;       
                });
                console.log("tool Configuration value " + data);
            }		
            DBFactory.getToolResults(call_back_db);
         }
         else 
         {        
            serverListArray = new Array();
            $scope.serverList = [];
                      
            $scope.selectedItem = JSON.parse($stateParams.tooldata);
            $scope.toolName = $scope.selectedItem.tool_name;
            specified_tool = $scope.selectedItem.tool_name;
            $scope.standardToolConfigurations = new Array(JSON.parse($stateParams.tooldata)); 
            console.log("Server Value got " +JSON.stringify($scope.selectedItem.config.server));
            
            angular.forEach($scope.selectedItem.config.server, function (data, index)
            {
                $scope.serverList.push(data);
                serverListArray.push(data);
            });
            
            console.log("Selected toolData " + JSON.stringify($scope.selectedItem));
            $scope.serverList = serverListArray;            
            $scope.rowId = $scope.selectedItem.row_id;
            $scope.serverName = $scope.serverList[0];          
            $scope.groupname = $scope.selectedItem.group;
            $scope.reqid = $scope.selectedItem.reqid;
         }		
	 }
     
    function getToolsResults(selectedToolData)
	{
        var configuration = new Array();
        
        if(typeof window.sqlitePlugin == "undefined") 
        {
            alert("db plugin is not available when you use ionic serve, so use mobile to use this functionality");
            console.log("you are not using from mobile, so db plugin in not available");
            return;
        }

        $cordovaSQLite.execute(db, "SELECT * FROM TOOL_CONFIG WHERE tool_name='"+specified_tool.toLowerCase()+"'").then(function(res)
        {
            if(res.rows.length > 0)
            {
                for (var i = 0; i < res.rows.length; i++)
                {
                    configuration.push(JSON.parse(res.rows.item(i).config));					
                    console.log("SelectedToolId for getToolResults-> " + res.rows.item(i).row_id);
                    console.log("SelectedConfig for getToolResults-> " + res.rows.item(i).config);					
                };
                $scope.standardToolConfigurations = configuration;
				console.log("configuration for getToolResults- " + configuration);
            }
            else
            {
                console.log("No results found for getToolResults");
            }
        }, function (err)
        {
            console.error(err);
        });     
    }
       
    function call_back_data(screenData, data)
    {
        if(typeof window.sqlitePlugin == "undefined") 
        {
            alert("db plugin is not available when you use ionic serve, so use mobile to use this functionality");
            console.log("you are not using from mobile, so db plugin in not available");
            return;
        }
        topicSubscribe.initialize();  
              
        var query ="";
        console.log("Action called " + action);
        if(action == "edit") 
        {
            console.log("Edit Tool Config called for call back data");
            query = "UPDATE CUSTOM_TOOL_CONFIG SET custom_config=? WHERE row_id=?";
            $cordovaSQLite.execute(db, query, [screenData, data[0].row_id]).then(function(res) 
            {
                alert("Selected record has been updated successfully!!");                            
            }, 
            function (err) 
            {
                console.error(err);
            });          
         }
         else 
         {
		    $cordovaSQLite.execute(db, "SELECT * FROM USER_INFO").then(function(res) 
            {
                if(res.rows.length > 0) 
                {
                    console.log("SELECTED User in Controller-> " + res.rows.item(0).user_id);
                    userId = res.rows.item(0).user_id;
                } 
                else 
                {
                    console.log("No results found For User in Controller");
                }
            }, 
            function (err) 
            {
                console.error(err);
            });
            console.log("Insert Tool Config called for call back data " + userId);           
            query = "INSERT INTO CUSTOM_TOOL_CONFIG (user_id, tool_name, version_no, custom_config, date) VALUES (?, ?, ?, ?, ?)";
            $cordovaSQLite.execute(db, query, [userId, specified_tool.toLowerCase(),"1", screenData, new Date().toString()]).then(function(res) 
            {
                console.log("insertId for custom tool config: " + res.insertId);
                alert("Inserted configuration has been saved successfully!!");
            }, 
            function (err) 
            {
                console.error(err);
            });
         } 
         $location.url('/side-menu21/myConfigs');           
    }
       
    function getToolConfigInfo(id, screenData) 
    {
         DBFactory.getToolConfigInfo(id, specified_tool.toLowerCase(), screenData, call_back_data)
    }
    				
	$scope.handleReset = function() 
	{
        console.log("Do nothing at this time");
        $scope.serverList = serverListArray;
        $scope.serverName = "";
        $scope.groupname = "";	
    }
	
    function getUserInfo(serverName, groupname, reqid, rowId)
    {
        DBFactory.getUserInfo(serverName, groupname, reqid, rowId, call_back_user_info)
    }
    
    function call_back_user_info(serverName, groupname, reqid, rowId, userid)
    {
        userConfig = {};
        userConfig["ecb"] = "topicSubscribe.onSubscribeNotificationGCM";
        console.log("servername = "+JSON.stringify(serverName) + userid);
        userConfig["userid"] = userid;
        userConfig["server"] = serverName;
		userConfig["group"] = groupname;
		userConfig["reqid"] = parseInt(reqid);
        userConfig["toolid"] = specified_tool;
        //userConfig["topic"] = "/topics/" +specified_tool.toLowerCase()+ "-" +groupname +"-" +reqid;
        console.log("Topic value " + $rootScope.createTopic(["/topics/", specified_tool.toLowerCase(), groupname, reqid]));
        userConfig["topic"] = $rootScope.createTopic(["/topics/", specified_tool.toLowerCase(), groupname, reqid]);
        userConfigJson = userConfig;
        console.log("userConfigJson value " + JSON.stringify(userConfigJson));
        console.log("RowId value " + rowId);
        
        getToolConfigInfo(rowId, JSON.stringify(userConfigJson)); 
    }
       
	$scope.configSubmit  = function (serverName, groupname, reqid, rowId) 
	{  
         $rootScope.selctdServer = serverName;
         $rootScope.selctdGrp = groupname;
         $rootScope.selctdReqid = reqid;
         $rootScope.selectdRowid = rowId;
         getUserInfo(serverName, groupname, reqid, rowId);                          
	};     
})
   
.controller('SideMenuCtrl', function($scope, $ionicModal, $location, DBFactory, $cordovaSQLite, $ionicSideMenuDelegate) 
{
     $scope.showFavoritesFlag = {checked:true};
     $scope.pingServer = function() 
	 {
		 console.log("Ping Server action called");
		 app.initialize();
	 }
     
     $scope.getAddConfigScreen = function() 
	 {
         $location.url('/side-menu21/user_config');
         action = "insert";
	 }
	  
	 $scope.pushNotification = function() 
	 {
		 console.log("Push Notification action called");
         userConfig = {};
         userConfig["senderID"] = "819109776891";
		 userConfig["ecb"] = "topicSubscribe.onSubscribeNotificationGCM";
         userConfigJson = userConfig;
		 topicSubscribe.initialize();
	 }
    
     $scope.showFavorite=function($event){
         //alert('coming in toogle');
         //$event.preventDefault();
         console.log('show favorites='+$scope.showFavoritesFlag.checked);
     } 
})

.controller('toolInformationCtrl', function($scope, $stateParams, $window, DBFactory, $cordovaPreferences) 
{
	 
    console.log("stateparams =="+$stateParams.toolId+"=="+$stateParams.info+"=="+$stateParams.action+"=="+$stateParams.topic+"=="+$stateParams.redirectServerURL+"=="+$stateParams.notificationMsg);

    
    $scope.toolName = $stateParams.toolId;
    $scope.serverMessage ="<table><thead><tr><th>Status</th><th>Total</th><th>P</th><th>Q</th><th>E</th><th>C</th><th>S</th><th>R</th></tr></thead><tbody><tr><td>C</td><td>1</td><td>0</td><td>0</td><td>0</td><td>0</td><td>0</td><td>1</td></tr></tbody></table>";// $stateParams.info;
    $scope.buttonInfo = $stateParams.action;
    
    $scope.handleServerAction  = function () 
	{
		$window.open($stateParams.redirectServerURL);
	};
    $scope.callGTRC  = function () 
	{
		var number = '18002005555' ; 
        window.plugins.CallNumber.callNumber(function()
        {
            //success event
        }, 
        function()
        {
            //error event
        }, number) 
	};
	$scope.handleSRM  = function () 
	{
		alert("SRM case");
	};
	$scope.displayFAQ  = function () 
	{
		alert("FAQ");
	};
	$scope.refresh  = function () 
	{
		alert("Hello " + $rootScope.selctdServer + "=" + $rootScope.selctdGrp + "=" + $rootScope.selctdReqid + "=" + $rootScope.selectdRowid);
        getUserInfo($rootScope.selctdServer, $rootScope.selctdGrp, $rootScope.selctdReqid, $rootScope.selectdRowid);
	};
    
    function getUserInfo(serverName, groupname, reqid, rowId)
    {
        DBFactory.getUserInfo(serverName, groupname, reqid, rowId, call_back_user_info)
    }
    
    function call_back_user_info(serverName, groupname, reqid, rowId, userid)
    {
        userConfig = {};
        userConfig["ecb"] = "topicSubscribe.onSubscribeNotificationGCM";
        console.log("servername = "+JSON.stringify(serverName) + userid);
        userConfig["userid"] = userid;
        userConfig["server"] = serverName;
		userConfig["group"] = groupname;
		userConfig["reqid"] = parseInt(reqid);
        userConfig["toolid"] = specified_tool;
        userConfig["topic"] = "/topics/" +specified_tool.toLowerCase()+ "-" +groupname +"-" +reqid;
        userConfigJson = userConfig;
        console.log("userConfigJson value " + JSON.stringify(userConfigJson));
        console.log("RowId value " + rowId);
        
        getToolConfigInfo(rowId, JSON.stringify(userConfigJson));        
    }	
    
    function getToolConfigInfo(id, screenData) 
    {
         DBFactory.getToolConfigInfo(id, specified_tool.toLowerCase(), screenData, call_back_data)
    }	 
    
    function call_back_data(screenData, data)
    {
        if(typeof window.sqlitePlugin == "undefined") 
        {
            alert("db plugin is not available when you use ionic serve, so use mobile to use this functionality");
            console.log("you are not using from mobile, so db plugin in not available");
            return;
        }
        topicSubscribe.initialize();  
    }
})

.controller('NavBarCtrl', function($scope, $location) 
{
	$scope.goHome  = function () 
	{
        $location.url('/toolList');
    };    
})
.controller('MainNavBarCtrl', function($scope, $location) 
{
	$scope.goHome  = function () 
	{
        $location.url('/toolList');
    };    
})
                             
  