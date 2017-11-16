var calendarHTML = `
<div style="margin-top: 10px">
	<button type="button" class="btn btn-primary createNewEventButton" data-toggle="modal" data-target="#createEventModal">Create new event</button>
	<button type="button" class="btn btn-primary goToTodayButton">Go to today</button>
</div>
<div class="row-fluid text-center cal-month-heading">
	<button class="btn float-left cal-chevron-left" type="button">
		<img src="resources/chevronLeft.png">
	</button>
	<button class="btn float-right cal-chevron-right" type="button">
		<img src="resources/chevronRight.png">
	</button>
	<h3>Default</h3>
</div>
<table class="table table-bordered cal">
	<thead class="cal-head">
		<tr>
			<th>Sunday</th>
			<th>Monday</th>
			<th>Tuesday</th>
			<th>Wednesday</th>
			<th>Thursday</th>
			<th>Friday</th>
			<th>Saturday</th>
		</tr>
	</thead>
	<tbody class="cal-body">
		<tr>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown invisible">
					<button class="btn btn-sm btn-success dropdown-toggle float-right" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"><span class="eventCount">0</span> Events </button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
		</tr>
	</tbody>
</table>`;