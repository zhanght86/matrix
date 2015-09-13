/**
 * Created by jiangfei on 2015/7/21.
 */
define(['controllers/app.controller','controllers/VirtualMachine'],function (controllerModule) {

  controllerModule.controller('VmListController', ['$scope', '$http','toaster',
    function ($scope, $http,toaster) {
      $scope.p1Name = 'p1';
      $scope.p2Name = 'p2';
      $scope.vmList = [];
      $scope.leModalOption = {
        id: 'le_modal1',
        content: 'leModal Test',
        isShow: false
      };
      $scope.openModalBox = function () {
        $http.get('/billing/element').success(function (data, status, headers, config) {
            return data;
          }).error(function (data, status, headers, config) {
          });
        $scope.leModalOption.isShow = true;
        toaster.pop('success', "title", '<ul><li>Render html</li></ul>', 5000, 'trustedHtml');
      };
      $scope.getVmList = function () {
        $http.get('/api/vm/list').
          success(function (data, status, headers, config) {
            $scope.vmList = data;
          }).
          error(function (data, status, headers, config) {
          });
      };
    }]);

  controllerModule.controller('VmCreateController', ['$scope', '$http', '$location',
    function ($scope, $http, $location) {
      $scope.name = '';
      $scope.description = '';
      $scope.cpu = '';
      $scope.ram = '';
      $scope.disk = '';
      $scope.createVm = function () {
        $http.post('/api/vm/create', {
          name: $scope.name,
          description: $scope.description,
          cpu: $scope.cpu,
          ram: $scope.ram,
          disk: $scope.disk
        }).
          success(function (data, status, headers, config) {
            $location.url('/');
          }).
          error(function (data, status, headers, config) {
          });
      };
    }]);
});