'use strict';

define([
	'angular',
	'filters',
	'services',
	'directives',
	'controllers',
	'angularUIRouter',
    'ngTable'
], function (angular, filters, services, directives, controllers) {
    return angular.module('glossariesApp', [
        'ui.router',
        'ngTable',
        'glossariesApp.filters',
        'glossariesApp.services',
        'glossariesApp.directives',
        'glossariesApp.controllers'
    ]);
});
