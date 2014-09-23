'use strict';

define([
  'angular', 
  'angularResource'
], function (angular) {
	angular.module('glossariesApp.services', ['ngResource'])
		.factory('Glossary', function($resource){
            return $resource('/glossaries/:id', {}, {
                query : {method: 'GET', isArray: false},
                save : {method: 'PUT'},
                update : {method: 'POST'}
            });
  		});
});
