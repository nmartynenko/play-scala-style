/*jshint newcap: false */

'use strict';

define(['angular', 'jquery', 'services'], function (angular, $) {

    return angular.module('glossariesApp.controllers', ['glossariesApp.services'])
        .controller('PeopleCtrl', function ($scope, $filter, People, ngTableParams) {
            People.query(function(people) {

                //convert result in more convenient one
                var data = $.map(people.people, function(value){
                    return $.extend({}, value.person, {
                        //add extra field, called 'name'
                        name : value.person.firstName + ' ' + value.person.lastName
                    });
                });

                $scope.tableParams = new ngTableParams({
                    page: 1,            // show first page
                    count: 5,           // count per page
                    filter: {           // default filtering
                        name : ''
                    },
                    sorting: {          // default sorting
                        name: 'asc'
                    }
                }, {
                    total: data.length, // length of data
                    counts: [
                        3, 5, 10        // pager's counts
                    ],
                    getData: function ($defer, params) {
                        // use build-in angular filter
                        // pass through filter
                        var filteredData = params.filter() ?
                            $filter('filter')(data, params.filter()) :
                            data;

                        // sort
                        var orderedData = params.sorting() ?
                            $filter('orderBy')(filteredData, params.orderBy()) :
                            filteredData;

                        $scope.users = orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count());

                        // change total count after filtering
                        params.total(orderedData.length);

                        $defer.resolve($scope.users);
                    }
                });
            });
        });

});
