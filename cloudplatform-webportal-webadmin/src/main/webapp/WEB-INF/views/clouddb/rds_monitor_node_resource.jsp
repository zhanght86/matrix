<%@ page language="java" pageEncoding="UTF-8"%>
<script>
	$(window).load(function() {
		var iw=document.body.clientWidth;
		if(iw>767){//sm&&md&&lg
			$('.queryOption').removeClass('collapsed');
		}else{$('#Physicalcluster').removeClass('chosen-select');}
	});
	$(window).resize(function(event) {
		var iw=document.body.clientWidth;
		if(iw>767){//sm&&md&&lg
			$('.queryOption').removeClass('collapsed');
		}else{$('#Physicalcluster').removeClass('chosen-select');}
	});
</script>
<div class="page-content-area">
	<div class="row">
	<div class="widget-box widget-color-blue ui-sortable-handle queryOption collapsed">
		<div class="widget-header hidden-md hidden-lg">
			<h5 class="widget-title">rds资源监控条件</h5>
			<div class="widget-toolbar">
				<a href="#" data-action="collapse">
					<i class="ace-icon fa fa-chevron-down"></i>
				</a>
			</div>
		</div>
		<div class="widget-body">
			<div class="page-header col-sm-12 col-xs-12 col-md-12">
				<div class="input-group pull-right col-sm-12 col-xs-12 col-md-12">
					<form class="form-inline">
						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<input type="text" class="form-control" id="hostIp"
								placeholder="请输入主机名">
						</div>
						<div class="form-group col-sm-6 col-xs-12 col-md-2">
							<input type="text" class="form-control" id="hostTag"
								placeholder="请输入标签">
						</div>
						<div class="form-group col-sm-6 col-xs-12 col-md-3">
							<button class="btn btn-sm btn-primary btn-search" id="monitorDbSearch" type="button">
								<i class="ace-icon fa fa-search"></i>搜索
							</button>
							<button class="btn btn-sm" type="button" id="monitorDbClear">清空</button>
							<button class="btn btn-sm btn-success" type="button" id="updateMonitorData">刷新</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>
		<div class="widget-box widget-color-blue ui-sortable-handle col-xs-12">
			<!-- <div class="widget-header">
				<h5 class="widget-title">rds健康监控列表</h5>
			</div> -->
			<div class="widget-body">
				<div class="widget-main no-padding">
					<table id="mcluster_list" class="table table-striped table-bordered table-hover">
					<thead class="monitor-table-headr-group">
						<tr class="header-one"> 
							<th colspan=2 >服务器</th>
							<th colspan=2>连接池资源</th>
							<th colspan=2>文件资源</th>
							<th colspan=3>表资源</th>
						</tr>
						<tr class="header-two">
							<th  class="table-head-sort" target-data="HOST_IP">主机</th>
							<th>标签</th>
							<th>最大连接数</th>
							<th>最大连接错误数</th>
							<th>最大打开文件数</th>
							<th class="table-head-sort" target-data="HAD_OPEN_FILE">已打开文件数</th>
							<th>表缓存数</th>
							<th class="table-head-sort" target-data="HAD_OPEN_TABLE">已打开表</th>
							<th class="table-head-sort" target-data="CACHE_TABLE_NOHIT_COUNT">表缓存未命中数</th>
						</tr>
					</thead>
						<tbody id="tby">
						</tbody>
					</table>
				</div>
			</div>
		</div>
		<div id="pageControlBar">
			<input type="hidden" id="totalPage_input" />
			<ul class="pager">
				<li><a href="javascript:void(0);" id="firstPage">&laquo首页</a></li>
				<li><a href="javascript:void(0);" id="prevPage">上一页</a></li>
				<li><a href="javascript:void(0);" id="nextPage">下一页</a></li>
				<li><a href="javascript:void(0);" id="lastPage">末页&raquo</a></li>
	
				<li class='hidden-480'><a>共<lable id="totalPage"></lable>页</a>
				</li>
				<li class='hidden-480'><a>第<lable id="currentPage"></lable>页</a>
				</li>
				<li class='hidden-480'><a>共<lable id="totalRows"></lable>条记录</a>
				</li>
			</ul>
		</div>
	</div>
</div>
<!-- /.page-content-area -->
<script src="${ctx}/static/scripts/pagejs/rds/rds_monitor_node_resource.js"></script>