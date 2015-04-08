(function() {
    'use strict';

    angular.module('app.core', [
        /*
         * Angular modules
         */
        'ngRoute',
        'ngResource',
        
        /*
         * Our reusable cross app code modules
         */
        'directive.facebook', 
        'directive.g+signin'
        
        /*
         * 3rd Party modules
         */
    ]);
    
})();