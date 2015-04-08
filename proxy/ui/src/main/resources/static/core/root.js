(function(){
	
	'use strict';

	function RootController($location, $timeout, $scope, security) {
		
		var vm = this;
		
		vm.greeting = 'Root greeting';
		vm.isAuthenticationInitialized = false;

		vm.user = undefined;
		vm.authFailureReason = undefined;
		vm.hasSocialConnection = false;
		vm.signUpForm = {};
		vm.logout = security.logout;
		vm.signUp = signUp;

		// register event listeners
//		$scope.$on('user.login.success', function( event ) {
//			$scope.$apply(function() {
//				console.log('received user.login.success event');
//				vm.user = security.loggedInUser();
//				vm.isAuthenticationInitialized = true;
//				vm.authFailureReason = undefined;
//				vm.hasSocialConnection = security.hasSocialConnection();
//			});
//		});
		
		$scope.$on('user.login.failed', function( event, reason ) {
			$timeout(function() {
				console.log('received user.login.failed event: ' + reason.name + ', message: ' + reason.message );
				
				vm.authFailureReason = reason;
				vm.isAuthenticationInitialized = true;
				vm.user = undefined;
				vm.hasSocialConnection = security.hasSocialConnection();
				
				// change the location to createProfile
				//console.log('setting path to /createProfile');
				//$location.path('/createProfile');
			});
		});
		
//		$scope.$on('user.logout', function( event ) {
//			$scope.$apply(function() {
//				console.log('received user.logout event');
//				vm.user = undefined;
//				vm.authFailureReason = undefined;
//				vm.hasSocialConnection = security.hasSocialConnection();
//
//				//$location.path('/');
//			});
//		});
		
		function signUp() {
			security.signUp( vm.signUpForm );
		}
//		
//		function logout() {
//			security.logout();
//		}
		
	}
	RootController.$inject = ['$location', '$timeout', '$scope', 'security'];

	angular.module('app.core').controller('RootController', RootController);
	
})();