'use strict';


var allTestFiles = [];
var TEST_REGEXP = /spec\.js$/;

var pathToModule = function(path) {
    //go to 3 levels higher, e.g. from '/base/public/javascripts/app' to '/base'
    return '../../../' + path.replace(/^\/base\//, '').replace(/\.js$/, '');
};

Object.keys(window.__karma__.files).forEach(function(file) {
    if (TEST_REGEXP.test(file)) {
        // Normalize paths to RequireJS module names.
        allTestFiles.push(pathToModule(file));
    }
});

require.config({
    // Karma serves files from '/base'
    //use our sources root
    baseUrl: '/base/public/javascripts/app',

    paths: {
        angular:            '/base/public/javascripts/bower_components/angular/angular',
        angularMocks:       '/base/public/javascripts/bower_components/angular-mocks/angular-mocks',
        angularResource:    '/base/public/javascripts/bower_components/angular-resource/angular-resource',
        angularUIBootstrap: '/base/public/javascripts/bower_components/angular-ui-bootstrap-bower/ui-bootstrap',
        angularUIRouter:    '/base/public/javascripts/bower_components/angular-ui-router/release/angular-ui-router',
        ngTable:            '/base/public/javascripts/bower_components/ng-table/ng-table',
        bootstrap:          '/base/public/javascripts/bower_components/bootstrap/dist/js/bootstrap',
        domReady :          '/base/public/javascripts/bower_components/requirejs-domready/domReady',
        jquery:             '/base/public/javascripts/bower_components/jquery/dist/jquery',
        text:               '/base/public/javascripts/bower_components/requirejs-text/text'
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
        'angularMocks': {
            deps: [ 'angular' ],
            exports :'angular.mock'
        },
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
    ],

    // dynamically load all test files
    deps: allTestFiles,

    // we have to kickoff jasmine, as it is asynchronous
    callback: window.__karma__.start
});