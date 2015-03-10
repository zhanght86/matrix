<%@ page language="java" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div class="page-content-area">
	<div id="page-header-id" class="page-header">
		<h1>
			<a href="${ctx}/list/mcluster">返回Container集群列表<i class="ace-icon fa fa-reply icon-only"></i></a>	
		</h1>
	</div>
	<input class="hidden" value="${ip}" name="vipaddr" id="vipaddr" type="text" />
	<!-- /.page-header -->
	<div class="row">
		<div class="col-sm-12 col-md-12">
			<div  class="col-sm-6 col-md-9" style="margin-top: 10px;">
			<table class="table table-bordered table-striped table-hover">
			   	<thead>
			       	<tr>
						<th colspan="6" style="text-align: center">db监控信息</th>
					</tr>
				</thead>
			   <tbody id="db_detail_table">
					<tr class="dbuser">
						<td rowspan="5" style="width: 20%;">dbuser</td>
					</tr>
					<tr class="backup">
						<td rowspan="5" style="width: 20%;">backup</td>
					</tr>
					<tr class="existed_db_anti_item">
						<td rowspan="5" style="width: 20%;">existed_db_anti_item</td>
					</tr>
					<tr class="write_read_avaliable">
						<td rowspan="5" style="width: 20%;">write_read_avaliable</td>
					</tr>
					<tr class="wsrep_status">
						<td rowspan="5" style="width: 20%;">wsrep_status</td>
					</tr>
					<tr class="cur_conns">
						<td rowspan="5" style="width: 20%;">cur_conns</td>
					</tr>
				</tbody>
			</table>
			</div>
		</div>
	</div>
</div>

<script src="${ctx}/static/scripts/pagejs/monitor_db_list_detail.js"></script>