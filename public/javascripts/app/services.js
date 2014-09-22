'use strict';

define([
  'angular', 
  'angularResource'
], function (angular) {
	angular.module('glossariesApp.services', ['ngResource'])
		.factory('Glossary', function($resource){
            return $resource('/glossaries/:id', {}, {
                query : {method: 'get', isArray: false},
                save : {method: 'post'},
                update : {method: 'put'}
            });
  		});
});
