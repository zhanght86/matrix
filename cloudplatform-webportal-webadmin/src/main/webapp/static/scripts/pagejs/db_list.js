var currentPage = 1; //第几页 
var recordsPerPage = 10; //每页显示条数
var currentSelectedLineDbName = 1;
	
 $(function(){
	//初始化
	page_init();

	$(document).on('click', 'th input:checkbox' , function(){
		var that = this;
		$(this).closest('table').find('tr > td:first-child input:checkbox')
		.each(function(){
			this.checked = that.checked;
			$(this).closest('tr').toggleClass('selected');
		});
	});
	
	//modal显示创建进度
	var mclusterId;
	$(document).on('click', "[name='buildStatusBoxLink']" , function(){
		mclusterId = $(this).closest('tr').find('input').val();
		if($(this).html().indexOf("正常")>=0){
			$('#buildStatusHeader').html("创建成功");
			status = "1";
		}else if($(this).html().indexOf("创建中")>=0){
			$('#buildStatusHeader').html("<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\"></i>创建中...");
			status = "2";
		}else if($(this).html().indexOf("创建失败")>=0){
			$('#buildStatusHeader').html("创建失败");
			status = "3";
		}
		queryBuildStatus(mclusterId,"new");
	});
	
	$('#create-mcluster-status-modal').on('shown.bs.modal', function(){
		if(status == "2") {
			queryBuildStatusrefresh = setInterval(function() {  
				queryBuildStatus(mclusterId,"update");
			},5000);
		}
	}).on('hidden.bs.modal', function (e) {
		queryBuildStatusrefresh = window.clearInterval(queryBuildStatusrefresh);
		location.reload();
	});
});	
function queryByPage(currentPage,recordsPerPage) {
	$("#tby tr").remove();
	var dbName = $("#nav-search-input").val()?$("#nav-search-input").val():'null';
	$.ajax({ 
		type : "get",
		url : "/db/" + currentPage + "/" + recordsPerPage+"/" + dbName,
		dataType : "json", /*这句可用可不用，没有影响*/
		contentType : "application/json; charset=utf-8",
		success : function(data) {
			error(data);
			var array = data.data.data;
			var tby = $("#tby");
			var totalPages = data.data.totalPages;
			
			for (var i = 0, len = array.length; i < len; i++) {
				var td0 = $("<input class=\"hidden\" type=\"text\" value=\""+array[i].mclusterId+"\"\> ");
				var td1 = $("<td class=\"center\">"
								+"<label class=\"position-relative\">"
								+"<input type=\"checkbox\" class=\"ace\"/>"
								+"<span class=\"lbl\"></span>"
								+"</label>"
							+"</td>");
				var td2;
				if(array[i].status == 6){
					td2 = $("<td>"
							+ "<a href=\"/detail/db/"+array[i].id+"\">"+array[i].dbName+"</a>"
							+ "</td>");
				}else if(array[i].status == 0 ||array[i].status == 3){	
					td2 = $("<td>"
							+ "<a  class=\"danger\" href=\"/audit/db/"+array[i].id+"\">"+array[i].dbName+"</a>"
							+ "</td>");
				}else{
					td2 = $("<td>"
							+ "<a style=\"text-decoration:none;\">"+array[i].dbName+"</a>"
							+ "</td>");
				}
				if(array[i].mcluster){
					var td3 = $("<td>"
 							+ array[i].mcluster.mclusterName
							+ "</td>");
				} else {
					var td3 = $("<td> </td>");
				}
				if(array[i].hcluster){
					var td4 = $("<td>"
 							+ array[i].hcluster.hclusterNameAlias
							+ "</td>");
				} else {
					var td4 = $("<td> </td>");
				}
				var td5 = $("<td>"
						+ array[i].user.userName
						+ "</td>");
				var td6 = $("<td>"
						+ date('Y-m-d H:i:s',array[i].createTime)
						+ "</td>");
				if(array[i].status == 4){
					var td7 = $("<td>"
							+"<a href=\"#\" name=\"dbRefuseStatus\" rel=\"popover\" data-container=\"body\" data-toggle=\"popover\" data-placement=\"top\" data-trigger='hover' data-content=\""+ array[i].auditInfo + "\" style=\"cursor:pointer; text-decoration:none;\">"
							+ translateStatus(array[i].status)
							+"</a>"
							+ "</td>");
				}else if(array[i].status == 2){
					var td7 = $("<td>"
							+"<a name=\"buildStatusBoxLink\" data-toggle=\"modal\" data-target=\"#create-mcluster-status-modal\" style=\"cursor:pointer; text-decoration:none;\">"
							+"<i class=\"ace-icon fa fa-spinner fa-spin green bigger-125\" />"
							+"创建中...</a>"
							+ "</td>");
				}else{
					var td7 = $("<td> <a>"
							+ translateStatus(array[i].status)
							+ "</a></td>");
				}
				
				/* var td6 = $("<td>"
							+"<div class=\"hidden-sm hidden-xs action-buttons\">"
								+"<a class=\"blue\" href=\"#\">"
									+"<i class=\"ace-icon fa fa-play-circle-o bigger-130\"></i>"
								+"</a>"
								+"<a class=\"green\" href=\"#\">"
									+"<i class=\"ace-icon fa  fa-power-off bigger-130\"></i>"
								+"</a>"
								+"<a class=\"red\" href=\"#\">"
									+"<i class=\"ace-icon fa fa-trash-o bigger-130\"></i>"
								+"</a>"
							+"</div>"
							+"<div class=\"hidden-md hidden-lg\">"
								+"<div class=\"inline position-relative\">"
									+"<button class=\"btn btn-minier btn-yellow dropdown-toggle\" data-toggle=\"dropdown\" data-position=\"auto\">"
										+"<i class=\"ace-icon fa fa-caret-down icon-only bigger-120\"></i>"
									+"</button>"
									+"<ul class=\"dropdown-menu dropdown-only-icon dropdown-yellow dropdown-menu-right dropdown-caret dropdown-close\">"
										+"<li>"
											+"<a href=\"#\" class=\"tooltip-info\" data-rel=\"tooltip\" title=\"View\">"
												+"<span class=\"blue\">"
													+"<i class=\"ace-icon fa fa-search-plus bigger-120\"></i>"
												+"</span>"
											+"</a>"
										+"</li>"
										+"<li>"
											+"<a href=\"#\" class=\"tooltip-success\" data-rel=\"tooltip\" title=\"Edit\">"
												+"<span class=\"green\">"
													+"<i class=\"ace-icon fa fa-pencil-square-o bigger-120\"></i>"
												+"</span>"
											+"</a>"
										+"</li>"
										+"<li>"
											+"<a href=\"#\" class=\"tooltip-error\" data-rel=\"tooltip\" title=\"Delete\">"
												+"<span class=\"red\">"
													+"<i class=\"ace-icon fa fa-trash-o bigger-120\"></i>"
												+"</span>"
											+"</a>"
										+"</li>"
									+"</ul>"
								+"</div>"
							+"</div>"
						+"</td>"
					);	 */
					
				if(array[i].status == 0 ||array[i].status == 5||array[i].status == 13){
					var tr = $("<tr class=\"warning\"></tr>");
					
				}else if(array[i].status == 3 ||array[i].status == 4||array[i].status == 14){
					var tr = $("<tr class=\"default-danger\"></tr>");
					
				}else{
					var tr = $("<tr></tr>");
				}
				
				tr.append(td0).append(td1).append(td2).append(td3).append(td4).append(td5).append(td6).append(td7);
				tr.appendTo(tby);
				
				$('[name = "dbRefuseStatus"]').popover();
			}//循环json中的数据 
			
			if (totalPages <= 1) {
				$("#pageControlBar").hide();
			} else {
				$("#pageControlBar").show();
				$("#totalPage_input").val(totalPages);
				$("#currentPage").html(currentPage);
				$("#totalRows").html(data.data.totalRecords);
				$("#totalPage").html(totalPages);
			}
		},
		error : function(XMLHttpRequest,textStatus, errorThrown) {
			$.gritter.add({
				title: '警告',
				text: errorThrown,
				sticky: false,
				time: '5',
				class_name: 'gritter-warning'
			});
	
			return false;
		}
	});
   }

function pageControl() {
	// 首页
	$("#firstPage").bind("click", function() {
		currentPage = 1;
		queryByPage(currentPage,recordsPerPage);
	});

	// 上一页
	$("#prevPage").click(function() {
		if (currentPage == 1) {
			$.gritter.add({
				title: '警告',
				text: '已到达首页',
				sticky: false,
				time: '5',
				class_name: 'gritter-warning'
			});
	
			return false;
			
		} else {
			currentPage--;
			queryByPage(currentPage,recordsPerPage);
		}
	});

	// 下一页
	$("#nextPage").click(function() {
		if (currentPage == $("#totalPage_input").val()) {
			$.gritter.add({
				title: '警告',
				text: '已到达末页',
				sticky: false,
				time: '5',
				class_name: 'gritter-warning'
			});
	
			return false;
			
		} else {
			currentPage++;
			queryByPage(currentPage,recordsPerPage);
		}
	});

	// 末页
	$("#lastPage").bind("click", function() {
		currentPage = $("#totalPage_input").val();
		queryByPage(currentPage,recordsPerPage);
	});
}
//查询Container集群创建过程
function queryBuildStatus(mclusterId,type) {	//type(update或new)
	if(type == "new"){
		$("#build_status_tby tr").remove();
	}
	$.ajax({
		type : "get",
		url : "/build/mcluster/"+mclusterId,
		dataType : "json", /*这句可用可不用，没有影响*/
		success : function(data) {
			error(data);
			var array = data.data;
			var build_status_tby = $("#build_status_tby");
			
			for (var i = 0, len = array.length; i < len; i++) {
				var td1 = $("<td>"
						+ array[i].step
						+"</td>");
				var td2 = $("<td>"
						+ array[i].stepMsg
						+"</td>");
				if(array[i].startTime != null )
				{
					var td3 = $("<td>"
							+ date('Y-m-d H:i:s',array[i].startTime)
							+ "</td>");
				}else{
					var td3 = $("<td>\-</td>");
				}
				if(array[i].endTime != null)
				{
					var td4 = $("<td>"
							+ date('Y-m-d H:i:s',array[i].endTime)
							+ "</td>");
				}else{
					var td4 = $("<td>\-</td>");
				}
				if(array[i].msg != null)
				{
					var td5 = $("<td>"
							+ array[i].msg
							+ "</td>");
				}else{
					var td5 = $("<td>\-</td>");
				}
				
				if(array[i].status == 1){
					var td6 = $("<td>"
							+"<a class=\"green\"><i class=\"ace-icon fa fa-check bigger-120\">成功</a>"
							+ "</td>");
				}else if(array[i].status == 0){
					var td6 = $("<td>"
							+"<a class=\"red\"><i class=\"ace-icon fa fa-times red bigger-120\">失败</a>"
							+ "</td>");
				}else if(array[i].status == 2){
					var td6 = $("<td>"
							+"<a style=\"text-decoration:none;\" class=\"green\"><h5><i class=\"ace-icon fa fa-spinner fa-spin green bigger-120\"/>运行中</h5></a>"
							+ "</td>");
				}else{
					var td6 = $("<td>"
							+"<a class=\"orange\"><i class=\"ace-icon fa fa-coffee orange bigger-120\">等待</a>"
							+ "</td>");
				}
					
				if(array[i].status == 0){
					var tr = $("<tr class=\"danger\"></tr>");
				}else{
					var tr = $("<tr></tr>");
				}
				
				tr.append(td1).append(td2).append(td3).append(td4).append(td5).append(td6);
				if(type == "new"){
					tr.appendTo(build_status_tby);
				}else{
					build_status_tby.find("tr:eq("+i+")").html(tr.html());
				}
			}
		},
		error : function(XMLHttpRequest,textStatus, errorThrown) {
			error(XMLHttpRequest);
			return false;
		}
	});
 }

function searchAction(){
	$('#nav-search-input').bind('keypress',function(event){
        if(event.keyCode == "13")    
        {
        	queryByPage(currentPage, recordsPerPage);
        }
    });
}
function page_init(){
	queryByPage(currentPage, recordsPerPage);
	searchAction();
	pageControl();
}