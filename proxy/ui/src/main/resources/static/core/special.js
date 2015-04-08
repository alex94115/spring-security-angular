(function(){
	
	'use strict';

	function SpecialController($window, $location, $rootScope, security) {
		
		var vm = this;
		
		vm.greeting = 'A special greeting';
		
//		 $rootScope.$on('event:google-plus-signin-success', function (event,authResult) {
//			console.log('');
//		    console.log('RECEIVED GPLUS SIGNIN SUCCESS');
//		    console.log('');
//		  });
		 
//		 $rootScope.$on('event:google-plus-signin-failure', function (event,authResult) {
//		    console.log('Received GPlus Signin Failure');
//		  });
		
		// startup logic
		activate();
		
		function activate() {
//			tempService.tempMethod().then(
//			  function( response ) {
//				  vm.greeting = response;
//			  },
//			  function( error ) {
//				  vm.greeting = error;
//			  }
//			);
		}
	}
	SpecialController.$inject = ['$window', '$location', '$rootScope', 'security'];

	angular.module('app.core').controller('SpecialController', SpecialController);
	
})();