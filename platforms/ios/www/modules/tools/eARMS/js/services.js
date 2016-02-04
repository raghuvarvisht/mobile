angular.module('app.services', ['ionic','ng-cordova'])
.run(function($ionicPlatform,$cordovaSQLite, $rootScope) 
{
  $ionicPlatform.ready(function() 
  {
	  
  });
})

.factory('DBFactory', ['$rootScope','$cordovaSQLite', '$ionicPlatform',
function($rootScope, $cordovaSQLite,$ionicPlatform)
{
	var blankFactory = {};	  
	blankFactory.getToolResults = function getToolsResults(call_back)
	{
		 $ionicPlatform.ready(function() 
		 {
			$rootScope.db = $cordovaSQLite.openDB("CSGTools");
			console.log("Print db type variable:" + (typeof $rootScope.db));
			var configuration = new Array();
			
			$cordovaSQLite.execute($rootScope.db, "SELECT * FROM TOOL_CONFIG").then(function(res)
			{
				if(res.rows.length > 0)
				{
					for (var i = 0; i < res.rows.length; i++)
					{
						console.log("print type of "+ (typeof res.rows.item(i).config))
						configuration.push(JSON.parse(res.rows.item(i).config));					
						console.log("SelectedToolId -> " + res.rows.item(i).tool_id);
						console.log("SelectedConfig -> " + res.rows.item(i).config);					
					};
					call_back(configuration);
					
					console.log("Data first - " + configuration);
				}
				else
				{
					console.log("No results found");
				}
			}, function (err)
			{
				console.error(err);
			}); 
		 });			
    }	 
	return blankFactory;	
}])

.service('BlankService', [function(){

}]);

