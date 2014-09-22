'use strict';

define(['angular', 'services'], function (angular, services) {
	angular.module('glossariesApp.filters', ['glossariesApp.services'])
		.filter('interpolate', ['version', function(version) {
			return function(text) {
				return String(text).replace(/\%VERSION\%/mg, version);
			};
	}]);
});
