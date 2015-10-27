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

<h3>尊敬的${userName}用户,</h3>
<p>您的云主机即将到期，请及时续费。以下为详细信息：</p>
<table class="gridtable" style="margin: 20px">
	<tr>
        <th width="100px">地域</th>
		<th width="100px">资源类型</th>
        <th width="100px">资源ID</th>
        <th width="100px">资源名称</th>
		<th width="100px">到期天数（/天）</th>
	</tr>
	<#list resList as res>
	<tr>
		<td>${res.region}</td>
		<td>${res.type}</td>
		<td>${res.id}</td>
		<td>${res.name}</td>
        <td>${res.day}</td>
	</tr>
	</#list>
</table>
<br/>
<p>注意：如不及时续费，到期后资源将被暂停使用。</p>
<hr/>
<p>乐视云计算公司</p>
</body>
</html>