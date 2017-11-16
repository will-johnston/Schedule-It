$(document).ready(function(){

	var cookie = document.cookie.split("=")[1];
	var activeGroupID;

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

			//update the current active group id
			activeGroupID = $(currPill).attr("groupID");

			//update the calendar
			var date = new Date();
			currentYear = date.getFullYear();
			currentMonth = date.getMonth();
			updateCalendar(currentYear, currentMonth, activeGroupID);
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

		$('[id^="sendMessage_"]').click(function() {
				var buttonId = this.id.toString();
				var groupId = activeGroupID;
				var textBoxId = 'message_' + groupId;
				console.log("This is the id: " + groupId);
				console.log("This is the button id: " + buttonId);
				var message = document.getElementById(textBoxId).value; //Message being sent
				var username; //Username that sent the message
				document.getElementById(textBoxId).value = '';
	
				var myJson = {};
				myJson["cookie"] = cookie;
				myJson["groupID"] = groupId;
				myJson["line"] = message;
				accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/chat", JSON.stringify(myJson),
					function(result) {
						//Success that message
						console.log("Message sent.");
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

		$(".groupEventGoingButton").click(function(event) {
			var data = {};
			data["cookie"] = cookie;
			data["notification"] = {};
			data["notification"]["id"] = $(event.target).parent().attr("notifID");
			data["notification"]["type"] = "event.invite";
			data["response"] = {};
			data["response"]["status"] = "going";
			data = JSON.stringify(data);

			accessServer("POST", "https://scheduleit.duckdns.org/api/user/notifications/respond", data,
				function(result) { //success
					console.log("Successfully responded to event");
					updateNotifications();
				},
				function(result) { //fail
					alert("Failed to respond to event");
				});
		});

		$(".groupEventMaybeGoingButton").click(function(event) {
			var data = {};
			data["cookie"] = cookie;
			data["notification"] = {};
			data["notification"]["id"] = $(event.target).parent().attr("notifID");
			data["notification"]["type"] = "event.invite";
			data["response"] = {};
			data["response"]["status"] = "maybeGoing";
			data = JSON.stringify(data);

			accessServer("POST", "https://scheduleit.duckdns.org/api/user/notifications/respond", data,
				function(result) { //success
					console.log("Successfully responded to event");
					updateNotifications();
				},
				function(result) { //fail
					alert("Failed to respond to event");
				});
		});

		$(".groupEventNotGoingButton").click(function(event) {
			var data = {};
			data["cookie"] = cookie;
			data["notification"] = {};
			data["notification"]["id"] = $(event.target).parent().attr("notifID");
			data["notification"]["type"] = "event.invite";
			data["response"] = {};
			data["response"]["status"] = "notGoing";
			data = JSON.stringify(data);

			accessServer("POST", "https://scheduleit.duckdns.org/api/user/notifications/respond", data,
				function(result) { //success
					console.log("Successfully responded to event");
					updateNotifications();
				},
				function(result) { //fail
					alert("Failed to respond to event");
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

				var json = JSON.parse(result);

				if(json[0] == null) {
					$("#notificationsBadge").text("0");
					return;
				}

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
					else if(notification["type"] == "invite.event") {
						var id = notification["id"];
						var name = notification["name"];

						var html = `
							<!-- group invite -->
							<div class="card">
								<div class="card-header">
									Event added
								</div>
								<div class="card-body">
									<img class="float-left" src="resources/groupDefaultPhoto.jpg" alt="Default Profile Photo" width="80" class="img-thumbnail">
									<p class="card-text">An event has been created: ` + name + `</p>
								</div>
								<div class="card-footer">
									<div class="float-right" notifID="` + id + `">
										<button type="button" class="btn btn-primary btn-sm groupEventGoingButton">Going</button>
										<button type="button" class="btn btn-primary btn-sm groupEventMaybeGoingButton">Maybe going</button>
										<button type="button" class="btn btn-danger btn-sm groupEventNotGoingButton">Not going</button>
									</div>
								</div>
							</div>
							`;

						$("#notificationMenu").append(html);
					}
					else if(notification["type"] == "eventReminder") {
						var id = notification["id"];
						var name = notification["name"];

						var html = `
							<!-- event reminder -->
							<div class="card">
								<div class="card-header">
									Upcoming event
								</div>
								<div class="card-body">
									<img class="float-left" src="resources/groupDefaultPhoto.jpg" alt="Default Profile Photo" width="80" class="img-thumbnail">
									<p class="card-text">` + name + ` in one day</p>
								</div>
								<div class="card-footer">
									<div class="float-right" notifID="` + id + `">
										<button type="button" class="btn btn-primary btn-sm eventReminderDismissButton">Dismiss</button>
									</div>
								</div>
							</div>
							`;
					}

					assignNotificationFunctionality();
				}
			},
			function(result) { //fail
				//alert("Failed to retrived notifications");
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

				//Create a me tab if there are no groups
				if(json.length == 0) {
					var data = {};
					data["cookie"] = cookie;
					data["groupname"] = "Me";
					data = JSON.stringify(data);

					accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/create", data,
						function(result) { //success
							console.log("Successfully created me group");
							updateGroups();
						},
						function(result) { //fail
							console.log("Failed to created me group");
						});

					return;
				}

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
										<div disabled class="chatBox" id="` + "chatbox_" + realID + `" style="resize:none; border-radius: 0.25em; text-align:left;margin-bottom:1%;background:#fff;height:21em;transition: 0.25s ease-out; width:100%; border:1px solid rgb(220, 220, 220); overflow:auto"></div>
											 
										<form name="message" action="">
											<input name="usermsg" class="chatbotTextField" type="text" id="` + "message_" +  realID + `" style="width: 53em; border:1px solid rgb(220, 220, 220)" maxlength="1000">
											<button type="button" class="btn btn-primary" id="` + "sendMessage_" + realID + `"  style="width: 5em; margin-right: 0.5em; margin-left: 0.5em">Send</button>
											<button type="button" class="btn btn-secondary chatbotButton" id="` + "sendBot_" + realID + `"  style="width: 6em">Chatbot</button>
										</form>
									</div>
								</div>
								<div class="tab-pane" id="` + id + "Cal" + `" role="tabpanel">
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
									` + calendarHTML + `
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

				//update the first tab's calendar
				var date = new Date();
				currentYear = date.getFullYear();
				currentMonth = date.getMonth();
				var firstTabID = $("#vPillsContent .tab-pane").first().attr("groupID");
				updateCalendar(currentYear, currentMonth, firstTabID);

				//set active group ID to first group
				activeGroupID = $("#vPillsContent .tab-pane").first().attr("groupID");

				$(".groupSettingsButton").off();
				$(".groupSettingsButton").click(function(event) {
					console.log("clicked");
					//populate the group settings modal with group information
					var parent = $(event.target).parent().parent().parent().parent();

					$(".groupFriendsList").attr("groupID", parent.attr("groupID"));

					$("#groupSettingsModalName").val(parent.attr("groupName"));
					//info & pic...
				});

				$(".chatbotButton").off();
				$(".chatbotButton").click(function() {
					//get user ID
					var data = {};
					data["cookie"] = cookie;
					data = JSON.stringify(data);

					accessServer("POST", "https://scheduleit.duckdns.org/api/user/getId", data,
						function(result) { //success
							var userID = result.substring(1);
							userID = userID.substring(0, userID.length - 1);
							userID = parseInt(userID);

							var messageSend = $("#group" + activeGroupID + "Content .chatbotTextField").val();

							var data = {};
							data["text"] = "<" + activeGroupID + "> <" + userID + "> " + messageSend;
							data = JSON.stringify(data);

							console.log(data);

							accessServer("POST", "https://willjohnston.pythonanywhere.com/api/chatterbot/", data,
								function(result) { //success
									console.log("Successfully sent message to chat bot");
									var json = JSON.parse(result);

									var messageRecieved = json["text"];
									var html = "<p>Chatbot: " + messageRecieved + "</p>";
									$("#group" + activeGroupID + "Content input").append(html);
								},
								function(result) { //fail
									alert("Failed to send message to chat bot");
								});
						},
						function(result) { //fail
							alert("Failed to get user ID");
						});
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
		data["groupid"] = activeGroupID;
		data = JSON.stringify(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/leave", data,
			function(result) { //success
				console.log("Successfully left group");

				updateGroups();
			},
			function(result) { //fail
				alert("Failed to leave group");
			});
	});

	$("#groupSettingsModalMuteGroup").click(function() {
		var data = {};
		data["cookie"] = cookie;
		data["groupid"] = activeGroupID;
		data["mute"] = "true";
		data = JSON.stringify(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/mute", data,
			function(result) { //success
				console.log("Successfully muted group");
				alert("Successfully muted group");
			},
			function(result) { //fail
				alert("Failed to mute group");
			});
	});

	$("#groupSettingsModalUnmuteGroup").click(function() {
		var data = {};
		data["cookie"] = cookie;
		data["groupid"] = activeGroupID;
		data["mute"] = "false";
		data = JSON.stringify(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/mute", data,
			function(result) { //success
				console.log("Successfully unmuted group");
				alert("Successfully unmuted group");
			},
			function(result) { //fail
				alert("Failed to unmuted group");
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

	//EVENTS
	$("#createEventModalConfirmButton").click(function() {
		var name = $("#createEventModalName").val();
		var info = $("#createEventModalInformation").val();
		var date = $("#createEventModalDate").val();
		var time = $("#createEventModalTime").val();

		//name must not be empty
		if(name == "") {
			$("#createEventModalName").addClass("is-invalid");
			return;
		}
		else {
			$("#createEventModalName").removeClass("is-invalid");
		}

		var dateArr = date.split("/");

		//date must have day, month and year
		if(dateArr.length != 3) {
			$("#createEventModalDate").addClass("is-invalid");
			return;
		}

		var year = parseInt(dateArr[2]);
		if(isNaN(year) || year < 1950 || year > 2500) {
			$("#createEventModalDate").addClass("is-invalid");
			return;
		}

		var month = parseInt(dateArr[0]);
		if(isNaN(month) || month < 1 || month > 12) {
			$("#createEventModalDate").addClass("is-invalid");
			return;
		}

		var day = parseInt(dateArr[1]);
		var endDay = new Date(year, month, 0).getDate();
		if(isNaN(day) || day < 1 || day > endDay) {
			$("#createEventModalDate").addClass("is-invalid");
			return;
		}

		$("#createEventModalDate").removeClass("is-invalid");

		//must have time and am/pm
		var timeArr = time.split(" ");
		if(timeArr.length != 2) {
			$("#createEventModalTime").addClass("is-invalid");
			return;
		}

		var meridiem = timeArr[1];
		if(meridiem != "am" && meridiem != "pm") {
			$("#createEventModalTime").addClass("is-invalid");
			return;
		}

		//must have hours and minutes
		var digits = timeArr[0].split(":");
		if(digits.length != 2) {
			$("#createEventModalTime").addClass("is-invalid");
			return;
		}

		var hours = parseInt(digits[0]);
		if(isNaN(hours) || hours < 1 || hours > 12) {
			$("#createEventModalTime").addClass("is-invalid");
			return;
		}

		var minutes = parseInt(digits[1]);
		if(isNaN(minutes) || minutes < 0 || minutes > 59) {
			$("#createEventModalTime").addClass("is-invalid");
			return;
		}

		$("#createEventModalTime").removeClass("is-invalid");

		var data = {}
		data["cookie"] = cookie;
		data["name"] = name;
		data["description"] = info;
		data["type"] = "group.event";
		data["date"] = new Date(year, month - 1, day, hours, minutes).toUTCString();
		data["groupid"] = activeGroupID;
		data = JSON.stringify(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/calendar/add", data,
			function(result) { //success
				console.log("Successfully created event");
				updateCalendar(currentYear, currentMonth, activeGroupID);
				$("#createEventModal").modal("hide");
			},
			function(result) { //fail
				alert("Failed to create event");
			});
	});
    
   //getting messages
	var messages = [];

	var updateChat = function() {
		//call endpoint
		var data = {};
		data["cookie"] = cookie;
		data["groupID"] = activeGroupID;

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/getChat", JSON.stringify(data),
			function(result) { //success
				//console.log("Successfully retrieved chat messages");

				//parse messages
				var json = JSON.parse(result);

				//if there are no messages in the array they should all be put in there
				if(json.lenth == 0) {
					messages = json["chat"];
				}
				else {
					var newMessages = [];
					//update newMessages with the new messages
					//update messages with the new messages

					//console.log($("#group" + activeGroupID + "Content .chatBox"));
					$("#chatbox_" + activeGroupID).empty();
					for(var i = 0; i < json.length; i++) {
						//update the chat box for the group 
						$("#group" + activeGroupID + "Content .chatBox").append("<p>" + json[i][1] + "[" + json[i][2] + "]" + ": " + json[i][0] + "\n" + "</p>");
					}
				}
				
			},
			function(result) { //fail
				//alert("Failed to retrieve chat messages");
				console.log("Failed to retrieve chat messages");
			});
	}

	//update chat every 3 seconds
	setInterval(updateChat, 3000);

});

	