/*jshint newcap: false */
/*global confirm: false */

'use strict';

define([
    'angular',
    'jquery',
    'services',
    'ngTable',
    'angularUIBootstrap'
], function (angular, $) {

    return angular.module('glossariesApp.controllers', [
        'ui.bootstrap',
        'ngTable',
        'glossariesApp.services'
    ])
        .controller('GlossariesCtrl', function ($scope, $timeout, Glossaries, ngTableParams, $modal) {
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

            $scope.edit = function (glossaryId) {
                var modalInstance = $modal.open({
                    templateUrl: 'editGlossaryForm.html',
                    controller: ModalInstanceCtrl,
                    resolve: {
                        glossary: function () {
                            return glossaryId === 0 ? {} :
                                //todo as example
                            {
                                id : glossaryId,
                                name : "dummy"
                            };
                        }
                    }
                });

                modalInstance.result.then(function (glossary) {
                    //todo save
                    alert('save');
                });
            };

            $scope.remove = function (glossaryId, confirmationMessage) {

                if (confirm(confirmationMessage)){
                    //todo remove
                    alert('remove');
                }
            };

            var ModalInstanceCtrl = function ($scope, $modalInstance, glossary) {

                $scope.glossary = glossary;

                $scope.ok = function () {
                    $modalInstance.close(glossary);
                };

                $scope.cancel = function () {
                    $modalInstance.dismiss('cancel');
                };
            };
        });
});
