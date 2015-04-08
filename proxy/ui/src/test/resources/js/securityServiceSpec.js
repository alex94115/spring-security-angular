describe('Unit testing security service', function() {
  console.log('starting securityServiceSpec.js');
  
  var mocks = null;

  beforeEach(module('stefanEdberg'));
  
  beforeEach(function() {
	  // Initialize the mock Facebook API
	  FB.init({appId: 123});
	  gapi.init({clientId: '98930494096-m0n3666pu5qb1dgukv5uhjbh379p2j3i.apps.googleusercontent.com'});
  });
  
  var rootScope, securityService, httpBackend = {};

  beforeEach(inject(function($rootScope, $q, security, $httpBackend) {
	rootScope = $rootScope;
	q = $q;
    securityService = security;
    httpBackend = $httpBackend;
    
    spyOn( rootScope, '$broadcast' ).and.callThrough();
  }));
  
  it('Unknown Facebook user should broadcast a login failed event', function() {
    
    // Simulate the event from the Facebook directive that causes the security service to attempt to log this user in
    rootScope.$broadcast('event:facebook-signin-success', { authResponse: { accessToken: 'ABC123' }} )	;
    rootScope.$apply();

    // 1. expect a call to FB.api and respond with our test user
    FBStub.respondToApiRequest('/me', {
    	  "id": "1383352035311024", 
    	  "email": "ithbtfs_qinstein_1423255377@tfbnw.net", 
    	  "first_name": "Patricia", 
    	  "gender": "female", 
    	  "last_name": "Qinstein", 
    	  "link": "https://www.facebook.com/app_scoped_user_id/1383352035311024/", 
    	  "locale": "en_US", 
    	  "middle_name": "Amijhgbbbche", 
    	  "name": "Patricia Amijhgbbbche Qinstein", 
    	  "timezone": -8, 
    	  "updated_time": "2015-02-06T20:43:03+0000", 
    	  "verified": false
    	});
    
    // 2. Expect a call to login. Respond with a 401 to indicate an unknown user.
    httpBackend.expectPOST('login/social', 'social_provider=FACEBOOK&facebook_userId=1383352035311024&facebook_accessToken=ABC123')
    	.respond(401, {
    		"timestamp": 1425012523933,
    		"status": 401,
    		"error": "Unauthorized",
    		"message":"Authentication Failed: Presented identity: facebook/1383352035311024 did not match verified identity: facebook/1383352035311024",
    		"path":"/login/social"
    });
    rootScope.$apply();
    
    // this pattern seems to be required to allow the test to run when the code being tested throws an 
    // exception, even though the exception is being caught in the then... catch statement	
    try {
    	httpBackend.flush();
    } catch( error ) {
    	// For some reason, this is necessary to avoid an Angular error...
    	rootScope.$$phase = null;
    }
    
    // this causes the catch block to run
    rootScope.$apply();

    // this causes the broadcasted event to be passed to listeners
    rootScope.$apply();
    
    // confirm the expectation
    expect(rootScope.$broadcast).toHaveBeenCalledWith('user.login.failed', { name: 'AuthenticationFailedException', message: 'Unknown user' } );
	  
  });
  
  it('Known Facebook user should broadcast a login success event', function() {
	    
	    // Simulate the event from the Facebook directive that causes the security service to attempt to log this user in
	    rootScope.$broadcast('event:facebook-signin-success', { authResponse: { accessToken: 'ABC123' }} );
	    rootScope.$apply();

	    // 1. expect a call to FB.api and respond with our test user
	    FBStub.respondToApiRequest('/me', {
	    	  "id": "1383352035311024", 
	    	  "email": "ithbtfs_qinstein_1423255377@tfbnw.net", 
	    	  "first_name": "Patricia", 
	    	  "gender": "female", 
	    	  "last_name": "Qinstein", 
	    	  "link": "https://www.facebook.com/app_scoped_user_id/1383352035311024/", 
	    	  "locale": "en_US", 
	    	  "middle_name": "Amijhgbbbche", 
	    	  "name": "Patricia Amijhgbbbche Qinstein", 
	    	  "timezone": -8, 
	    	  "updated_time": "2015-02-06T20:43:03+0000", 
	    	  "verified": false
	    	});
	    
	    
	    // 2. Expect a call to login. Respond with a 200 to indicate an known user.
	    httpBackend.expectPOST('login/social', 'social_provider=FACEBOOK&facebook_userId=1383352035311024&facebook_accessToken=ABC123')
	    	.respond(200, '<!DOCTYPE html><html data-ng-app="stefanEdberg" ng-strict-di><body ng-controller="RootController as root"></body></html>');
    	httpBackend.expectGET('resource/')
			.respond(200, { id: 'abcdefg12345789', content: 'Hello, logged in user!' });
	    
    	httpBackend.flush();
    	rootScope.$apply();
    	
	    // confirm the expectation
	    expect(rootScope.$broadcast).toHaveBeenCalledWith('user.login.success');
		  
	  });
  
  it('Known Google user should broadcast a login success event', function() {
    rootScope.$broadcast('event:google-plus-signin-success', {
      "access_token":"aa999.KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK-wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww",
      "token_type":"Bearer",
    });
	rootScope.$apply();
	 
	GapiStub.respondToApiRequest('client.load', {});
	rootScope.$apply();
	
	GapiStub.respondToDeferredApiRequest('client.plus.people.get', true, { 
		"result": {
		  "kind":	"plus#person",
		  "gender":	"male",
		  "emails":	[{
		    "value":	"somebody@gmail.com",
		    "type":"account"
		  }],
		  "objectType":		"person",
		  "id":				"3333333333333333333333",
		  "displayName":	"Some Body",
		  "name": {
			  "familyName":"Body",
			  "givenName":"Some"
		  },
		  "url":"https://plus.google.com/3333333333333333333333",
		  "verified":false
		},
		"body":"body",
		"status":200,
		"statusText":"OK"
	});
	
    // 2. Expect a call to login. Respond with a 200 to indicate an known user.
    httpBackend.expectPOST('login/social', 'social_provider=GOOGLE_PLUS&gPlus_userId=3333333333333333333333&gPlus_accessToken=aa999.KKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKKK-wwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwwww')
    	.respond(200, '<!DOCTYPE html><html data-ng-app="stefanEdberg" ng-strict-di><body ng-controller="RootController as root"></body></html>');
	httpBackend.expectGET('resource/')
		.respond(200, { id: 'abcdefg12345789', content: 'Hello, logged in user!' });
    
	httpBackend.flush();
	rootScope.$apply();
	
    // confirm the expectation
    expect(rootScope.$broadcast).toHaveBeenCalledWith('user.login.success');

	
  });

});
