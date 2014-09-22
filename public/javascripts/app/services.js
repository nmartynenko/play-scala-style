'use strict';

define([
  'angular', 
  'angularResource'
], function (angular) {
	angular.module('glossariesApp.services', ['ngResource'])
		.factory('Glossaries', function($resource){
            return $resource('/glossaries', {}, {
                'query': {method: 'GET', isArray: false }
            });
  		});
});
