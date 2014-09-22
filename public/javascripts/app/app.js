'use strict';

define([
	'angular',
	'filters',
	'services',
	'directives',
	'controllers'
], function (angular, filters, services, directives, controllers) {
    return angular.module('glossariesApp', [
        'glossariesApp.filters',
        'glossariesApp.services',
        'glossariesApp.directives',
        'glossariesApp.controllers'
    ]);
});
