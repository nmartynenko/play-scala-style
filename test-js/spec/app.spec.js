/* global describe: false,
 it: false,
 xit: false,
 expect: false,
 jasmine: false,
 beforeEach: false,
 afterEach: false,
 inject: false */

'use strict';
define(['app', 'jquery', 'angularMocks'], function(app, $) {

    describe('Application', function () {
        it('should contain jQuery global object', function () {
            expect($).toBeDefined();
            expect($().jquery).toBeDefined();
        });

        it('should contain angular global object', function () {
            expect(angular).toBeDefined();
        });

        it('should contain angular.mock global object', function () {
            expect(angular.mock).toBeDefined();
        });

        it('should contain jasmine global object', function () {
            expect(jasmine).toBeDefined();
        });

        it('should be defined', function () {
            expect(app).toBeDefined();
            expect($.isPlainObject(app)).toBeTruthy();
        });

    });

    describe('Application', function () {

    });
});