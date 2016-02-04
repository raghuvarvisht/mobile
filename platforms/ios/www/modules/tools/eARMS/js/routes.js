angular.module('app.routes', [])

.config(function($stateProvider, $urlRouterProvider) 
{
  // Ionic uses AngularUI Router which uses the concept of states
  // Learn more here: https://github.com/angular-ui/ui-router
  // Set up the various states which the app can be in.
  // Each state's controller can be found in controllers.js
  $stateProvider
        
    .state('menu', 
	{
      url: '/side-menu21',
      abstract:true,
      templateUrl: 'modules/tools/eARMS/html/menu.html'
    })
      
	  
    .state('menu.userConfigration', 
	{
      url: '/user_config',
      views: 
	  {
        'side-menu21': 
		{
          templateUrl: 'modules/tools/eARMS/html/userConfigration.html',
          controller: 'userConfigrationCtrl'
        }
      }
    })
        
       
            
    .state('toolInformation', 
	{
      url: '/page2',
      templateUrl: 'modules/tools/eARMS/html/toolInformation.html',
      controller: 'toolInformationCtrl'
    })
        
      
              
    .state('devXTools',
	{
      url: '/page3',
      templateUrl: 'modules/tools/eARMS/html/devXTools.html',
      controller: 'devXToolsCtrl'
    })
	
	
	
	.state('toolList',
	{
      url: '/toolList',
      templateUrl: 'modules/tools/eARMS/html/toolList.html',
      controller: 'toolListCtrl'
    });

  //if none of the above states are matched, use this as the fallback
  //$urlRouterProvider.otherwise('/side-menu21/user_config');
  $urlRouterProvider.otherwise('/toolList');
});