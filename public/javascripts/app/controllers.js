/*jshint newcap: false */

'use strict';

define(['angular', 'jquery', 'services'], function (angular, $) {

    return angular.module('glossariesApp.controllers', ['glossariesApp.services'])
        .controller('GlossariesCtrl', function ($scope, $timeout, Glossaries, ngTableParams) {
            $scope.tableParams = new ngTableParams({
                page: 1,            // show first page
                count: 3,           // count per page
                filter: {           // default filtering
                    name: ''
                },
                sorting: {          // default sorting
                    name: 'asc'
                }
            }, {
                total: 0, // length of data
                counts: [
                    2, 3, 5        // pager's counts
                ],
                getData: function ($defer, params) {
                    Glossaries.get(
                        {
                            startRow: (params.page() - 1) * params.count(),
                            pageSize: params.count()
                        },
                        function (data) {
                            $timeout(function () {
                                // update table params
                                params.total(data.totalElements);
                                // set new data
                                $defer.resolve(data.content);
                            }, 200);
                        }
                    );
                }
            });
        });
});
