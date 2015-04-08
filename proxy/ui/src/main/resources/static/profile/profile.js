(function(){
	
	'use strict';
	
	angular.module('app.profile', [
        /*
         * Angular modules
         */
        'ngRoute'
        
        /*
         * Our reusable cross app code modules
         */
        
        /*
         * 3rd Party modules
         */
    ]);

	function ProfileController($window, $location, $scope, security) {
		
		var vm = this;
		vm.signUpForm = {};
		vm.signUp = signUp;
		vm.socialProfileUser = security.socialProfileUser();

		function signUp() {
			security.signUp( vm.signUpForm );
		}
	}
	
	ProfileController.$inject = ['$window', '$location', '$scope', 'security'];

	angular.module('app.profile').controller('ProfileController', ProfileController);
	
})();