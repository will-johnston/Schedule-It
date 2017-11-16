var calendarHTML = `
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
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
		</tr>
		<tr>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
			<td>
				<div class="dropdown">
					<button class="btn btn-sm btn-success dropdown-toggle float-right invisible" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">...</button>
					<div class="dropdown-menu event-dropdown">
					</div>
				</div>
				<div class="day">1</div>
			</td>
		</tr>
	</tbody>
</table>`;