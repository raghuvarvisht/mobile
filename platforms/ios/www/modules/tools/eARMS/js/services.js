angular.module('app.services', ['ionic','ngCordova'])
.run(function($ionicPlatform,$cordovaSQLite, $rootScope) 
{
  $ionicPlatform.ready(function() 
  {

  });
})

.factory('DBFactory', ['$cordovaSQLite', '$ionicPlatform',
function($cordovaSQLite, $ionicPlatform)
{
	var blankFactory = {};	
    
    blankFactory.getToolResults = function getToolsResults(call_back)
	{
		 $ionicPlatform.ready(function() 
		 {
			if(typeof window.sqlitePlugin == "undefined") 
            {
                alert("db plugin is not available when you use ionic serve, so use mobile to use this functionality");
                console.log("you are not using from mobile, so db plugin in not available");
                return;
            }
			console.log("Print db type variable:" + (typeof db));
			var configuration = new Array();

			$cordovaSQLite.execute(db, "SELECT * FROM TOOL_CONFIG").then(function(res)
			{
                if(res.rows.length > 0)
				{
					for (var i = 0; i < res.rows.length; i++)
					{
                        var tempData = {};
                        tempData.row_id = res.rows.item(i).row_id;
                        tempData.config = JSON.parse(res.rows.item(i).config);
                        tempData.tool_name = res.rows.item(i).tool_name;
						console.log("print type of "+ (typeof res.rows.item(i).config))
						configuration.push(tempData);					
						console.log("SelectedToolId getToolResults-> " + res.rows.item(i).row_id);
						console.log("SelectedConfig getToolResults-> " + res.rows.item(i).config);					
					};
					call_back(configuration);				
					console.log("Configuration in getToolResults- " + configuration);
				}
				else
				{
					console.log("No results found for Tool Config");
				}
			}, function (err)
			{
				console.error(err);
			}); 
		 });		
    }
    
    blankFactory.getToolConfigInfo = function getToolConfigInfo(id, toolName, screenData, call_back_data) 
    {
        var configuration = null;
        if(typeof window.sqlitePlugin == "undefined") 
        {
            alert("db plugin is not available when you use ionic serve, so use mobile to use this functionality");
            console.log("you are not using from mobile, so db plugin in not available");
            return;
        }
               
        console.log("Inside getToolConfigInfo method"); 
         
        var sqlString="SELECT * FROM CUSTOM_TOOL_CONFIG WHERE tool_name='"+toolName+"'";
        if(typeof id !="undefined" && id != -1) 
        {
            sqlString = sqlString + " AND row_id='"+id+"'";
        }     
        console.log("SQL Query String with row id " + sqlString);
        
        $cordovaSQLite.execute(db, sqlString).then(function(res)
        {            
            if(res.rows.length > 0)
            {
                console.log("Result length " + res.rows.length);
                configuration = new Array() 
                for (var i = 0; i < res.rows.length; i++)
                {
                    var tempData = {};
                    tempData.row_id = res.rows.item(i).row_id;
                    tempData.config = JSON.parse(res.rows.item(i).custom_config);
                    tempData.tool_name = res.rows.item(i).tool_name;
                    console.log("print type of "+ (typeof res.rows.item(i).custom_config))
                    configuration.push(tempData);					
                    console.log("SelectedToolId getToolConfigInfo-> " + res.rows.item(i).row_id);
                    console.log("SelectedConfig getToolConfigInfo-> " + res.rows.item(i).custom_config);					
                };
                call_back_data(screenData, configuration);
				console.log("configuration getToolConfigInfo- " + configuration);
            }
            else
            {
                console.log("No results found for getToolConfigInfo");              
                call_back_data(screenData, configuration);
            }
        }, 
        function (err)
        {
            console.error("Error executing getToolConfigInfo" + err);       
            call_back_data(screenData, null);
        });       
    } 
    
    blankFactory.getUserInfo = function getUserInfo(serverName, groupname, reqid, rowId, call_back_user_info)
	{
		 $ionicPlatform.ready(function() 
		 {
			var userid = "";
            if(typeof window.sqlitePlugin == "undefined") 
            {
                alert("db plugin is not available when you use ionic serve, so use mobile to use this functionality");
                console.log("you are not using from mobile, so db plugin in not available");
                return;
            }          
            $cordovaSQLite.execute(db, "SELECT * FROM USER_INFO").then(function(res) 
            {
                if(res.rows.length > 0) 
                {
                    console.log("SELECTED User in Controller for config submit-> " + res.rows.item(0).user_id);
                    userid = res.rows.item(0).user_id;
                    call_back_user_info(serverName, groupname, reqid, rowId, userid);
                } 
                else 
                {
                    console.log("No results found For User in Controller");
                    alert("You are not logged in. Please login using CEC userid and password in the application");
                }
            }, 
            function (err) 
            {
                console.error(err);
                alert("You are not logged in. Please login using CEC userid and password in the application");
            });           			
		 });		
    } 
      
    blankFactory.deleteCustomToolConfig = function deleteCustomToolConfig(call_back_delete, rowId)
	{
		 $ionicPlatform.ready(function() 
		 {
			if(typeof window.sqlitePlugin == "undefined") 
            {
                alert("db plugin is not available when you use ionic serve, so use mobile to use this functionality");
                console.log("you are not using from mobile, so db plugin in not available");
                return;
            }
			
			$cordovaSQLite.execute(db, "DELETE FROM CUSTOM_TOOL_CONFIG WHERE row_id =" + rowId).then(function(res)
			{
				console.log("inside delete of custom Tool Config");
				call_back_delete(rowId);             
			}, 
            function (err)
			{
				console.error(err);
			}); 
		 });		
    }   	 
	return blankFactory;	
}])

.service('BlankService', [function(){

}]);

