/* global describe: false,
 it: false,
 xit: false,
 expect: false,
 jasmine: false,
 beforeEach: false,
 afterEach: false,
 inject: false */

'use strict';
define(['app', 'jquery', 'text!/base/test/javascript/fixtures/glossaries.json', 'angularMocks'], function(app, $, data) {

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

        it('should contain test data', function() {
            expect(data).toBeDefined();
        });

        it('should be defined', function () {
            expect(app).toBeDefined();
            expect($.isPlainObject(app)).toBeTruthy();
        });

    });

    //if it is defined, then convert text data to JSON
    var jsonData = JSON.parse(data);

    describe('Application filters', function() {
        var interpolateFilter,
        //version should be any
            version = new Date().getTime();

        //load module
        beforeEach(module('glossariesApp'));

        beforeEach(
            module(function($provide) {
                $provide.value('version', version);
            })
        );

        beforeEach(
            inject(function($filter){
                interpolateFilter = $filter('interpolate');
            })
        );

        it('should contain interpolate filter', function(){
            expect(interpolateFilter).toBeDefined();
        });

        it('should replace %VERSION% with the value we provide', function(){
            var initialString = 'version is %VERSION%',
                resultString =  'version is ' + version;

            expect(interpolateFilter(initialString)).toEqual(resultString);
        });
    });

    describe('Application directives', function() {
        //load module
        beforeEach(module('glossariesApp'));


    });

    describe('Glossaries controller', function() {
        var $scope,
            $httpBackend,
            createController;

        //load module
        beforeEach(module('glossariesApp'));

        beforeEach(
            inject(function ($rootScope, $controller, _$httpBackend_) {
                $scope = $rootScope.$new();

                $httpBackend = _$httpBackend_;

                createController = function () {
                    return $controller('GlossariesCtrl', {
                        '$scope': $scope
                    });
                };
            })
        );

        afterEach(function() {
            $httpBackend.verifyNoOutstandingExpectation();
            $httpBackend.verifyNoOutstandingRequest();
        });

        it('should contain People resource, and name matters',
            inject(function ($injector) {
                var correctResourceName = 'Glossary',
                    wrongResourceNames = ['glossary', 'GLOSSARY', 'aaaa'];

                expect($injector.get(correctResourceName)).toBeDefined();

                //iterate over bunch of wrong names
                $.each(wrongResourceNames, function (wrongResourceName) {
                    //anonymous function to wrap a throwable method
                    expect(function () {
                        $injector.get(wrongResourceName);
                    }).toThrow();
                });
            })
        );

        it('should process GlossariesCtrl controller and put tableParams of type "ngTableParams" in $scope',
            inject(function (ngTableParams) {
                //create new controller
                var controller = createController();

                //it should be not null
                expect(controller).toBeDefined();

                //and then expect table params be present in scope
                expect($scope.tableParams).toBeDefined();

                //and then expect table params be type of ngTableParams
                expect($scope.tableParams).toEqual(jasmine.any(ngTableParams));

            })
        );

        it('should resolve data by default',
            inject(function ($q) {
                //pull controller
                createController();

                var spec = this,
                    tableParams = $scope.tableParams,
                    tableSettings = tableParams.settings(),
                    $defer = $q.defer();

                //call JSON data file
                $httpBackend
                    .expectGET('/glossaries?' + $.param({
                        pageSize: tableParams.count(),
                        startRow: (tableParams.page() - 1) * tableParams.count()
                    }))
                    .respond(data);

                tableSettings.getData($defer, tableParams);

                //flush request
                $httpBackend.flush();

                $defer.promise.then(
                    function (glossaries) {
                        //total number should be the same as responded data length
                        expect(tableSettings.total).toEqual(jsonData.totalElements);

                        //number items on page should be greater then 2
                        expect(tableParams.count()).toBeGreaterThan(2);

                        //it should show first page with sorted values
                        expect(glossaries.length).toEqual(tableParams.count());
                    }, function () {
                        spec.fail("there should be no error");
                    }
                );
            })
        );

    });

});