$(function() {
	disableEnterkey();
});

function disableEnterkey() {
	$("input").on("keydown", function(e) {
		if((e.which && e.which === 13) || (e.keyCode && e.keyCode === 13)) {
			return false;
		} else {
			return true;
		}
	});
}

function addNewTravelItem() {
	$.ajax({
		url: $("#baseURI").val() + "/miscLab/ajaxLab/travelItemLi.ajax",
		method: "POST",
		dataType: "html",
		data: {
			listNo: $("#travelItemUl").children().length + 1,
			name: $("#addNewName").val()
		}
	}).done(function(html) {
		$("#travelItemUl").append(html);
	});

	return false;
}

function editTravelItem(listNo) {
	$.ajax({
		url: $("#baseURI").val() + "/miscLab/ajaxLab/editTravelItem.ajax",
		method: "POST",
		dataType: "json",
		data: {
			listNo: listNo,
			checked: $("#travelItemList\\." + listNo + "\\.checked\\.1").val()
		}
	}).done(function(res) {
		if(!res || !res.status || res.status != "OK") {
			alert("error");
		}
	});

	return false;
}
