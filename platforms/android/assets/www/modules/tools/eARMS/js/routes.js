angular.module('app.routes', [])

.config(function($stateProvider, $urlRouterProvider) 
{
  // Ionic uses AngularUI Router which uses the concept of states
  // Learn more here: https://github.com/angular-ui/ui-router
  // Set up the various states which the app can be in.
  // Each state's controller can be found in controllers.js
  $stateProvider
        
    .state('indexmenu', 
	{
      url: '/side-menu20',
      abstract:true,
      templateUrl: 'modules/tools/eARMS/html/indexMenu.html'
    })
    
    .state('indexmenu.toolList', 
	{
      cache: false,
      url: '/toolList',
      views: 
	  {
        'side-menu20': 
		{
          templateUrl: 'modules/tools/eARMS/html/toolList.html',
          controller: 'toolListCtrl'
        }
      }
    })
    
    .state('menu', 
	{
      url: '/side-menu21',
      abstract:true,
      templateUrl: 'modules/tools/eARMS/html/menu.html'
    })
    
    .state('menu.myConfigs', 
	{
      cache: false,
      url: '/myConfigs',
      views: 
	  {
        'side-menu21': 
		{
          templateUrl: 'modules/tools/eARMS/html/myConfigs.html',
          controller: 'earmsMyConfigsCtrl'
        }
      }
    })  
      
    .state('menu.userConfigration', 
	{
      url: '/user_config?:tooldata',
      views: 
	  {
        'side-menu21': 
		{
          templateUrl: 'modules/tools/eARMS/html/userConfigration.html',
          controller: 'earmsConfigrationCtrl'
        }
      }
    })
                      
    .state('toolInformation', 
	{
      url: '/toolInfo?toolId&msg&action',
      templateUrl: 'modules/tools/eARMS/html/toolInformation.html',
      controller: 'toolInformationCtrl'
    })
                          		
    $urlRouterProvider.otherwise('/side-menu20/toolList');
});