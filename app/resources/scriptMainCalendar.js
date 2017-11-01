var date = new Date();
var currentYear = date.getFullYear();
var currentMonth = date.getMonth();

var assignCalendarFunctionality;
var updateCalendar;

$(document).ready(function(){

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

			var groupID = $(event.target).closest("button").parent().parent().parent().parent().attr("id");
			updateCalendar(currentYear, currentMonth, groupID);
		});

		$(".cal-chevron-right").click(function() {
			if(++currentMonth == 12) {
				currentYear++;
				currentMonth = 0;
			}

			var groupID = $(event.target).closest("button").parent().parent().parent().parent().attr("id");
			updateCalendar(currentYear, currentMonth, groupID);
		});

		$(".goToTodayButton").click(function() {
			date = new Date();
			currentYear = date.getFullYear();
			currentMonth = date.getMonth();

			var groupID = $(event.target).parent().parent().parent().parent().attr("id");
			updateCalendar(currentYear, currentMonth, groupID);
		});
	}

	assignCalendarFunctionality();

	updateCalendar = function(year, month, groupID) {
		//update the calendar month heading
		$("#" + groupID + " .cal-month-heading h3").text(months[month] + " " + year);

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
				var cell = $("#" + groupID + " .cal")[0].rows[1].cells[i];
				cell.innerHTML = prevMonthIndex--;
				cell.classList.add("text-muted");
			}
		}

		for(var i = 1; i < endDate + 1 && row < 6; i++) {
			var cell = $("#" + groupID + " .cal")[0].rows[row].cells[col];
			cell.innerHTML = i;
			cell.classList.remove("text-muted");

			if(++col == 7) {
				row++;
				col = 0;
			}
		}

		if(row == 5 && col < 7) {
			var index = 1;

			for(var i = col; i < 7; i++) {
				var cell = $("#" + groupID + " .cal")[0].rows[5].cells[col++];
				cell.innerHTML = index++;
				cell.classList.add("text-muted");
			}
		}

	};
});