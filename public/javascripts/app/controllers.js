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
                        //TODO error handling
                    );
                }
            };

            //Modal settings
            var openModal = function(title, glossary){
                var modalInstance = $modal.open({
                    templateUrl: 'editGlossaryForm.html',
                    controller: ModalInstanceCtrl,
                    resolve: {
                        title: function(){
                            return title;
                        },
                        glossary: function () {
                            return glossary || {};
                        }
                    }
                });

                modalInstance.result.then(function () {
                    $scope.tableParams.reload();
                });
            };

            var ModalInstanceCtrl = function ($scope, $modalInstance, title, glossary) {

                $scope.title = title;
                $scope.glossary = glossary;

                $scope.ok = function () {
                    var g = $scope.glossary,
                        $action;

                    if (g.id){
                        $action = Glossary.update(g);
                    } else {
                        $action = Glossary.save(g);
                    }

                    $action.$promise.then(
                        function(){
                            $modalInstance.close($scope.glossary);
                        },
                        function(){
                            //TODO error handling
                        }
                    );

                };

                $scope.cancel = function () {
                    $modalInstance.dismiss('cancel');
                };
            };
        });
});
