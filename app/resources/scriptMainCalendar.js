var date = new Date();
var currentYear = date.getFullYear();
var currentMonth = date.getMonth();

var assignCalendarFunctionality;
var updateCalendar;

var eventNameOld;
var editEventInfoOld;
var eventDateOld;
var eventTimeOld;
var activeEventID;

var username;
var meGroupID;

$(document).ready(function(){
	var accessServer = function(method, url, data, onSuccess, onFail) {
		var xhr = new XMLHttpRequest();
		xhr.open(method, url);
		xhr.onload = function () {
			if (xhr.status === 200) {
				onSuccess(xhr.response);
			}
			else {
				console.log("FAILED TO ACCESS SERVER");
				console.log("DATA: " + data);
				console.log("RESULT: " + xhr.response);
				onFail(xhr.response);
			}
		};

		xhr.send(data);
	};

	var months = ["January", "Feburary", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"];

	assignCalendarFunctionality = function() {
		$(".cal-chevron-left").off();
		$(".cal-chevron-right").off();
		$(".goToTodayButton").off();

		$(".cal-chevron-left").click(function(event) {
			if(--currentMonth == -1) {
				currentYear--;
				currentMonth = 11;
			}

			var groupID = $(event.target).closest("button").parent().parent().parent().parent().attr("groupID");
			updateCalendar(currentYear, currentMonth, groupID);
		});

		$(".cal-chevron-right").click(function() {
			if(++currentMonth == 12) {
				currentYear++;
				currentMonth = 0;
			}

			var groupID = $(event.target).closest("button").parent().parent().parent().parent().attr("groupID");
			updateCalendar(currentYear, currentMonth, groupID);
		});

		$(".goToTodayButton").click(function() {
			date = new Date();
			currentYear = date.getFullYear();
			currentMonth = date.getMonth();

			var groupID = $(event.target).parent().parent().parent().parent().attr("groupID");
			updateCalendar(currentYear, currentMonth, groupID);
		});
	}

	assignCalendarFunctionality();

	updateCalendar = function(year, month, groupID) {
		//update the calendar month heading
		$("#group" + groupID + "Content .cal-month-heading h3").text(months[month] + " " + year);

		//update the days of the month
		var startDay = new Date(year, month, 1).getDay();
		var endDate = new Date(year, month + 1, 0).getDate();
		var row = 1;
		var col = startDay;

		if(startDay > 0) {
			var d = new Date(year, month, 0);
			var lastDayOfPrevMonth = d.getDay();
			var prevMonthIndex = d.getDate();

			for(var i = lastDayOfPrevMonth; i >= 0; i--) {
				var cell = $("#group" + groupID + "Content .cal")[0].rows[1].cells[i];
				cell.getElementsByClassName("day")[0].innerHTML = prevMonthIndex--;
				cell.getElementsByClassName("day")[0].classList.add("text-muted");
				cell.getElementsByClassName("dropdown")[0].classList.add("invisible");
				cell.getElementsByClassName("eventCount")[0].innerHTML = 0;
				cell.getElementsByClassName("dropdown-menu")[0].innerHTML = "";
				cell.style.backgroundColor = "";
			}
		}

		for(var i = 1; i < endDate + 1 && row < 6; i++) {
			var cell = $("#group" + groupID + "Content .cal")[0].rows[row].cells[col];
			cell.getElementsByClassName("day")[0].innerHTML = i;
			cell.getElementsByClassName("day")[0].classList.remove("text-muted");
			cell.getElementsByClassName("dropdown")[0].classList.add("invisible");
			cell.getElementsByClassName("eventCount")[0].innerHTML = 0;
			cell.getElementsByClassName("dropdown-menu")[0].innerHTML = "";
			cell.style.backgroundColor = "";

			if(++col == 7) {
				row++;
				col = 0;
			}

			var today = new Date();
			if(year == today.getFullYear() && month == today.getMonth() && i == today.getDay()) {
				cell.style.backgroundColor = "lightGrey";
			}
		}

		if(row == 5 && col < 7) {
			var index = 1;

			for(var i = col; i < 7; i++) {
				var cell = $("#group" + groupID + "Content .cal")[0].rows[5].cells[col++];
				cell.getElementsByClassName("day")[0].innerHTML = index++;
				cell.getElementsByClassName("day")[0].classList.add("text-muted");
				cell.getElementsByClassName("dropdown")[0].classList.add("invisible");
				cell.getElementsByClassName("eventCount")[0].innerHTML = 0;
				cell.getElementsByClassName("dropdown-menu")[0].innerHTML = "";
				cell.style.backgroundColor = "";
			}
		}

		var data = {}
		data["cookie"] = document.cookie.split("=")[1];
		data["month"] = month + 1;
		data["year"] = year;
		data["groupid"] = groupID;
		data = JSON.stringify(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/calendar/get", data,
			function(result) { //success
				console.log("Successfully retrieved events");

				var json = JSON.parse(result);
				var keys = Object.keys(json);
				var length = keys.length;

				if(length == 0) {
					return;
				}

				for(var i = 0; i < length; i++) {
					var event = json[keys[i]];

					var eventMonth = parseInt(event["time"].split(" ")[0].split("-")[1]);
					var eventYear = parseInt(event["time"].split(" ")[0].split("-")[0]);
					if(eventMonth == month + 1 && eventYear == year) {
						var eventDay = parseInt(event["time"].split(" ")[0].split("-")[2]);
						var eventDate = new Date(year, month, eventDay);
						var col = eventDate.getDay();
						var row = Math.floor((eventDay + startDay - 1) / 7) + 1;

						var cell = $("#group" + groupID + "Content .cal")[0].rows[row].cells[col];
						cell.getElementsByClassName("dropdown")[0].classList.remove("invisible");
						cell.getElementsByClassName("eventCount")[0].innerHTML++;

						var eventDateDispArr = event["time"].split(" ")[0].split("-");
						var eventDateDisp = eventDateDispArr[1] + "/" + eventDateDispArr[2] + "/" + eventDateDispArr[0];
						var eventTimeDisp = event["time"].split(" ")[1];
						var eventTimeDisp = eventTimeDisp.substring(0, 5);

						var eventHTML = `
							<div class="card">
								<div class="card-header">` + event["name"] + 
								`</div>
								<div class="card-body">
									<div>` + event["description"] + `</div>
								</div>
								<div class="card-footer">
									<button type="button" class="btn btn-sm btn-secondary float-right editEventButton">Edit</button>
									<div class="eventTime">` + eventDateDisp + " " + eventTimeDisp + `</div>
								</div>
							</div>`;

						cell.getElementsByClassName("dropdown-menu")[0].innerHTML += eventHTML;
					}
				}

				$(".editEventButton").click(function() {
					var parent = $(this).parent().parent();
					var name = parent.find(".card-header").html();
					var info = parent.find(".card-body p").html();

					var dateFull = parent.find(".eventTime").html().split(" ")[0].split("-");
					var date = dateFull[1] + "/" + dateFull[2] + "/" + dateFull[0];

					var timeFull = parent.find(".eventTime").html().split(" ")[1].split(".")[0].split(":");
					var time = timeFull[0] + ":" + timeFull[2] + " am"; //there is no way for me to know if it is am or pm right now


					$("#editEventModalName").val(name);
					$("#editEventModalInfo").val(info);
					$("#editEventModalDate").val(date);
					$("#editEventModalTime").val(time);

					$("#editEventModal").modal("show");
				});
			},
			function(result) { //fail
				alert("Failed to retrieve event");
			});

		if(groupID == meGroupID) {
			var data = {};
			data["cookie"] = document.cookie.split("=")[1];
			data["groupid"] = groupID;
			data = JSON.stringify(data);

			accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/calendar/all", data,
				function(result) { //success
					console.log("Successfully retrieved all events");
					var json = JSON.parse(result);

					for(var i = 0; i < json.length; i++) {
						var response = json[i]["response"];

						for(var j = 0; j < response["count"]; j++) {
							if(response[j]["username"] == username) {
								console.log(json[i]["name"]);
							}
						}
					}
				},
				function(result) { //fail
					console.log("Failed to retrieve all events");
				});
		}
	};
});