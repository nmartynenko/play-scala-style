'use strict';

require.config({
	paths: {
		angular:            '../bower_components/angular/angular',
		angularResource:    '../bower_components/angular-resource/angular-resource',
        angularUIBootstrap: '../bower_components/angular-ui-bootstrap-bower/ui-bootstrap-tpls',
		angularUIRouter:    '../bower_components/angular-ui-router/release/angular-ui-router',
        ngTable:            '../bower_components/ng-table/ng-table',
	    bootstrap:          '../bower_components/bootstrap/dist/js/bootstrap',
	    domReady :          '../bower_components/requirejs-domready/domReady',
		jquery:             '../bower_components/jquery/dist/jquery',
		text:               '../bower_components/requirejs-text/text'
	},
	shim: {
		'angular' : {
			deps : [
				'domReady',
        		'jquery'
      		],
			exports : 'angular'
		},
		'angularResource': [ 'angular' ],
		'angularUIBootstrap' : {
      		deps : [
        		'angular'
      		]
		},
		'angularUIRouter' : {
      		deps : [
        		'angular'
      		]
		},
		'ngTable' : {
      		deps : [
        		'angular'
      		]
		},
    	jquery : {
      		exports : '$'
		},
        bootstrap : {
            deps : [
                'jquery'
            ]
        }
	},
	priority: [
		'angular'
	]
});

require(['angular', 'app'], function ( angular, app ) {

  require(['domReady!'], function ( document ) {
    try {
        angular.bootstrap( document, [app.name] );
    }
    catch (e) {
        console.error(e.stack || e.message || e);
    }
  });
});
