/**
 * Created by yaokuo on 2014/12/14.
 */
define(function(require,exports,module){
    var $ = require('jquery');
    var common = require('../../common');
    var cn = new common();

    var DataHandler = function(){
    };

    module.exports = DataHandler;

    DataHandler.prototype = {
    		DbInfoHandler : function(data){
                var dbInfo = data.data;

                function GetNetAddr(containers){ //获取container IP地址,并拼成字符串返回
                    var ips='';
                    for(var i= 0,len=containers.length;i<len;i++){
                        if(dbInfo.containers[i].type == "mclustervip"){
                            ips = ips+dbInfo.containers[i].ipAddr+' ';
                        }
                        /*注释掉非VIP的IP
                         else{
                            ips = ips+dbInfo.containers[i].ipAddr+' ';
                        }*/
                    }
                    return ips;
                }

    			$("#db_info_db_id").html(dbInfo.dbName);
    			$("#db_info_db_name").html(dbInfo.dbName);
    			if(dbInfo.hcluster) {
    				$("#db_info_available_region").html(dbInfo.hcluster.hclusterNameAlias);
    			}
    			$("#db_info_net_addr").html(GetNetAddr(dbInfo.containers));
                $("#db_info_running_state").html(cn.TranslateStatus(dbInfo.status));
                $("#db_info_create_time").html(cn.TransDate('Y-m-d H:i:s',dbInfo.createTime));
                $("#db_info_remain_days").html(cn.RemainAvailableTime(dbInfo.createTime));
                
                
                var dbconfig = new DataHandler();
                $("#showConfigInfo").click(function(){
                	cn.GetData("/db/gbConfig/" + $("#dbId").val(),dbconfig.getInfo);
                  });
        },
        getInfo: function(data){
        	var $tby = $("#dbConfigTby");
            $tby.find("tr").remove();
        	$('#dbConfigModal').modal({
                backdrop:false,
                show:true
            });
        	
        	$("#dbConfigModalLabel").html("配置信息");
        	var data = data.data;
        	
        	$("#dbConfigInfo").html("<br/>" + cn.formatJson(JSON.stringify(data)));
        }
    }
});