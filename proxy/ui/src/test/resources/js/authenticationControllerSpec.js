describe('AuthenticationController', function() {

  beforeEach(module('stefanEdberg'));

  beforeEach(function() {
    securityMock = {
      loggedInUser: 					function() {},
      registerFacebookAuthListeners: 	function() {},
      registerGoogleAuthListeners:		function() {},
      hasSocialConnection:				function() {},
      signUp:							function() {}			
    };
    
    spyOn(securityMock, 'loggedInUser').and.returnValue('a user');
	  
    module(function($provide) {
      $provide.value('security', securityMock);
    });
    
  });
  
  var scope, vm, httpBackend;

  beforeEach(inject(function($rootScope, $controller, $httpBackend) {
    scope = $rootScope.$new();
    vm = $controller('AuthenticationController', {$scope: scope});
    httpBackend = $httpBackend;
  }));
  
  it('expect default initialized values', inject(function($controller) {
    expect(vm.user).toBe(undefined);
    expect(vm.authFailureReason).toBe(undefined);
	expect(vm.isAuthenticationInitialized).toBe(false);
	expect(vm.signUpForm).toEqual({});
  }));
  
  it('The user.login.success event sets the user', inject(function($controller) {
	httpBackend.expectGET('main.html').respond(200);
	expect(vm.user).toBe(undefined);
	scope.$broadcast('user.login.success');
	expect(vm.user).not.toBe(undefined);
	expect(securityMock.loggedInUser).toHaveBeenCalled();
  }));
  
  it('The user.login.failed event sets the failure reason', inject(function($controller){
	httpBackend.expectGET('main.html').respond(200);
	vm.user = 'Some user';
	expect(vm.user).not.toBe(undefined);
	scope.$broadcast('user.login.failed', {
		name: 'NoSocialConnections',
		message: 'User does not have any active social connections'
	});
	expect(vm.user).toBe(undefined);
	expect(vm.authFailureReason).not.toBe(undefined);
  }));

});
