var contextRoot = null;

$(document).ready(function() {
	contextRoot = $('meta[name=contextRoot]').attr("content");
	if (!contextRoot)
		contextRoot = '';

	
	//reload();
});

function reload() {
	console.log($("#meal-date").val());

	$.ajax({
		url : contextRoot + '/api/1.0/mealmenus?currentDate=' + $("#meal-date").val(),
		success : function(data) {
		}
	});
}
