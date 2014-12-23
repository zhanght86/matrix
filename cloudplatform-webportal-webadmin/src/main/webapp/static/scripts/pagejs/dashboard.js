$(function () {
	getOverview();
	initPieChart('monitorCluster',1);
	initPieChart('monitorNode',2);
	initPieChart('monitorDb',3);
	$('#nav-search').addClass("hidden");
});

function initPieChart(divName,type){
    $('#' + divName).highcharts({
        chart: {
            type: 'pie',
            plotBackgroundColor: null,
            plotBorderWidth: null,
            plotShadow: false
        },
        tooltip: {
	        formatter: function() {
	            return '<b>'+ this.point.name +'</b>: '+ this.point.y ;
	        } 
        },
        title: {
       	 	text:''
        },
        plotOptions: {
        	 pie: {
                 allowPointSelect: true,
                 cursor: 'pointer',
                 dataLabels: {
                     enabled: false
                 },
                 showInLegend: true,
                 dataLabels: {
                     enabled: true,
                     format: '{point.name}:{point.y}'
                 }
             },
            series: {
                cursor: 'pointer',
                events: {
                    click: function(e) {
		                location.href = e.point.url;
					}
				}
            }
        },
        credits:{
        	enabled: false
        }
    });
    setPieChartData(divName,type);
}

function setPieChartData(divName,type){
	var chart = $('#' + divName).highcharts();
	var url = "/list/mcluster/monitor/" + type
	chart.showLoading();
	$.ajax({ 
		type : "get",
		url : "/dashboard/monitor/" + type,
		dataType : "json", 
		contentType : "application/json; charset=utf-8",
		success : function(data) {
			if(error(data)) return;
			var status = data.data;
			var pieChartData=[{
				data: []
	           }];
			
			pieChartData[0].data.push({name:'正常',color:'green',y:status.nothing,url:url});
			pieChartData[0].data.push({name:'危险',color:'#19A2A2',y:status.general,url:url});
			pieChartData[0].data.push({name:'严重危险',color:'#FDC43E',y:status.serious,url:url});
			pieChartData[0].data.push({name:'宕机',color:'#D15A06',y:status.crash,url:url});
			pieChartData[0].data.push({name:'超时',color:'#CC0032',y:status.timeout,url:url});
			
			$('#'+divName+'-nothing').html(status.nothing);
			$('#'+divName+'-general').html(status.general);
			$('#'+divName+'-serious').html(status.serious);
			$('#'+divName+'-crash').html(status.crash);
			$('#'+divName+'-timeout').html(status.timeout);
			
			if(chart.series.length != 0){
				chart.series[0].remove(false);
			}
			chart.hideLoading();
			chart.addSeries(pieChartData[0],false);
			chart.redraw();
		}
	});
}


function updateMclusterChart(divName,type){
	setPieChartData(divName,type);
}

function getOverview(){
	$.ajax({
		type : "get",
		url : "/dashboard/statistics",
		contentType : "application/json; charset=utf-8",
		success : function(data) {
			if(error(data)) return;
			var view = data.data;
			$('#dbSum').html(view.db);
			$('#dbUserSum').html(view.dbUser);
			$('#mclusterSum').html(view.mcluster);
			$('#unauditeDbSum').html(view.dbAudit);
			$('#unauditeDbUserSum').html(view.dbUserAudit);
			$('#hclusterSum').html(view.hcluster);
			$('#hostSum').html(view.host);
			$('#onCreateDbSum').html(view.dbBuilding);
			$('#createFailDbSum').html(view.dbFaild);
			$('#createFailDbUserSum').html(view.dbUserFaild);
		}
	});
}