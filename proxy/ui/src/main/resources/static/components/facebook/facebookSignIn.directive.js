(function() {

	'use strict';

	angular
		.module('directive.facebook')
		.directive('facebookSignin', ['$window', facebookSignin ]);
	
	function facebookSignin($window) {

		return {
			restrict: 'E',
			transclude: true,
			template: "<div class='fb-login-button' data-max-rows='1' data-size='large' data-show-faces='false' data-auto-logout-link='false' data-scope='public_profile, email', data-onlogin='fbAsyncInit();' data-onclick='FB.login();'></div>",
			replace: true,
		};
	}
	
})();