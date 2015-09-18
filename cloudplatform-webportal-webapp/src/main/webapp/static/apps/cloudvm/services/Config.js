/**
 * Created by jiangfei on 2015/8/19.
 */
define(['services/app.service'], function (serviceModule) {
  serviceModule.factory('Config', [function () {
    var config = {};
    config.urls = {
      vm_regions: '/ecs/regions/',
      vm_list: '/ecs/region/{region}',
      image_list: '/osi/region/{region}',
      flavor_group_data: '/osf/region/{region}/group',
      vm_create_old: '/ecs/region/{region}/vm-create',
      vm_create: '/ecs/vm/create',
      vm_start: '/ecs/region/{region}/vm-start',
      vm_stop: '/ecs/region/{region}/vm-stop',
      vm_delete: '/ecs/region/{region}/vm-delete',
      vm_disk_type:'/osv/volume/type/list',
      vm_network_shared_list:'/osn/network/shared/list',
    };
    config.vmStatuses = [
      {"text": "活跃", "value": "ACTIVE"},
      {"text": "创建中", "value": "BUILD"},
      {"text": "已暂停", "value": "PAUSED"},
      {"text": "已挂起", "value": "SUSPENDED"},
      {"text": "已删除", "value": "DELETED"},
      {"text": "已停止", "value": "SHUTOFF"},
    ];
    config.vmDiskStatuses = [
      {"text": "创建中", "value": "creating"},
      {"text": "可用的", "value": "available"},
      {"text": "挂载中", "value": "attaching"},
      {"text": "使用中", "value": "in-use"},
      {"text": "删除中", "value": "deleting"},
      {"text": "异常", "value": "error"},
    ];
    return config;
  }]);
});