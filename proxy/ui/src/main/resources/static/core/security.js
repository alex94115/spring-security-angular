(function() {
    'use strict';
    
	// Security Service
	function security( $rootScope, $http, $q, $resource ) {
    	
    	// instance variables
    	var userInfo = {
    		user: 				'ANONYMOUS',
    		didLoginFail:		false
    	},
    	lock = 	{
			isAuthInProgress:	false
		},
		socialProfiles = {
    		facebook: {
    			isConnected:	false,
    			didLoginFail:	false,
    			user: 			undefined,
    			auth: 			undefined
    		},
    		gPlus: {
    			isConnected:	false,
    			didLoginFail:	false,
    			user: 			undefined,
    			auth: 			undefined
    		}
    	},
    	lockObserverCallbacks = [];
    	
    	// public API
    	var service = {
    		loggedInUser: 					loggedInUser,
    		registerFacebookAuthListeners: 	registerFacebookAuthListeners,
    		registerGoogleAuthListeners:	registerGoogleAuthListeners,
    		hasSocialConnection:			hasSocialConnection,
    		socialProfileUser:				socialProfileUser,
    		signUp:							signUp,
    		logout:							logout
    	};
    	return service;

    	// internal functions
    	
        /**
         * Private data accessors
         */
		function loggedInUser() {
			console.log( "inside security.getLoggedInUser()" );
			return userInfo.user;
		}
    	
    	function hasSocialConnection() {
    		return socialProfiles.facebook.isConnected || socialProfiles.gPlus.isConnected;
    	}
    	
    	function socialProfileUser() {
    		return socialProfiles.facebook.user || socialProfiles.gPlus.user;
    	}
    	
    	/**
    	 * Calls each notification callback in the array, passing in the stored state object
    	 */
    	function notifyLockObservers() {
    		var i;
    		
    		for( i = 0; i < lockObserverCallbacks.length; i++ ) {
    			var callback = lockObserverCallbacks[i];
    			callback.fn( callback.state );
    		}
    		
    		return i > 0;
    	}
    	
    	/**
    	 * Callbacks are notified (one by one, although there should only ever be at most 
    	 * one observer right now) after the isAuthInProgress lock is released.
    	 * 
    	 * If the lock is free as expected, we grab it and resolve the promise. Otherwise
    	 * something unexpected has happened and we reject the promise.
    	 */
		function lockStatusChangeCallback( state ) {
			console.log('Got a callback that the lock status changed' );

			// remove the last observer (all observers are alike)
			lockObserverCallbacks.pop();
			
			var deferred = state.deferred;
			
			// cleanup state
			delete state.deferred;
			
			if( userInfo.user === 'ANONYMOUS' && !lock.isAuthInProgress ) {
				
				userInfo.auth = state.authResponse;
				
				// grab the lock & make this state object the current lockholder
				lock.isAuthInProgress = true;
				state.isLockOwner = true;
				console.log('Observer: \'' + state.owner + '\' got the authentication lock.' );
				deferred.resolve( state );
				
			} else {
				deferred.reject( 'Observer: \'' + state.owner + '\' notified of change, but user now: ' + userInfo.user + ' and lock.isAuthInProgress: ' + lock.isAuthInProgress );
			}
		}
    	
    	/**
         * Attempts to authenticate this user by posting a set of social credentials (provider, id, access token) 
         * to the app server at login/social. If the credentials can be verified, this 
         * has the effect of setting up the security context for this user.
         */
        function appServerLogin( state ) {
        	
        	var deferred = $q.defer(),
        	  loginPostData;
        	state.deferred = deferred;
        	
        	if( state.owner === 'facebook' ) {
            	console.log('Setting facebook profile user: ' + state.facebookMe );
            	socialProfiles.facebook.user = state.facebookMe;
                
                loginPostData = $.param({ 
                	social_provider: 		'FACEBOOK',
            		facebook_userId:	 	state.facebookMe.id,
            		facebook_accessToken: 	socialProfiles.facebook.auth.accessToken
            	});
        	} else {
        		console.log('Setting gPlus profile user: ' + state.gPlusMe );
            	socialProfiles.gPlus.user = state.gPlusMe;
                
                loginPostData = $.param({ 
                	social_provider: 'GOOGLE_PLUS',
            		gPlus_userId: state.gPlusMe.id,
            		gPlus_accessToken: socialProfiles.gPlus.auth.access_token
            	});
        	}
            
            var postAction = {
        		method: 'POST',
    			url: 'login/social',
    			data: loginPostData,
    			headers: {'Content-Type': 'application/x-www-form-urlencoded'}
            };
        	
            $http( postAction )
            	.success(function(response, status, headers, config) {
            		state.appServerLoginResponse = response;
            		
            		// clean up state
            		var deferred = state.deferred;
            		delete state.deferred;
            		
            		deferred.resolve( state );
            	})
            	.error(function(error, status, headers, config) {
            		
            		state.error = error;
            		
            		// clean up state
            		var deferred = state.deferred;
            		delete state.deferred;
            		
            		deferred.reject( state );
            	});
            
            return deferred.promise;
        }
        
        /**
         * Requests a private resource on the app server. Success shows that the
         * security context is setup and working properly.
         */
        function getGreeting( state ) {
        	
        	var deferred = $q.defer();
        	state.deferred = deferred;
        	
        	$http.get('resource/').success(function(response, status, headers, config) {
        		state.greetingResponse = response;
        		
        		// clean up state
        		var deferred = state.deferred;
        		delete state.deferred;
        		
        		deferred.resolve( state );
        	});
        	
        	return deferred.promise;
        }
        
        /**
         * Logs the response from the protected resource & marks didLoginSucceed as true
         */
        function logGreetingResponse( state ) {
        	console.log('Received data from resource server. content: ' + state.greetingResponse.content + ', id: ' + state.greetingResponse.id );
        	
        	if( state.greetingResponse.content ) {
        		state.didLoginSucceed = true;
        	}
        	
        	return state;
        }
        
        function logAuthenticationError( error ) {
        	
        	var message = 'Error in the login / request resource chain.';
        	
        	if( error.hasOwnProperty( 'status' ) ) {
        		message += ' Status: ' + error.status;
        		
        		if( error.hasOwnProperty( 'statusText' ) ) {
            		message += ', statusText : ' + error.statusText;
            	}
        	}
        	
        	console.log('Error in the login / request resource chain: ' + error );
        }
    	
        /**
         * Since the events from Facebook and Google arrive at unpredictable times,
         * we use a lock to keep from trying to authenticate to the app server
         * with multiple social profiles concurrently.
         * 
         * If the lock is available and the user is anonymous, this function sets locked to true,
         * modifies the state parameter to indicate that this sequence is now the lock 
         * holder (so that it can release the lock after the login attempt is complete)
         * and completes.
         * 
         * If the lock is taken and the user is anonymous, we register an observer with a callback 
         * that is notified when the lock is released.
         * 
         * If the user is logged in, we reject the promise (error), since there's no reason
         * to try to authenticate again. 
         * 
         */
		function acquireAuthenticationLock( state ) {

			var deferred = $q.defer();
			
			if( userInfo.user === 'ANONYMOUS' && !lock.isAuthInProgress ) {
				
				userInfo.auth = state.authResponse;
				lock.isAuthInProgress = true;
				
				// this state object is now the lock holder
				state.isLockOwner = true;
				console.log('\'' + state.owner + '\' got the authentication lock.' );
				
				deferred.resolve( state );
				
			} else if( userInfo.user === 'ANONYMOUS' && lock.isAuthInProgress ) {
				
				console.log('\'' + state.owner + '\' attempted to get the authentication lock, but it is taken right now. Adding an observer.' );
				
				// store the deferred object in state so we can resolve in the callback
				state.deferred = deferred;
				
				// Add an observer here and wait for a callback when the lock is released.
				lockObserverCallbacks.push({ 
					fn: 	lockStatusChangeCallback, 
					state:	state
				});
				
			} else { 
				 
				deferred.reject({
					name:		'Already Logged In',
					message:	'Acquire authentication lock failed because user is locced in. User: ' + userInfo.user
				});
			}
			
			return deferred.promise;
		}
		
		/**
		 * Final step in the happy-path process is to broadcast the user.login.success
		 * event and release the lock, notifying any observers.
		 */
		function broadcastResultAndResetLock( state ) {
			if( state.isLockOwner ) {
				
				if( state.didLoginSucceed ) {
					$rootScope.$broadcast( 'user.login.success' );
				}
				
				// reset lock & notify any observers
				lock.isAuthInProgress = false;
				notifyLockObservers();
				
			} else if( socialProfiles.facebook.isConnected === socialProfiles.facebook.didLoginFail && 
					   socialProfiles.gPlus.isConnected === socialProfiles.gPlus.didLoginFail ) {
				
				$rootScope.$broadcast( 'user.login.failed', lock.loginFailureReason );
			}
			
		}
		
		/**
		 * Upon app server login, copies the profile that was used to 
		 * authenticate from the socialProfiles object to the userInfo object.
		 */
		function onAppServerLoginSuccess( state ) {
			console.log('app server+' + state.owner + ' login succeeded');
			
			if( state.owner === 'facebook' ) {
				userInfo.user = socialProfiles.facebook.user;
			} else {
				userInfo.user = socialProfiles.gPlus.user;
			}
			
			return state;
		}
		
		/**
		 * Throws an exception indicating that authentication failed
		 */
		function onAppServerLoginFailure( state ) {
			console.log('app server+' + state.owner + ' login failed');
			throw {
				name: 		'AuthenticationFailedException',
				message: 	'Unknown user',
				state: 		state
			};
		}
		
		function handleLoginException( exception ) {
			var doLockObserversExist;
			
			if( exception.state.isLockOwner ) {
				console.log( exception.state.owner + ' acquired the lock but could not authenticate' );
				
				// reset the lock & notify observers
				lock.isAuthInProgress = false;
				doLockObserversExist = notifyLockObservers();
				
				// record that login failed for this provider
				if( exception.state.owner === 'facebook' ) {
					socialProfiles.facebook.didLoginFail = true;
				} else {
					socialProfiles.gPlus.didLoginFail = true;
				}
				
				if( !doLockObserversExist ) {
					if( socialProfiles.facebook.isConnected === socialProfiles.facebook.didLoginFail && 
					   socialProfiles.gPlus.isConnected === socialProfiles.gPlus.didLoginFail ) {
						
						$rootScope.$broadcast( 'user.login.failed', {name: exception.name, message: exception.message } );
					}
				}
				
			} else {
				console.log( 'authentication via ' + exception.state.owner + ' failed with message: ' + exception.message );
			}
		}
		
		function getFbUserAttributes( state ) {
			
			state.deferred = $q.defer();
			
			FB.api('/me', function( response ) {
				
				state.facebookMe = response;
				
				// cleanup state
				var deferred = state.deferred;
				delete state.deferred;
				
				deferred.resolve( state );
			});
			
			return state.deferred.promise;
		}
		
		function getGooglePlusClient( state ) {
			
			state.deferred = $q.defer();
			
			gapi.client.load('plus','v1', function() {
				
				var deferred = state.deferred;
				delete state.deferred;
				
				deferred.resolve( state );
			});
			
			return state.deferred.promise;
		}
		
		function getGoogleUserAttributes( state ) {
			
			state.deferred = $q.defer();
			
			gapi.client.plus.people.get({ userId: 'me' }).then( function( response ) {
			    state.gPlusMe = response.result;
				
			    // cleanup state
			    var deferred = state.deferred;
			    delete state.deferred;
			    
			    deferred.resolve( state );
			  });
			
			return state.deferred.promise;
		}
		
		/**
		 * Register root scope listeners that are called after the Facebook initialization /
		 * user login status service has returned.
		 */
		function registerFacebookAuthListeners( $rootScope ) {
			
			console.log('doing the initial registration of facebook auth listeners. userInfo: ' + userInfo );

			/**
		     * Processes the fbLoginSuccess event by setting the 
		     * $rootScope loggedInUserAuth variable, fetching user 
		     * information from Facebook, and then setting up the
		     * security context with the app server.
		     */
		    $rootScope.$on('event:facebook-signin-success', function( event, authResponse ) {
		    	
		    	console.log('Security service received event:facebook-signin-success' );
		    	
		    	socialProfiles.facebook.isConnected = true;
		    	socialProfiles.facebook.auth = authResponse;
		    	
		    	acquireAuthenticationLock({ owner: 'facebook', authResponse: authResponse })
		    		.then( getFbUserAttributes )
		    		.then( appServerLogin )
		    		.then( onAppServerLoginSuccess, onAppServerLoginFailure )
		    		.then( getGreeting )
		    		.then( logGreetingResponse )
		    		.then( broadcastResultAndResetLock )
		    		.catch( handleLoginException );
		    });

		    $rootScope.$on('event:facebook-signin-failure', function() {
		        
		        socialProfiles.facebook.isConnected = false;
		    	socialProfiles.facebook.auth = undefined;
		    	
		    	// Facebook login failed. If gPlus isn't connected (or login failed), broadcast login failed event
		    	if( socialProfiles.gPlus.isConnected === socialProfiles.gPlus.didLoginFail ) {
					$rootScope.$broadcast( 'user.login.failed', 'Unknown user' );
		    	}
		    });
			
		}
		
		/**
		 * Register root scope listeners that are called after the Google Plus initialization /
		 * user login status service has returned.
		 */
		function registerGoogleAuthListeners( authResult ) {
			
			console.log('doing the initial registration of google auth listeners. userInfo: ' + userInfo );
			
			/**
		     * Processes the google authentication success event by setting the 
		     * $rootScope loggedInUserAuth variable, fetching user 
		     * information from Google, and then setting up the
		     * security context with the app server.
		     */
		    $rootScope.$on('event:google-plus-signin-success', function( event, authResult ) {

		    	console.log('Security service received event:google-plus-signin-success' );
		    	
				socialProfiles.gPlus.isConnected = true;
				socialProfiles.gPlus.auth = authResult;
				
				acquireAuthenticationLock({ owner: 'gPlus', authResponse: authResult })
	    			.then( getGooglePlusClient )
					.then( getGoogleUserAttributes )
					.then( appServerLogin )
					.then( onAppServerLoginSuccess, onAppServerLoginFailure )
		    		.then( getGreeting )
		    		.then( logGreetingResponse )
		    		.then( broadcastResultAndResetLock )
					.catch( handleLoginException );
		    });
		    
		    $rootScope.$on('event:google-plus-signin-failure', function() {
		        socialProfiles.gPlus.isConnected = false;
		    	socialProfiles.gPlus.auth = undefined;
		    	
		    	// GPlus login failed. If Facebook isn't connected (or login failed), broadcast login failed event
		    	if( socialProfiles.facebook.isConnected === socialProfiles.facebook.didLoginFail ) {
					$rootScope.$broadcast( 'user.login.failed', 'Unknown user' );
		    	}
		    });
		}
		
		function signUp( form ) {
			console.log( 'received the sign up data in security service' );
			
			var doGPlusSignUp = socialProfiles.gPlus.user && socialProfiles.gPlus.auth;
			var doFacebookSignUp = socialProfiles.facebook.user && socialProfiles.facebook.auth; 
			
			var postData;
			
			if( doGPlusSignUp ) {
				postData = {
					site: 			'gPlus',
					siteUserId:		socialProfiles.gPlus.user.id,
					accessToken:	socialProfiles.gPlus.auth.access_token,
					firstName:		socialProfiles.gPlus.user.name.givenName,
					lastName:		socialProfiles.gPlus.user.name.familyName,
					displayName:	socialProfiles.gPlus.user.displayName,
					email:			socialProfiles.gPlus.user.emails[0].value,
					gender:			socialProfiles.gPlus.user.gender,
					profileUrl:		socialProfiles.gPlus.user.url,
					imageUrl:		socialProfiles.gPlus.user.image.url,
				};
				
			} else if( doFacebookSignUp ) {
				postData = {
					site: 			'facebook',
					siteUserId:		socialProfiles.facebook.user.id,
					accessToken:	socialProfiles.facebook.auth.accessToken,
					firstName:		socialProfiles.facebook.user.first_name,
					middleName:		socialProfiles.facebook.user.middle_name,
					lastName:		socialProfiles.facebook.user.last_name,
					displayName:	socialProfiles.facebook.user.name,
					email:			socialProfiles.facebook.user.email,
					gender:			socialProfiles.facebook.user.gender,
					timezone:		socialProfiles.facebook.user.timezone,
					profileUrl:		socialProfiles.facebook.user.link
				};
			} else {
				throw {
					name: 		'UnknownSignUpSite',
					message: 	'Tried to sign up but neither the facebook or gPlus social profiles were initialized'
				};
			}
			
			// add data from the form (unrelated to a specific social site)
			postData.screenName = form.screenName;
			postData.postCode = form.postCode;
			
			// Post the sign up data to register this user
//			$http.post('/signup', postData )
//				.success( function( response ) {
//					console.log( 'Post to signup suceeded with: ' + response );
//					
//					// broadcast the signin success event so we can cause the security context to be set up
//					if( doGPlusSignUp ) {
//						$rootScope.$broadcast('event:google-plus-signin-success', socialProfiles.gPlus.auth);
//					} else {
//						$rootScope.$broadcast('event:facebook-signin-success', socialProfiles.facebook.auth);
//					}
//					
//				})
//				.error(function( error ) {
//					console.log( 'Post to signup errored with: ' + error );
//				});
			
			var UserResource = $resource('/resource/user');
			var user = UserResource.save( postData, function() {
				console.log( 'User: user' );
			});
			
		}
		
		function logout() {
			$http.post('logout', {})
				.success(function() {
					resetSecurity();
					$rootScope.$broadcast( 'user.logout', 'Logout success.' );
		    		
				}).error(function(data) {
					resetSecurity();
					$rootScope.$broadcast( 'user.logout', 'Logout with error' );
				});	
		}
		
		function resetSecurity() {
			userInfo = {
	    		user: 				'ANONYMOUS',
	    		didLoginFail:		false,
	    		auth:				undefined
	    	};
			
			socialProfiles = {
	    		facebook: {
	    			isConnected:	false,
	    			didLoginFail:	false,
	    			user: 			undefined,
	    			auth: 			undefined
	    		},
	    		gPlus: {
	    			isConnected:	false,
	    			didLoginFail:	false,
	    			user: 			undefined,
	    			auth: 			undefined
	    		}
	    	};
		}
		
		return service;
    	
    }
    
	security.$inject = ['$rootScope', '$http', '$q', '$resource' ];
	
    angular.module('app.core').factory( 'security', security );
    

})();
