(function(){
	
	'use strict';
	
	function runBlock( $rootScope, security ) {
		security.registerFacebookAuthListeners( $rootScope );
		security.registerGoogleAuthListeners( $rootScope );
	}
	
	runBlock.$inject = ['$rootScope', 'security'];

	angular.module('app.core').run( runBlock );
	
})();