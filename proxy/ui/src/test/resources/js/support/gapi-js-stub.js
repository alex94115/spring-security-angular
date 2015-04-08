// inspired by https://github.com/involver/facebook-js-stub
var gapi = (function(){
  var self = {
    client: {
      plus: {
        people: {}
      }
    }
  };

  self.init = function(options) {
    GapiStub.clientId(options.clientId);
  };

  self.client.load = function( api, version, callback ) {
    if (!GapiStub.initialized()) return;
    GapiStub.addApiRequest('client.load', callback);
  };
  
  self.client.plus.people.get = function( params ) {
	  return GapiStub.addDeferredApiRequest('client.plus.people.get');
  };
  
  return self;
}());

var GapiStub = (function() {
  var self = {
    client: {}
  };
  
  var state, apiRequests;

  var initialize = function() {
    state = {
      'clientId': null 
    };
    apiRequests = {};
  };

  initialize();
  
  self.addApiRequest = function(path, callback) {
    apiRequests[path] = callback;
  };
  
  self.addDeferredApiRequest = function(path) {
	var deferredApiRequest = {
      then: {}
	};
	
	apiRequests[path] = deferredApiRequest;
	
	return {
        then: function (success, fail) {
        	deferredApiRequest.then.success = success;
        	deferredApiRequest.then.fail = fail;
        }
    };
  };

  self.findApiRequest = function(path) {
    return apiRequests[path];
  };

  self.clientId = function(id) {
    if( id ) {
      state.clientId = id;
    }
    return state.clientId;
  };

  self.reset = function() {
    initialize();
  };

  self.respondToApiRequest = function(path, response) {
    if (typeof(apiRequests[path]) === 'undefined') return;
    apiRequests[path](response);
  };
  
  self.respondToDeferredApiRequest = function( path, isSuccess, response ) {
	  if (typeof(apiRequests[path]) === 'undefined') return;
	  var fn = apiRequests[path];
	  if( isSuccess ) {
		  fn.then.success( response );
	  } else {
		  fn.then.fail( response );
	  }
  };

  self.initialized = function() {
    var clientId = state.clientId;
    return (typeof(clientId) == 'number' || typeof(clientId) == "string");
  };

  return self;
}());