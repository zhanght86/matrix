<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="zh">

<head>
	<meta charset="utf-8"/>
	<meta http-equiv="X-UA-compatible" content="IE=edge,chrome=1"/>
	<meta name="viewport" content="width=device-width,initial-scale=1, maximum-scale=1, user-scalable=no"/>
	<!-- bootstrap css -->
	<link type="text/css" rel="stylesheet" href="${ctx}/static/css/bootstrap.min.css"/>
	<!-- fontawesome css -->
	<link type="text/css" rel="stylesheet" href="${ctx}/static/css/font-awesome.min.css"/>
	<!-- ui-css -->
	<link type="text/css" rel="stylesheet" href="${ctx}/static/css/ui-css/common.css"/>
	<title>关系型数据库RDS</title>
</head>

<body>
	<!-- 全局参数 start -->
	<input class="hidden" value="${dbId}" name="dbId" id="dbId" type="text" />
	<!-- 全局参数 end -->
<%@ include file="../../layouts/header.jsp"%>

<!-- main-content begin-->
<div class="container-fluid">
    <div class="row main-header">
        <!-- main-content-header begin -->
        <div class="col-xs-12 col-sm-6 col-md-6">
            <div class="pull-left">
                <h3>
                    <span class="fa  fa-cubes"></span>
                    <span id="dbName"></span>
                    <span style="display: inline-block;vertical-align:super;">
                        <small id="dbStatus" class="text-success text-xs"></small>
                    </span>
                    <a class="btn btn-default btn-xs" href="${ctx}/list/db">
                        <span class="glyphicon glyphicon-step-backward"></span>
                        返回实例列表
                    </a>
                </h3>
            </div>
        </div>
        <div class="col-sm-6 col-md-6 hidden-xs">
            <div class="pull-right">
                <h3>
                    <small>
                        <span class="pd-r8">
                            <span>功能指南</span>
	                        <a href="/helpCenter/helpCenter.jsp?container=product-RDSIntro" target="_black" style="text-decoration:none;">
	                            <button class="btn btn-default btn-xs">
	                                <span class="glyphicon glyphicon-eject" id="rds-icon-guide"></span>
	                            </button>
                            </a>
                        </span>
                    </small>
                    <!-- <small>
                        <span>
                            <button class="btn-success btn btn-sm" onclick="window.open('http://10.154.28.164/phpMyAdmin')">登录数据库</button>
                        </span>
                    </small> -->
                    <small>
                        <span>
                            <button class="btn-danger btn btn-sm disabled">重启实例</button>
                        </span>
                    </small>
                    <small>
                        <span>
                            <button class="btn-default btn btn-sm disabled">备份实例</button>
                        </span>
                    </small>
                    <small>
                        <span>
                            <button class="btn-default btn btn-sm glyphicon glyphicon-list disabled"></button>
                        </span>
                    </small>
                </h3>
            </div>
        </div>
    </div>
    <!-- main-content-header end-->
    <div class="row">
        <!-- main-content-center-begin -->
        <nav id="sidebar" class="col-sm-2 col-md-2 nav-sidebar-div">
            <div class="sidebar sidebar-line sidebar-selector">
                <ul class="nav nav-sidebar li-underline">
                    <li class="active"><a class="text-sm" src="${ctx}/detail/baseInfo/${dbId}" href="javascript:void(0)">基本信息</a></li>
                    <li><a  class="text-sm" src="${ctx}/detail/account/${dbId}" href="javascript:void(0)">账号管理</a></li>
                    <li><a  class="text-sm" href="javascript:void(0)"><span class="glyphicon glyphicon glyphicon-chevron-right"></span>系统资源监控</a>
                        <ul class="nav hide">
                            <li><a  class="text-sm" src="${ctx}/monitor/dbLink/${dbId}" href="javascript:void(0)">COMDML</a></li>
                            <li><a  class="text-sm" src="${ctx}/monitor/InnoDB/buffer/${dbId}" href="javascript:void(0)">InnoDB缓冲池</a></li>
                            <li><a  class="text-sm" src="${ctx}/monitor/QPS/TPS/${dbId}" href="javascript:void(0)">QPS/TPS</a></li>
                            <%-- <li><a  class="text-sm" src="${ctx}/monitor/cpu/${dbId}" href="javascript:void(0)">cpu使用率</a></li> --%>
                        <!--<li><a  class="text-sm" href="javascript:void(0)">磁盘空间</a></li>
                            <li><a  class="text-sm" href="javascript:void(0)">IOPS</a></li>
                            <li><a  class="text-sm" href="javascript:void(0)">CPU利用率</a></li>
                            <li><a  class="text-sm" href="javascript:void(0)">网络流量</a></li>
                            <li><a  class="text-sm" href="javascript:void(0)">InnoDB读写量</a></li>
                            <li><a  class="text-sm" href="javascript:void(0)">InnoDB读写次数</a></li>
                            <li><a  class="text-sm" href="javascript:void(0)">InnoDB日志</a></li>
                            <li><a  class="text-sm" href="javascript:void(0)">临时表</a></li>
                            <li><a  class="text-sm" href="javascript:void(0)">MyISAM key Buffer</a></li>
                            <li><a  class="text-sm" href="javascript:void(0)">MyISAM读写次数</a></li>
                            <li><a  class="text-sm" href="javascript:void(0)">COMDML</a></li>
                            <li><a  class="text-sm" href="javascript:void(0)">ROWDML</a></li> -->
                        </ul>
                    </li>
                    <li><a  class="text-sm" href="javascript:void(0)" src="${ctx}/list/backup/${dbId}">备份与恢复</a></li>
                    <li><a  class="text-sm" href="javascript:void(0)">参数设置 <p class="pull-right home-orange">敬请期待...</p></a></li>
                    <li><a class="text-sm" href="javascript:void(0)">日志管理<p class="pull-right home-orange">敬请期待...</p></a></li>
                    <li><a  class="text-sm" href="javascript:void(0)">性能优化<p class="pull-right home-orange">敬请期待...</p></a></li>
                    <li><a class="text-sm" href="javascript:void(0)">阈值报警<p class="pull-right home-orange">敬请期待...</p></a></li>
                    <li><a class="text-sm" src="${ctx}/detail/security/${dbId}" href="javascript:void(0)">安全控制</a></li>
                </ul>
            </div>
        </nav>
        
        <div class="embed-responsive embed-responsive-16by9 col-sm-10 col-xm-12"  id="frame-content-div">
        <iframe class="embed-responsive-item " id="frame-content" src="${ctx}/detail/baseInfo/${dbId}" frameBorder=0 scrolling="no"></iframe>
        </div> 
    </div>
</div>
<%@ include file="../../layouts/rToolbar.jsp"%>
</body>
<!-- js -->
<script type="text/javascript" src="${ctx}/static/modules/seajs/2.3.0/sea.js"></script>
<script type="text/javascript">
// Set configuration
seajs.config({
base: "${ctx}/static/modules/",
alias: {
    "jquery": "jquery/2.0.3/jquery.min.js",
    "bootstrap": "bootstrap/bootstrap/3.3.0/bootstrap.js"
}
});
seajs.use("${ctx}/static/page-js/clouddb/layout/main");
</script>

</html>