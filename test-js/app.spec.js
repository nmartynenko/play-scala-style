/* global describe: false,
 it: false,
 xit: false,
 expect: false,
 jasmine: false,
 beforeEach: false,
 afterEach: false,
 inject: false */

'use strict';

describe('Application', function() {
    var $ = jQuery;

    it('should contain jQuery global object', function() {
        expect($).toBeDefined();
        expect($().jquery).toBeDefined();
    });

    xit('should contain angular global object', function() {
        expect(angular).toBeDefined();
    });

    xit('should contain angular.mock global object', function() {
        expect(angular.mock).toBeDefined();
    });

    it('should contain jasmine global object', function() {
        expect(jasmine).toBeDefined();
    });

    xit('should be defined', function() {
    });

});

describe('Application', function() {

});