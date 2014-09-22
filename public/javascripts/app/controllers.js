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
        .controller('GlossariesCtrl', function ($scope, $timeout, Glossary, ngTableParams, $modal) {
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
                    Glossary.query(
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
                Glossary.get(
                    {id: glossaryId},
                    openModal
                );
            };

            $scope.add = function(){
                openModal({});
            };

            $scope.remove = function (glossaryId, confirmationMessage) {
                if (confirm(confirmationMessage)){
                    Glossary.remove(
                        {id: glossaryId},
                        function () {
                            $scope.tableParams.reload();
                        }
                    );
                }
            };

            var openModal = function(glossary){
                var modalInstance = $modal.open({
                    templateUrl: 'editGlossaryForm.html',
                    controller: ModalInstanceCtrl,
                    resolve: {
                        title: function(){
                            return "Change Me!";
                        },
                        glossary: function () {
                            return glossary;
                        }
                    }
                });

                modalInstance.result.then(function (glossary) {
                    Glossary.save(glossary,
                        function(){
                            $scope.tableParams.reload();
                        }
                    );
                });
            };

            var ModalInstanceCtrl = function ($scope, $modalInstance, glossary) {

                $scope.glossary = glossary;

                $scope.ok = function () {
                    $modalInstance.close($scope.glossary);
                };

                $scope.cancel = function () {
                    $modalInstance.dismiss('cancel');
                };
            };
        });
});
