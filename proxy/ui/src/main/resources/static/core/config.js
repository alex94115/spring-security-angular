(function() {
	
	'use strict';
	
	function configureAppCore( $routeProvider ) {
		
		//TODO fix error on a missing <base> tag with this setting enabled
		//$locationProvider.html5Mode(true);
		
		$routeProvider
			.when('/', {
		      templateUrl: 	'main.html'
		      // uses the root controller, as specified in index.html
		    })
		    .when('/createProfile', {
		      templateUrl: 	'profile/createProfile.html',
		      controller:	'ProfileController',
		      controllerAs:	'profile'
		    })
		    .when('/home', {
		      templateUrl:	'home/home.html',
		      controller:	'HomeController',
		      controllerAs:	'home'
		    })
		    .otherwise({
		      redirectTo  : '/'
		    });
	}
	
	configureAppCore.$inject = [ '$routeProvider' ];
	
	angular.module('app.core').config( configureAppCore );
			
})();
	
	    
