(function() {

	'use strict';

	angular
		.module('directive.facebook', [])
		.directive('facebook', ['$window', facebook ])
		.run(['$window','$rootScope', runFacebookDirective ]);
	
	function facebook($window) {

		return {
			restrict: 'E',
			transclude: true,
			template: "<div id='fb-root'></div>",
			replace: true,
			link: function (scope, element, attrs, ctrl, linker) {

				// Asynchronously load the Facebook api
				var js, fjs = document.getElementsByTagName('script')[0];
				
				if( document.getElementById('facebook-jssdk')) {
					return;
				}
				
				js = document.createElement('script');
				js.id = 'facebook-jssdk';
				// without debug output
				js.src = '//connect.facebook.net/en_US/sdk.js#xfbml=1&appId=1534780056805129&version=v2.0';
				// to get debug output
				//js.src = '//connect.facebook.net/en_US/sdk/debug.js#xfbml=1&appId=1534780056805129&version=v2.0';
				fjs.parentNode.insertBefore( js, fjs );
			}
		};
	}
	
	function runFacebookDirective($window, $rootScope) {
		$window.facebookSigninCallback = function( authResult ) {
			console.log('statusChangeCallback');
		    console.log(authResult);
		    // The authResult object is returned with a status field that lets the
		    // app know the current login status of the person.
		    // Full docs on the authResult object can be found in the documentation
		    // for FB.getLoginStatus().
		    if (authResult.status === 'connected') {
		      // Logged into your app and Facebook.
		    	$rootScope.$broadcast('event:facebook-signin-success', authResult);
		    } else if (authResult.status === 'not_authorized') {
		      // The person is logged into Facebook, but not your app.
		    	$rootScope.$broadcast('event:facebook-signin-failure', authResult);
		    } else {
		      // The person is not logged into Facebook, so we're not sure if
		      // they are logged into this app or not.
		    	$rootScope.$broadcast('event:facebook-signin-failure', authResult);
		    }
		}; 
		
		$window.fbAsyncInit = function() {
			
			FB.getLoginStatus(function(authResult) {
				console.log('statusChangeCallback');
			    console.log(authResult);
			    // The authResult object is returned with a status field that lets the
			    // app know the current login status of the person.
			    // Full docs on the authResult object can be found in the documentation
			    // for FB.getLoginStatus().
			    if (authResult.status === 'connected') {
			      // Logged into your app and Facebook.
			    	$rootScope.$broadcast('event:facebook-signin-success', authResult.authResponse);
			    } else if (authResult.status === 'not_authorized') {
			      // The person is logged into Facebook, but not your app.
			    	$rootScope.$broadcast('event:facebook-signin-failure', authResult);
			    } else {
			      // The person is not logged into Facebook, so we're not sure if
			      // they are logged into this app or not.
			    	$rootScope.$broadcast('event:facebook-signin-failure', authResult);
			    }
			});
		};
		
		
	}

})();