var date = new Date();
var currentYear = date.getFullYear();
var currentMonth = date.getMonth();

var assignCalendarFunctionality;
var updateCalendar;

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
			}
		}

		for(var i = 1; i < endDate + 1 && row < 6; i++) {
			var cell = $("#group" + groupID + "Content .cal")[0].rows[row].cells[col];
			cell.getElementsByClassName("day")[0].innerHTML = i;
			cell.getElementsByClassName("day")[0].classList.remove("text-muted");
			cell.getElementsByClassName("dropdown")[0].classList.add("invisible");
			cell.getElementsByClassName("eventCount")[0].innerHTML = 0;
				cell.getElementsByClassName("dropdown-menu")[0].innerHTML = "";

			if(++col == 7) {
				row++;
				col = 0;
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

				//endpoint needs to return an array of events but it's fine for now
				if(Object.keys(json).length == 0) {
					return;
				}

				for(var i = 0; i < 1; i++) {
					var event = json["event"];

					//temporary fix for event bug
					var eventMonthTemp = parseInt(event["time"].split(" ")[0].split("-")[1]);
					var eventYearTemp = parseInt(event["time"].split(" ")[0].split("-")[0]);
					if(eventMonthTemp == month + 1 && eventYearTemp == year) {
						var eventDay = parseInt(event["time"].split(" ")[0].split("-")[2]);
						var eventDate = new Date(year, month, eventDay);
						var col = eventDate.getDay();
						var row = Math.floor((eventDay + startDay - 1) / 7) + 1;

						var cell = $("#group" + groupID + "Content .cal")[0].rows[row].cells[col];
						cell.getElementsByClassName("dropdown")[0].classList.remove("invisible");
						cell.getElementsByClassName("eventCount")[0].innerHTML++;

						var eventHTML = `
							<div class="card">
								<div class="card-header">
									` + event["name"] + `
								</div>
								<div class="card-body">
									<img class="float-left" style="margin-right: 10px" src="resources/groupDefaultPhoto.jpg" alt="Default event photo" class="img-thumbnail" width="100">
									<p>` + event["description"] + `</p>
								</div>
								<div class="card-footer">
									` + event["time"] + `
									<button type="button" class="btn btn-sm btn-secondary float-right">Edit</button>
								</div>
							</div>`;

						cell.getElementsByClassName("dropdown-menu")[0].innerHTML += eventHTML;
					}
				}
			},
			function(result) { //fail
				alert("Failed to retrieve event");
			});
	};
});