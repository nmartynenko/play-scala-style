/* global module : true, env : true
 */

'use strict';

module.exports = function (config) {
    config.set({
        basePath: '../',
//        TODO migrate to Require JS
        frameworks: ['jasmine'/*, 'requirejs'*/],
        files: [
            //lib dependencies
            'public/javascripts/jquery-1.9.0.min.js',
            //'public/javascripts/angular.js',
            //'public/javascripts/angular-route.js',
            //'public/javascripts/angular-resource.js',
            //'public/javascripts/angular-animate.js',
            //'public/javascripts/angular-sanitize.js',
            'public/javascripts/jquery-ui.min.js',
            'public/javascripts/jquery.validate.js',
            'public/javascripts/jquery.dataTables.js',
            'public/javascripts/handlebars.js',
            'public/javascripts/globalize.js',
            'public/javascripts/json2.js',
            //main js file
            'public/javascripts/main.js',
            //test specs
            'test-js/**/*spec.js'
        ],

        // list of files to exclude
        exclude: [

        ],

        preprocessors: {
            'src/main/webapp/html/js/angular/**/*.js': 'coverage'
        },

        // test results reporter to use
        reporters: ['progress', 'coverage'],

        // web server port
        port: 9876,

        // enable / disable colors in the output (reporters and logs)
        colors: true,

        // level of logging
        logLevel: config.LOG_INFO,

        // enable / disable watching file and executing tests whenever any file changes
        autoWatch: true,

        // Start these browsers
        browsers: ['PhantomJS'],

        // If browser does not capture in given timeout [ms], kill it
        captureTimeout: 60000,

        // Continuous Integration mode
        // if true, it capture browsers, run tests and exit
        singleRun: true

    });
};
