var contextRoot = null;

$(document).ready(function() {
	contextRoot = $('meta[name=contextRoot]').attr("content");
	if (!contextRoot)
		contextRoot = '';

    var pickr = $("#meal-date").flatpickr({
        onChange : function(dates, str, pickr) {
            reload();
        }
    });
    pickr.setDate(new Date());

    $('#dialog-add').on('show.bs.modal', function (e) {
        $('#form-edit-l1-menu1').val('');
        $('#form-edit-l1-menu2').val('');
        $('#form-edit-l1-menu3').val('');
        $('#form-edit-l1-menu4').val('');
        $('#form-edit-l1-menu5').val('');
        $('#form-edit-l1-menu6').val('');
        $('#form-edit-l1-menu7').val('');
        $('#form-edit-l2-menu1').val('');
        $('#form-edit-l2-menu2').val('');
        $('#form-edit-l2-menu3').val('');
        $('#form-edit-l2-menu4').val('');
        $('#form-edit-l2-menu5').val('');
        $('#form-edit-l2-menu6').val('');
        $('#form-edit-l2-menu7').val('');
        $('#form-edit-salad1').val('');
        $('#form-edit-salad2').val('');
        $('#form-edit-mrice').val('');
        $('#form-edit-dessert').val('');
        $('#form-edit-d1-menu1').val('');
        $('#form-edit-d1-menu2').val('');
        $('#form-edit-d1-menu3').val('');
        $('#form-edit-d1-menu4').val('');
        $('#form-edit-d1-menu5').val('');
        $('#form-edit-d1-menu6').val('');
        $('#form-edit-d1-menu7').val('');


        if (isEdit) {
            $.ajax({
                url : contextRoot + '/api/1.0/mealmenu?currentDate=' + $("#meal-date").val(),
                success : function(data) {
                    menuData = data;
                    if (data != null && data != '') {
                        var launch1 = null;
                        var launch2 = null;
                        var salad1 = null;
                        var salad2 = null;
                        var mrice = null;
                        var dessert = null;
                        var dinner = null;

                        for (var k = 0; k < data.meals.length; k++) {
                            if (data.meals[k].mealType == 'LAUNCH_1') {
                                launch1 = data.meals[k];
                            } else if (data.meals[k].mealType == 'LAUNCH_2') {
                                launch2 = data.meals[k];
                            } else if (data.meals[k].mealType == 'DINNER') {
                                dinner = data.meals[k];
                            }
                        }

                        if (launch1 != null) {
                            $('#form-edit-l1-menu1').val(launch1['menu1']);
                            $('#form-edit-l1-menu2').val(launch1['menu2']);
                            $('#form-edit-l1-menu3').val(launch1['menu3']);
                            $('#form-edit-l1-menu4').val(launch1['menu4']);
                            $('#form-edit-l1-menu5').val(launch1['menu5']);
                            $('#form-edit-l1-menu6').val(launch1['menu6']);
                            $('#form-edit-l1-menu7').val(launch1['menu7']);
                        }else {}
                        if(launch2 != null){
                            $('#form-edit-l2-menu1').val(launch2['menu1']);
                            $('#form-edit-l2-menu2').val(launch2['menu2']);
                            $('#form-edit-l2-menu3').val(launch2['menu3']);
                            $('#form-edit-l2-menu4').val(launch2['menu4']);
                            $('#form-edit-l2-menu5').val(launch2['menu5']);
                            $('#form-edit-l2-menu6').val(launch2['menu6']);
                            $('#form-edit-l2-menu7').val(launch2['menu7']);
                        }else{}
                           $('#form-edit-salad1').val(data['salad1']);
                           $('#form-edit-salad2').val(data['salad2']);
                           $('#form-edit-mrice').val(data['mrice']);
                           $('#form-edit-dessert').val(data['dessert']);
                        if(dinner != null){
                             $('#form-edit-d1-menu1').val(dinner['menu1']);
                             $('#form-edit-d1-menu2').val(dinner['menu2']);
                             $('#form-edit-d1-menu3').val(dinner['menu3']);
                             $('#form-edit-d1-menu4').val(dinner['menu4']);
                             $('#form-edit-d1-menu5').val(dinner['menu5']);
                             $('#form-edit-d1-menu6').val(dinner['menu6']);
                             $('#form-edit-d1-menu7').val(dinner['menu7']);
                        }
                        }
                }
            });
        } else {
         reload();
        }
    });

    $('#form-edit-btn-save').click(function(e) {
        if (!isEdit) {
            menuData = {};
            menuData['serviceDate'] = $("#meal-date").val();
        }

        if (menuData['meals'] == null) {
            menuData['meals'] = [];
        }

        var launch1 = null;
        var launch2 = null;
        var dinner = null;

        for (var k = 0; k < menuData.meals.length; k++) {
            if (menuData.meals[k].mealType == 'LAUNCH_1') {
                launch1 = menuData.meals[k];
            } else if (menuData.meals[k].mealType == 'LAUNCH_2') {
                launch2 = menuData.meals[k];
            } else if (menuData.meals[k].mealType == 'DINNER') {
                dinner = menuData.meals[k];
            }
        }

        if (launch1 == null) {
            launch1 = {};
            launch1['mealType']= 'LAUNCH_1';

            menuData.meals.push(launch1);
        }
        launch1['menu1'] = $('#form-edit-l1-menu1').val();
        launch1['menu2'] = $('#form-edit-l1-menu2').val();
        launch1['menu3'] = $('#form-edit-l1-menu3').val();
        launch1['menu4'] = $('#form-edit-l1-menu4').val();
        launch1['menu5'] = $('#form-edit-l1-menu5').val();
        launch1['menu6'] = $('#form-edit-l1-menu6').val();
        launch1['menu7'] = $('#form-edit-l1-menu7').val();

        if (launch2 == null) {
            launch2 = {};
            launch2['mealType']= 'LAUNCH_2';

            menuData.meals.push(launch2);
        }
        launch2['menu1'] = $('#form-edit-l2-menu1').val();
        launch2['menu2'] = $('#form-edit-l2-menu2').val();
        launch2['menu3'] = $('#form-edit-l2-menu3').val();
        launch2['menu4'] = $('#form-edit-l2-menu4').val();
        launch2['menu5'] = $('#form-edit-l2-menu5').val();
        launch2['menu6'] = $('#form-edit-l2-menu6').val();
        launch2['menu7'] = $('#form-edit-l2-menu7').val();

       menuData['salad1'] = $('#form-edit-salad1').val();
       menuData['salad2'] = $('#form-edit-salad2').val();
       menuData['mrice'] = $('#form-edit-mrice').val();
       menuData['dessert'] = $('#form-edit-dessert').val();

        if (dinner == null) {
            dinner = {};
            dinner['mealType']= 'DINNER';

            menuData.meals.push(dinner);
        }
        dinner['menu1'] = $('#form-edit-d1-menu1').val();
        dinner['menu2'] = $('#form-edit-d1-menu2').val();
        dinner['menu3'] = $('#form-edit-d1-menu3').val();
        dinner['menu4'] = $('#form-edit-d1-menu4').val();
        dinner['menu5'] = $('#form-edit-d1-menu5').val();
        dinner['menu6'] = $('#form-edit-d1-menu6').val();
        dinner['menu7'] = $('#form-edit-d1-menu7').val()



        console.log(JSON.stringify(menuData));

        if (isEdit) {
            $.ajax({
                type: 'PUT',
                url:  contextRoot + '/api/1.0/mealmenu',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(menuData),
                dataType: 'json',
                success: function(data) {
                    console.log('good' + data);
                },
                complete: function() {
                    $('#dialog-add').modal('hide');
                    reload();
                }
            });
        } else {
            $.ajax({
                type: 'POST',
                url:  contextRoot + '/api/1.0/mealmenu',
                contentType: 'application/json; charset=utf-8',
                data: JSON.stringify(menuData),
                dataType: 'json',
                success: function(data) {
                    console.log('good' + data);
                },
                complete: function() {
                    $('#dialog-add').modal('hide');
                    reload();
                }
            });
        }
           //reload();
    });
    //

    reload();
});

var isEdit = false;
var menuData = null;

function reload() {
	console.log($("#meal-date").val());

	$.ajax({
		url : contextRoot + '/api/1.0/mealmenu?currentDate=' + $("#meal-date").val(),
		success : function(data) {
			var html = '';

			console.log(data);
            if (data != null && data != '') {
                isEdit = true;
                $('#meal-create').html("수정");
                $('#dialog-add-title').html("메뉴 수정");

                var launch1 = null;
                var launch2 = null;
                var salad1 = null;
                var salad2 = null;
                var mrice = null;
                var dessert = null;
                var dinner = null;

                for (var k = 0; k < data.meals.length; k++) {
                    if (data.meals[k].mealType == 'LAUNCH_1') {
                        launch1 = data.meals[k];
                    } else if (data.meals[k].mealType == 'LAUNCH_2') {
                        launch2 = data.meals[k];
                    } else if (data.meals[k].mealType == 'DINNER') {
                        dinner = data.meals[k];
                    }
                }


                if (launch1 != null) {
                    for (var i = 1; i <= 7; i++) {
                        if (launch1['menu' + i] == null || launch1['menu' + i] == '') {
                            $('#table-l1-' + i).html('-');
                        } else {
                            $('#table-l1-' + i).html(launch1['menu' + i]);
                        }
                    }
                } else {
                    for (var i = 1; i <= 7; i++) {
                        $('#table-l1-' + i).html('-');
                    }
                }
                if (launch2 != null) {
                    for (var i = 1; i <= 7; i++) {
                        if (launch2['menu' + i] == null || launch2['menu' + i] == '') {
                            $('#table-l2-' + i).html('-');
                        } else {
                            $('#table-l2-' + i).html(launch2['menu' + i]);
                        }
                    }
                } else {
                    for (var i = 1; i <= 7; i++) {
                        $('#table-l2-' + i).html('-');
                    }
                }
                if(data.salad1 == null || data.salad1 == ''){
                    $('#table-salad1').html('-');
                }else{
                    $('#table-salad1').html(data.salad1);
                }
               if(data.salad2 == null || data.salad2 == ''){
                    $('#table-salad2').html('-');
                }else{
                    $('#table-salad2').html(data.salad2);
                }
               if(data.mrice == null || data.mrice == ''){
                    $('#table-mrice').html('-');
                }else{
                    $('#table-mrice').html(data.mrice);
                    console.log(data.mrice);
                }
               if(data.dessert== null || data.dessert == ''){
                    $('#table-dessert').html('-');
                }else{
                    $('#table-dessert').html(data.dessert);
                }
                if (dinner != null) {
                    for (var i = 1; i <= 7; i++) {
                        if (dinner['menu' + i] == null || dinner['menu' + i] == '') {
                            $('#table-d1-' + i).html('-');
                        } else {
                            $('#table-d1-' + i).html(dinner['menu' + i]);
                        }
                    }
                } else {
                    for (var i = 1; i <= 7; i++) {
                        $('#table-d1-' + i).html('-');
                    }
                }
            } else {
                isEdit = false;
                $('#meal-create').html("추가");
                $('#dialog-add-title').html("메뉴 추가");
            }

			//$('#meal-menu').html(html);
		}
	});
}