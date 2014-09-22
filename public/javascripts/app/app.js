'use strict';

define([
	'angular',
	'filters',
	'services',
	'directives',
	'controllers',
	'angularUIRouter',
    'ngTable',
    'angularUIBootstrap'
], function (angular, filters, services, directives, controllers) {
    return angular.module('glossariesApp', [
        'ui.bootstrap',
        'ui.router',
        'ngTable',
        'glossariesApp.filters',
        'glossariesApp.services',
        'glossariesApp.directives',
        'glossariesApp.controllers'
    ]);
});
