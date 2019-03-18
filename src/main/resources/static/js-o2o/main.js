var contextRoot = null;

$(document).ready(function() {
	var begin = moment().startOf('week');

	contextRoot = $('meta[name=contextRoot]').attr("content");
	if (!contextRoot)
		contextRoot = '';

	var pickr = $("#meal-date").flatpickr({
		onChange : function(dates, str, pickr) {
			reload();
		}
	});
	pickr.setDate(new Date());

	reload();
});

function reload() {
	console.log($("#meal-date").val());

	$.ajax({
		url : contextRoot + '/api/1.0/mealmenus?currentDate=' + $("#meal-date").val(),
		success : function(data) {
			var html = '';
			if (data != null) {
				var minSDay = null;

				for (var i = 0; i < data.length; i++) {
					if (data[i]['serviceDate'] != null) {
						minSDay = moment(data[i]['serviceDate']);
						break;
					}
				}

				if (minSDay != null) {
					var oriStartDay = minSDay.startOf('week');

					html += '<thead>';
					html += '<tr><td colspan="2" rowspan="2">구분</td><td colspan="18">중식</td><td colspan="7">석식</td></tr>';
					html += '<tr><td colspan="7">Korea (4,500)</td><td colspan="7">Kitchen (5,000)</td><td colspan="2">샐러드바</td><td>잡곡밥</td><td>후식</td><td colspan="7">Kitchen (5,000)</td></tr>';
					html += '</thead>';
					var startDay = moment(oriStartDay);
					
					for (var i = 0; i < 7; i++) {
						var isToday = '';
						if (startDay.isSame($("#meal-date").val()))
							isToday = 'class="table-primary"';

						html += '<tr>';
						html += '<th scope="row" ' + isToday + '>' + startDay.format('ddd') + '</th>';
						html += '<th scope="row" ' + isToday + '>' + startDay.format('YYYY-MM-DD') + '</th>';

            			var menu = null;
            			for (var j = 0; j < data.length; j++) {
            				if (startDay.isSame(data[j]['serviceDate'])) {
            					menu = data[j];
            					break;
            				}
            			}
						
						if (menu != null) {
							var launch1 = null;
							var launch2 = null;
							var dinner = null;

							for (var k = 0; k < menu.meals.length; k++) {
								if (menu.meals[k].mealType == 'LAUNCH_1') {
									launch1 = menu.meals[k];
								} else if (menu.meals[k].mealType == 'LAUNCH_2') {
									launch2 = menu.meals[k];
								} else if (menu.meals[k].mealType == 'DINNER') {
									dinner = menu.meals[k];
								}
							}
							html += makeMealDetailHtml(launch1, isToday);
							html += makeMealDetailHtml(launch2, isToday);

							html += '<td ' + isToday + '>' + menu['salad1'] + '</td>';
							html += '<td ' + isToday + '>' + menu['salad2'] + '</td>';
							html += '<td ' + isToday + '>' + menu['mrice'] + '</td>';
							html += '<td ' + isToday + '>' + menu['dessert'] + '</td>';

							html += makeMealDetailHtml(dinner, isToday);
						} else {
							html += '<td colspan="25" ' + isToday + '></td>';
						}

						html += '</tr>';
						startDay.add(1, 'd');
					}
				} else {
					html += '<tr><td>no data.</td></tr>';
				}
			}
			html += '';

			$('#meal-menu').html(html);
		}
	});
}

function makeMealDetailHtml(menuDetail, isToday) {
	var html = '';
	if (menuDetail != null) {
		html += '<td ' + isToday + '>' + (menuDetail.menu1 == null ? '' : menuDetail.menu1) + '</td>';
		html += '<td ' + isToday + '>' + (menuDetail.menu2 == null ? '' : menuDetail.menu2) + '</td>';
		html += '<td ' + isToday + '>' + (menuDetail.menu3 == null ? '' : menuDetail.menu3) + '</td>';
		html += '<td ' + isToday + '>' + (menuDetail.menu4 == null ? '' : menuDetail.menu4) + '</td>';
		html += '<td ' + isToday + '>' + (menuDetail.menu5 == null ? '' : menuDetail.menu5) + '</td>';
		html += '<td ' + isToday + '>' + (menuDetail.menu6 == null ? '' : menuDetail.menu6) + '</td>';
		html += '<td ' + isToday + '>' + (menuDetail.menu7 == null ? '' : menuDetail.menu7) + '</td>';
	} else {
		html += '<td colspan="7" ' + isToday + '>N/A</td>';
	}
	return html;
}