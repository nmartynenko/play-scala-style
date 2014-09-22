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
            //configure ng-table
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

            //CRUD actions
            $scope.edit = function (title, glossaryId) {
                Glossary.get(
                    {id: glossaryId},
                    function(data){
                        openModal(title, data);
                    }
                );
            };

            $scope.add = function(title){
                openModal(title);
            };

            $scope.remove = function (confirmationMessage, glossaryId) {
                if (confirm(confirmationMessage)){
                    Glossary.remove(
                        {id: glossaryId},
                        function () {
                            $scope.tableParams.reload();
                        }
                    );
                }
            };

            //Modal settings
            var openModal = function(title, glossary){
                var modalInstance = $modal.open({
                    templateUrl: 'editGlossaryForm.html',
                    controller: ModalInstanceCtrl,
                    scope: $scope,
                    resolve: {
                        title: function(){
                            return title;
                        },
                        glossary: function () {
                            return glossary || {};
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

            var ModalInstanceCtrl = function ($scope, $modalInstance, title, glossary) {

                $scope.title = title;
                $scope.glossary = glossary;

                $scope.ok = function () {
                    //TODO figure out what is wrong with scope's binding
                    $modalInstance.close($scope.glossary);
                };

                $scope.cancel = function () {
                    $modalInstance.dismiss('cancel');
                };
            };
        });
});
