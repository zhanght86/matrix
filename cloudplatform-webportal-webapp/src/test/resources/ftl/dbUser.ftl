<html>
<head>
<style type="text/css">
table.gridtable {
	font-family: verdana, arial, sans-serif;
	font-size: 11px;
	color: #333333;
	border-width: 1px;
	border-color: #666666;
	border-collapse: collapse;
}

table.gridtable th {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #dedede;
}

table.gridtable td {
	border-width: 1px;
	padding: 8px;
	border-style: solid;
	border-color: #666666;
	background-color: #ffffff;
	text-align: center; 
}
p {
	margin: 20px
}
</style>
</head>
<body>
<!-- Table goes in the document BODY -->

<h3>${userName}您好：</h3>
<p>数据库用户${type}成功。以下为相关创建信息：</p>
<table class="gridtable" style="margin: 20px">
	<tr>
		<th width="100px">所属数据库</th>
		<th width="100px">用户名</th>
		<th width="100px">密码</th>
		<th width="300px">访问IP</th>
		<th width="100px">最大并发量</th>
	</tr>
	<tr>
		<td>${dbName}</td>
		<td>${dbUserName}</td>
		<td>${dbUserPassword}</td>
		<td align="left">${ip}</td>
		<td>${maxConcurrency}</td>
	</tr>
</table>
<p>本邮件由系统发出，请勿回复。<br/>如有问题，联系系统管理员。</p>
<hr/>
<p>乐视云计算公司<br/>
PAAS云开发团队</p>
</body>
</html>