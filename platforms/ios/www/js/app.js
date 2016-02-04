// Ionic Starter App
// angular.module is a global place for creating, registering and retrieving Angular modules
// 'starter' is the name of this angular module example (also set in a <body> attribute in index.html)
// the 2nd parameter is an array of 'requires'
// 'starter.services' is found in services.js
// 'starter.controllers' is found in controllers.js
var db = null;

angular.module('app', ['ionic', 'app.controllers', 'app.routes', 'app.services', 'app.directives', 'ngCordova'])
.run(function($ionicPlatform,$cordovaSQLite) 
{
  $ionicPlatform.ready(function() 
  {
    if(window.cordova && window.cordova.plugins.Keyboard) 
	{
      cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
    }
    if(window.StatusBar) 
	{
      // org.apache.cordova.statusbar required
      StatusBar.styleDefault();
    }
	db = $cordovaSQLite.openDB("CSGTools");		
    $cordovaSQLite.execute(db, "SELECT * FROM USER_INFO").then(function(res) 
	{
		if(res.rows.length > 0) 
		{
			console.log("SELECTED -> " + res.rows.item(0).user_id);
		} 
		else 
		{
			console.log("No results found");
		}
    }, 
	function (err) 
	{
		console.error(err);
	});
	
	for (var i = 1; i<=5; i++)
	{
		var query = "INSERT INTO TOOL_CONFIG (tool_id, config) VALUES (?, ?)";
		$cordovaSQLite.execute(db, query, [i, '{"tool_name":"eARMS","value":"testValue","server":"http://server"}']).then(function(res) 
		{
			console.log("insertId: " + res.insertId);
		}, 
		function (err) 
		{
			console.error(err);
		});
	}	  	
  });
})