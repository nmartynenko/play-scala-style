'use strict';

module.exports = function (config) {
    config.set({
        basePath: '../',
        frameworks: ['jasmine', 'requirejs'],
        files: [
            //lib dependencies
            {pattern: 'public/javascripts/bower_components/**/*.js', included: false},
            {pattern: 'public/javascripts/app/*.js', included: false},
            {pattern: 'public/javascripts/app/**/*.js', included: false},
            //test specs
            {pattern: 'test/javascript/**/*spec.js', included: false},
            {
                pattern: 'test/javascript/fixtures/**/*.json',
                included: false,
                watched: true,
                served: true
            },
            // needs to be last http://karma-runner.github.io/0.12/plus/requirejs.html
            'test/javascript/main-test.js'
        ],

        // list of files to exclude
        exclude: [
            'public/javascripts/app/main.js'
        ],

        preprocessors: {
            'public/javascripts/app/**/*.js': 'coverage'
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
        // if true, it captures browsers, run tests and exit
        singleRun: true

    });
};
