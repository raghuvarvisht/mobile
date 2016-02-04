var db = null;
var data;

angular.module('app.controllers', [])    
.controller('toolListCtrl', function($scope, $cordovaSQLite, $location, $ionicPlatform, DBFactory) 
{
	$ionicPlatform.ready(function() 
	{
		  
	});
	
	function call_back_db(configuration) 
	{
	   $scope.listItems = configuration;
	   //$scope.$apply();
	   console.log("Configuration value " + configuration);
	}		
	DBFactory.getToolResults(call_back_db);
})

.controller('userConfigrationCtrl', function($scope, $cordovaSQLite, $location) 
{
	db = $cordovaSQLite.openDB("CSGTools");
	function getToolsResults()
	{
        var configuration = new Array();
        $cordovaSQLite.execute(db, "SELECT * FROM TOOL_CONFIG").then(function(res)
        {
            if(res.rows.length > 0)
            {
                for (var i = 0; i < res.rows.length; i++)
                {
                    configuration.push(JSON.parse(res.rows.item(i).config));					
                    console.log("SelectedToolId -> " + res.rows.item(i).tool_id);
                    console.log("SelectedConfig -> " + res.rows.item(i).config);					
                };
                $scope.items = configuration;
				console.log("configuration - " + configuration);
            }
            else
            {
                console.log("No results found");
            }
        }, function (err)
        {
            console.error(err);
        });      
    }
			
	//$scope.items = [{"tool_name":"eARMS","value":"testValue","server":"http://server1"},{"tool_name":"eARMS","value":"testValue","server":"http://server2"},{"tool_name":"eARMS","value":"testValue","server":"http://server3"}];
	
	$scope.handleReset = function(param) 
	{
		//alert("Inserted group " +  $scope.groupname);
		alert("Display Group Value : " +  param);
	}
	
	$scope.redirect  = function (groupname, reqid) 
	{
		 alert("Insert Operation Called for user Config");
		 db = $cordovaSQLite.openDB("CSGTools");
		 var query = "INSERT INTO TOOL_CONFIG (tool_id, config) VALUES (?, ?)";
		 $cordovaSQLite.execute(db, query, ["1", "default"]).then(function(res) 
		 {
			console.log("insertId: " + res.insertId);
		 }, 
		 function (err) 
		 {
			console.error(err);
		 });
		 $location.url('/page2');
	}; 
	 
	 function init() 
	 {
		getToolsResults();
	 }
	 init();
})
   
.controller('toolInformationCtrl', function($scope) 
{
	$scope.callGTRC  = function () 
	{
		alert("GTRC");
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
		alert("Refresh");
	};		 
})
   
.controller('devXToolsCtrl', function($scope) 
{

})
 
.controller('SideMenuCtrl', function($scope, $ionicModal, $location) 
{
     $scope.pingServer = function() 
	 {
		 alert("Ping Server action called");
	 }
	
	 $scope.redirectUserConfig = function() 
	 {
		alert("Edit action called");
	 },
	 
	// Load the modal from the given template URL
    $ionicModal.fromTemplateUrl('modal.html', function($ionicModal) 
	{
        $scope.modal = $ionicModal;		
    }, 
	{
       scope: $scope,
       animation: 'slide-in-up'
    }); 
	
	$scope.handleEdit = function() 
	{
		alert("Edit Called");
		$scope.modal.hide();
		$location.url('/side-menu21/user_config');
	};
	
	$scope.handleDelete = function() 
	{
		alert("Delete Called")
	};
})
                             
  