$(document).ready(function(){

	var cookie = document.cookie.split("=")[1];

	//This stops the notification menu from closing when it's clicked on
	$("#notificationMenu").click(function(event){
		event.stopPropagation();
	});

	var assignFunctionality = function() {
		$("#vPillsTab a").on("shown.bs.tab", function(event) {
			//Fix previous pill stuff
			prevPill = event.relatedTarget.getAttribute("href");
			$(prevPill + " .nav a").removeClass("active");
			$(prevPill + " .tab-content .tab-pane").removeClass("active show");

			//Fix current pill stuff
			var currPill = event.target.getAttribute("href");
			$(currPill + " .nav a:first").tab("show");

			//update the calendar
			var date = new Date();
			currentYear = date.getFullYear();
			currentMonth = date.getMonth();
			updateCalendar(currentYear, currentMonth, currPill.substring(1));
		});

		//Chevron button to hide group information
		$(".chevron").click(function() {
			if(!$(this).hasClass("collapsed")) {
				$(this).find("img").attr("src","resources/chevronDown.png");
				$('[id^="chatbox_"]').css('height', '32em');
			}
			else {
				$(this).find("img").attr("src","resources/chevronUp.png");
				$('[id^="chatbox_"]').css('height', '21em');
			}
		});

		//Send message buttons
		$('[id^="sendMessage_"]').click(function() {
			var buttonId = this.id.toString();
			var groupId = buttonId.replace('sendMessage_', '').toString();
			var textBoxId = 'message_' + groupId;
			var message = document.getElementById(textBoxId).value; //Message being sent
			var username; //Username that sent the message
			document.getElementById(textBoxId).value = '';

			var data = {};
			data["cookie"] = cookie;
	
			accessServer("POST", "https://scheduleit.duckdns.org/api/user/getsettings", JSON.stringify(data),
				function(result) { //success
					var json = JSON.parse(result);
					username = json.username;
				},
				function(result) { //fail
					console.log(data);
					console.log(result);
					alert("Failed to obtain user account settings");
			});
			var a = new Date();
			var year = a.getFullYear();
			var month = a.getMonth();
			var day = a.getDay();
			var hours = a.getHours();
			var minutes = a.getMinutes();
			var seconds = a.getSeconds();
			var timeStamp = year + ":" + month + ":" + day + " " + hours + ":" + minutes + ":" + seconds; //Timestamp for message

			var myJson = {};
			myJson["username"] = username;
			myJson["groupID"] = groupId;
			myJson["time"] = timeStamp;
			myJson["line"] = message;
			accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/chat", JSON.stringify(myJson),
				function(result) {
					//Success that message
				},
				function(result) {
					console.log(myJson);
					console.log(result);
					alert("Failed to send message");				}
			);
		});
	};

	assignFunctionality();

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

	//NOTIFICATIONS
	var assignNotificationFunctionality = function() {
		$(".friendRequestAcceptButton").off();
		$(".friendRequestDeclineButton").off();
		$(".groupInviteAcceptButton").off();
		$(".groupInviteDeclineButton").off();

		$(".friendRequestAcceptButton").click(function(event) {
			var data = {};
			data["cookie"] = cookie;
			data["notification"] = {};
			data["notification"]["id"] = $(event.target).parent().attr("notifID");
			data["notification"]["type"] = "invite.friend";
			data["response"] = {};
			data["response"]["accept"] = "true";
			data = JSON.stringify(data);

			accessServer("POST", "https://scheduleit.duckdns.org/api/user/notifications/respond", data,
				function(result) { //success
					console.log("Successfully accepted friend request");

					updateFriends();
					updateNotifications();
				},
				function(result) { //fail
					alert("Failed to accept friend request");
				});
		});

		$(".friendRequestDeclineButton").click(function(event) {
			var data = {};
			data["cookie"] = cookie;
			data["notification"] = {};
			data["notification"]["id"] = $(event.target).parent().attr("notifID");
			data["notification"]["type"] = "invite.friend";
			data["response"] = {};
			data["response"]["accept"] = "false";
			data = JSON.stringify(data);

			console.log(data);

			accessServer("POST", "https://scheduleit.duckdns.org/api/user/notifications/respond", data,
				function(result) { //success
					console.log("Successfully declined friend request");

					updateNotifications();
				},
				function(result) { //fail
					alert("Failed to decline friend request");
				});
		});

		$(".groupInviteAcceptButton").click(function(event) {
			var data = {};
			data["cookie"] = cookie;
			data["notification"] = {};
			data["notification"]["id"] = $(event.target).parent().attr("notifID");
			data["notification"]["type"] = "invite.group";
			data["response"] = {};
			data["response"]["accept"] = "true";
			data = JSON.stringify(data);

			accessServer("POST", "https://scheduleit.duckdns.org/api/user/notifications/respond", data,
				function(result) { //success
					console.log("Successfully accepted group invite");
					updateGroups();
					updateNotifications();
				},
				function(result) { //fail
					alert("Failed to accept group invite");
				});
		});

		$(".groupInviteDeclineButton").click(function(event) {
			var data = {};
			data["cookie"] = cookie;
			data["notification"] = {};
			data["notification"]["id"] = $(event.target).parent().attr("notifID");
			data["notification"]["type"] = "invite.group";
			data["response"] = {};
			data["response"]["accept"] = "false";
			data = JSON.stringify(data);

			accessServer("POST", "https://scheduleit.duckdns.org/api/user/notifications/respond", data,
				function(result) { //success
					console.log("Successfully declined group invite");
					updateNotifications();
				},
				function(result) { //fail
					alert("Failed to decline group invite");
				});
		});
	};

	var updateNotifications = function() {
		var data = {};
		data["cookie"] = cookie;
		data = JSON.stringify(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/notifications/get", data,
			function(result) { //success
				console.log("Successfully retrieved notifications");

				$("#notificationMenu").empty();

				if(result == "") {
					$("#notificationsBadge").text("0");
					return;
				}

				var json = JSON.parse(result);

				$("#notificationsBadge").text(json.length);

				for(var i = 0; i < json.length; i++) {
					var notification = json[i];

					if(notification["type"] == "invite.friend") {
						var notifID = notification["id"];
						var fullName = notification["data"]["fullName"];
						//var picture = notification["data"]["picture"];

						var html = `
							<!-- friend request -->
							<div class="card">
								<div class="card-header">
									Friend request
								</div>
								<div class="card-body">
									<img class="float-left" src="resources/profileDefaultPhoto.png" alt="Default Profile Photo" width="80" class="img-thumbnail">
									<p class="card-text">` + fullName + ` would like to add you as a friend</p>
								</div>
								<div class="card-footer">
									<div class="float-right" notifID="` + notifID + `">
										<button type="button" class="btn btn-primary btn-sm friendRequestAcceptButton">Accept</button>
										<button type="button" class="btn btn-danger btn-sm friendRequestDeclineButton">Decline</button>
									</div>
								</div>
							</div>
							`;

						$("#notificationMenu").append(html);
						//might need to assign the functionality of the accept/decline buttons
					}
					else if(notification["type"] == "invite.group") {
						var id = notification["id"];
						var name = notification["data"]["groupname"];
						//var picture = notification["data"]["picture"];

						var html = `
							<!-- group invite -->
							<div class="card">
								<div class="card-header">
									Group invite
								</div>
								<div class="card-body">
									<img class="float-left" src="resources/groupDefaultPhoto.jpg" alt="Default Profile Photo" width="80" class="img-thumbnail">
									<p class="card-text">You have been invited to join ` + name + `</p>
								</div>
								<div class="card-footer">
									<div class="float-right" notifID="` + id + `">
										<button type="button" class="btn btn-primary btn-sm groupInviteAcceptButton">Accept</button>
										<button type="button" class="btn btn-danger btn-sm groupInviteDeclineButton">Decline</button>
									</div>
								</div>
							</div>
							`;

						$("#notificationMenu").append(html);
					}

					assignNotificationFunctionality();
				}
			},
			function(result) { //fail
				alert("Failed to retrived notifications");
			});
	};

	//update notifications every 30 seconds
	setInterval(updateNotifications, 30000);
	updateNotifications();


	//SETTINGS MODAL
	var fullName;
	var username;
	var email;
	var phoneNumber;
	$("#accountSettingsButton").click(function() {
		//populate the account settings modal fields

		//cookie = document.cookie;
		var data = {};
		data["cookie"] = cookie;
		data = JSON.stringify(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/getsettings", data,
			function(result) { //success
				console.log("Successfully obtained account settings");

				var json = JSON.parse(result);

				fullName = json.fullname;
				username = json.username;
				email = json.email
				phoneNumber = json.phone;

				$("#settingsModalFullNameField").val(fullName);
				$("#settingsModalUsernameField").val(username);
				$("#settingsModalEmailField").val(email);
				$("#settingsModalPhoneNumberField").val(phoneNumber);
				$("#settingsModalChangePasswordField").val("");
				$("#settingsModalConfirmPasswordField").val("");
				$("#settingsModalPicture").attr("src", "resources/profileDefaultPhoto.png");
			},
			function(result) { //fail
				alert("Failed to obtain account settings");
			});
	});

	$("#accountSettingsModalSaveButton").click(function() {
		var fullNameChanged = $("#settingsModalFullNameField").val();
		var emailChanged = $("#settingsModalEmailField").val();
		var phoneNumberChanged = $("#settingsModalPhoneNumberField").val();
		var passwordChanged = $("#settingsModalChangePasswordField").val();
		var confirmPasswordChanged = $("#settingsModalConfirmPasswordField").val();
		//var picURL = ...

		var data = {};
		data["username"] = username;
		data["cookie"] = cookie;

		if(fullName != fullNameChanged) {
			data["fullname"] = fullNameChanged;
		}

		if(email != emailChanged) {
			data["email"] = emailChanged;
		}

		if(phoneNumber != phoneNumberChanged) {
			data["phone"] = phoneNumberChanged;
		}

		if(passwordChanged != "") {
			if(passwordChanged == confirmPasswordChanged) {
				data["pass"] = passwordChanged;
			}
			else {
				alert("Passwords do not match");
				return;
			}
		}

		data = JSON.stringify(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/edit", data,
			function(result) { //success
				console.log("Successfully saved user account settings");

				$("#accountSettingsModal").modal("hide");
			},
			function(result) { //fail
				alert("Failed to save user account settings");
			});

	});
	$("#accountSettingsModalDeleteAccountButton").click(function() {
		
	});

	//LOGOUT BUTTON
	$("#logoutButton").click(function() {
		document.cookie = "cookie=";
		window.location.href = "https://scheduleit.duckdns.org/";
	});

	//GROUPS
	var updateGroups = function() {
		var data = {};
		data["cookie"] = cookie;
		data = JSON.stringify(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/get", data,
			function(result) { //success
				console.log("Successfully retrieved groups");

				var json = JSON.parse(result);

				var l = $("#vPillsTab").children().length;
				for(var i = 0; i < l; i++) {
					$("#vPillsTab").children().eq(0).remove();
					$("#vPillsContent").children().eq(0).remove();
				}

				for(var i = 0; i < json.length; i++) {
					var realID = json[i]["id"];
					var id = "group" + realID + "Content";
					var name = json[i]["name"];
					var info = "...";

					var tabHTML = `<a class="nav-link" data-toggle="pill" href="#` + id + `" role="tab">` + name + `</a>`;
					var contentHTML = `
						<!-- Group -->
						<div class="tab-pane fade" id="` + id + `" role="tabpanel" groupID="` + realID + `" groupName="` + name + `">
							<div class="collapse show" id="` + id + "Collapse" + `">
								<div class="card card-group">
									<div class="card-body">
										<img src="resources/groupDefaultPhoto.jpg" alt="Default Group Photo" class="img-thumbnail" width="100">
										<h3>` + name + `</h3>
										<p>` + info + `</p>`;

										if(name != "Me") {
											contentHTML += '<button type="button" class="btn btn-secondary btn-sm groupSettingsButton" data-toggle="modal" data-target="#groupSettingsModal">Group settings</button>';
										}
						
					contentHTML += `
									</div>
								</div>
							</div>
							
							<ul class="nav nav-tabs nav-fill" role="tablist">
								<li class="nav-item">
									<a class="nav-link" data-toggle="tab" href="#` + id + "Chat" + `" role="tab">Chat</a>
								</li>
								<li class="nav-item">
									<a class="nav-link" data-toggle="tab" href="#` + id + "Cal" + `" role="tab">Calendar</a>
								</li>
								<button class="btn chevron" type="button" data-toggle="collapse" data-target="#` + id + "Collapse" + `" aria-expanded="false">
									<img src="resources/chevronUp.png">
								</button>
							</ul>
							<div class="tab-content">
								<div class="tab-pane show" id="` + id + "Chat" + `" role="tabpanel"  style="padding: 2%">
									<div id="` + id + "_wrapper" + `">
										<div id="` + "chatbox_" + id + `" style="border-radius: 0.25em; text-align:left;margin-bottom:1%;background:#fff;height:21em;transition: 0.25s ease-out; width:100%; border:1px solid rgb(220, 220, 220); overflow:auto"></div>
											 
										<form name="message" action="">
											<input name="usermsg" type="text" id="` + "message_" +  id + `" style="width: 53em; border:1px solid rgb(220, 220, 220)" maxlength="1000">
											<button type="button" class="btn btn-primary" id="` + "sendMessage_" + id + `"  style="width: 5em; margin-right: 0.5em; margin-left: 0.5em">Send</button>
											<button type="button" class="btn btn-secondary" id="` + id + "_sendBot" + `"  style="width: 6em">Chatbot</button>
										</form>
									</div>
								</div>
								<div class="tab-pane" id="` + id + "Cal" + `" role="tabpanel">
									<div style="margin-top: 10px">
										<button type="button" class="btn btn-primary">Create new event</button>
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
												<td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td><td>7</td>
											</tr>
											<tr>
												<td>8</td><td>9</td><td>10</td><td>11</td><td>12</td><td>13</td><td>14</td>
											</tr>
											<tr>
												<td>15</td><td>16</td><td>17</td><td>18</td><td>19</td><td>20</td><td>21</td>
											</tr>
											<tr>
												<td>22</td><td>23</td><td>24</td><td>25</td><td>26</td><td>27</td><td>28</td>
											</tr>
											<tr>
												<td>29</td><td>30</td><td>31</td><td></td><td></td><td></td><td></td>
											</tr>
										</tbody>
									</table>
								</div>
							</div>
						</div>`;

					$("#vPillsContent").append(contentHTML);
					$("#vPillsTab").append(tabHTML);
				}

				//make first tab active
				$("#vPillsTab").children().eq(0).addClass("active");
				$("#vPillsContent").children().eq(0).addClass("show active");
				$("#vPillsContent .nav-tabs a").first().addClass("active");
				$("#vPillsContent .tab-content .tab-pane").first().addClass("active");

				assignFunctionality();
				assignCalendarFunctionality();

				$(".groupSettingsButton").off();
				$(".groupSettingsButton").click(function(event) {
					console.log("clicked");
					//populate the group settings modal with group information
					var parent = $(event.target).parent().parent().parent().parent();

					$(".groupFriendsList").attr("groupID", parent.attr("groupID"));

					$("#groupSettingsModalName").val(parent.attr("groupName"));
					//info & pic...
				});
			},
			function(result) { //fail
				alert("Failed to retrieved groups");
			});
	};

	updateGroups();

	var assignGroupModalInviteFriendsFunctionality = function(modal) {
		$("body").on("click", ".groupFriendsList img", function(event) {
			var nameField = $("#groupSettingsModalName");
			var name = nameField.val();

			if(name == "") {
				nameField.addClass("is-invalid");
			}
			else {
				var data = {};
				data["cookie"] = cookie;
				data["invitee"] = $(event.target).parent().text();
				data["invitedto"] = $(event.target).parent().parent().attr("groupID");
				data = JSON.stringify(data);

				accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/invite", data,
					function(result) { //success
						console.log("Successfully invited user to group");
						alert("Successfully invited user to group");
					},
					function(result) { //fail
						alert("Failed to invite user to group");
					});

				nameField.removeClass("is-invalid");
			}
		});

	};

	assignGroupModalInviteFriendsFunctionality();

	$("#newGroupModalCreateButton").click(function() {
		var nameField = $("#newGroupModalName");
		var infoField = $("#newGroupModalInfo");

		var name = nameField.val();
		var info = infoField.val();

		if(name == "") {
			nameField.addClass("is-invalid");
		}
		else {
			var data = {};
			data["cookie"] = cookie;
			data["groupname"] = name;
			data = JSON.stringify(data);

			accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/create", data,
				function(result) { //success
					console.log("Successfully created group");

					updateGroups();
				},
				function(result) { //fail
					alert("Failed to create group");
				});

			$("#newGroupModal").modal("hide");

			nameField.val("");
			infoField.val("");
			nameField.removeClass("is-invalid");
		}
	});

	$("#newGroupModalCancelButton").click(function() {
		$("#newGroupModalName").val("");
		$("#newGroupModalInfo").val("");
		$("#newGroupModalName").removeClass("is-invalid");
	});

	$("#groupSettingsSaveButton").click(function() {
		//Write the changed values to the database


		$("#groupSettingsModal").modal("hide");
	});

	$("#groupSettingsLeaveGroupButton").click(function() {
		var data = {};
		data["cookie"] = cookie;
		data["id"] = "group id to leave";
		data = JSON.stringify(data);

		accessServer("POST", "leave group endpoint", data,
			function(result) { //success
				console.log("Successfully left group");

				//update the groups
			},
			function(result) { //fail
				alert("Failed to leave group");
			});
	});

	//FRIENDS --------------------------------
	var updateFriends = function() {
		$("#friendsList").empty();
		$("body").off("click", "#friendsList img");

		$(".groupFriendsList").empty();

		var data = {};
		data["cookie"] = cookie;
		data = JSON.stringify(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/friends/get", data,
			function(result) { //success
				console.log("Successfully retrieved friends");

				var json = JSON.parse(result);

				for(var i = 0; i < json.friends.length; i++) {
					var friendListHTML = '<li class="list-group-item"><img class="float-right" src="resources/remove.png" width="18px" />' + json.friends[i] + '</li>';
					$("#friendsList").append(friendListHTML);

					var groupFriendListHTML = '<li class="list-group-item"><img class="float-right" src="resources/plus.png" width="18px" />' + json.friends[i] + '</li>';
					$(".groupFriendsList").append(groupFriendListHTML);
				}
			},
			function(result) { //fail
				alert("Failed to retrieved friends");
			});

		$("body").on("click", "#friendsList img", function() {
			var data = {};
			data["cookie"] = cookie;
			data["username"] = $(this).parent().text();
			data = JSON.stringify(data);

			accessServer("POST", "https://scheduleit.duckdns.org/api/user/friends/remove", data,
				function(result) { //success
					console.log("Successfully removed friend");
					updateFriends();
				},
				function(result) { //fail
					alert("Failed to remove friend");
				});

		});
	};

	updateFriends();

	//This needs to be changed to send a friend request instead of automatically add them as a friend
	$("#sendFriendRequestButton").click(function() {
		var username = $("#sendFriendRequestTextbox").val();

		var data = {}
		data["cookie"] = cookie;
		data["username"] = username;
		data = JSON.stringify(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/friends/invite", data,
			function(result) { //success
				console.log("Successfully sent friend request");
				alert("Successfully sent friend request");
				$("#sendFriendRequestTextbox").val("");
			},
			function(result) { //fail
				alert("Failed to send friend request");
			});
	});
});
