$(document).ready(function(){

	var cookie = document.cookie.split("=")[1];
	var activeGroupID;
	var username;

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

	//get username
	var data = {};
	data["cookie"] = cookie;
	data = JSON.stringify(data);

	accessServer("POST", "https://scheduleit.duckdns.org/api/user/getsettings", data,
		function(result) { //success
			console.log("Successfully retrieved user settings");

			username = JSON.parse(result)["username"];
		},
		function(result) { //fail
			console.log("Failed to retrieve user settings");
		});

	$("#settingsModalChooseFileButton").change(function() {
		var button = $("#settingsModalChooseFileButton");
		var dirArr = button.val().split("\\");
		$("#settingsModalFileLabel").html(dirArr[dirArr.length - 1]);
	});

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
			data["response"]["accept"] = "going";
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
			data["response"]["accept"] = "on the fence";
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
			data["response"]["accept"] = "not going";
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
									<p class="card-text">` + fullName + ` would like to add you as a friend</p>
								</div>
								<div class="card-footer">
									<div class="float-right" notifID="` + notifID + `">
										<button type="button" class="btn btn-success btn-sm friendRequestAcceptButton">Accept</button>
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
									<p class="card-text">You have been invited to join ` + name + `</p>
								</div>
								<div class="card-footer">
									<div class="float-right" notifID="` + id + `">
										<button type="button" class="btn btn-success btn-sm groupInviteAcceptButton">Accept</button>
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
									<p class="card-text">An event has been created: ` + name + `</p>
								</div>
								<div class="card-footer">
									<div class="float-right" notifID="` + id + `">
										<button type="button" class="btn btn-success btn-sm groupEventGoingButton">Going</button>
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
	//var image;
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
				email = json.email;
				phoneNumber = json.phone;
				path = json.image;

				$("#settingsModalFullNameField").val(fullName);
				$("#settingsModalUsernameField").val(username);
				$("#settingsModalEmailField").val(email);
				$("#settingsModalPhoneNumberField").val(phoneNumber);
				if(path != "") {
					$("#settingsModalProfilePicture").attr("src", path);
				}
				$("#settingsModalChangePasswordField").val("");
				$("#settingsModalConfirmPasswordField").val("");
				$("#settingsModalChooseFileButton").val("");
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

		data["image"] = path;

		data = JSON.stringify(data);

		console.log(data);

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

	$("#settingsModalUploadButton").click(function() {
		//check that file element has value
		var file = $("#settingsModalChooseFileButton").val();

		if(file == null || file == "") {
			$("#settingsModalChooseFileButton").parent().addClass("is-invalid");
			return;
		}

		$("#settingsModalChooseFileButton").parent().removeClass("is-invalid");

		upload(document.getElementById("settingsModalChooseFileButton"),
			cookie,
			function() {
				$("#settingsModalProfilePicture").attr("src", path);
				console.log("Successfully uploaded profile picture, path: " + path);
			},
			function() {
				console.log("Failed to upload profile picture");
			});
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

					var tabHTML = `<a class="nav-link" data-toggle="pill" href="#` + id + `" role="tab">` + name + `</a>`;
					var contentHTML = `
						<!-- Group -->
						<div class="tab-pane fade" id="` + id + `" role="tabpanel" groupID="` + realID + `" groupName="` + name + `">
							<div class="collapse show" id="` + id + "Collapse" + `">
								<div class="card card-group">
									<div class="card-body">
										<h3>` + name + `</h3>`;

										if(name != "Me") {
											contentHTML += '<br />';
											contentHTML += '<button type="button" class="btn btn-secondary btn-sm groupSettingsButton" style="margin-right: 10px">Group settings</button>';
											contentHTML += '<button type="button" class="btn btn-secondary btn-sm leaveGroupButton">Leave group</button>';
										}
										else {
											contentHTML += "<p>This tab is just for you! Set personal events and see all events you're attending in your calendar.";
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
									<div disabled class="chatBox" id="` + "chatbox_" + realID + `" style="resize:none; border-radius: 0.25em; text-align:left;margin-bottom:1%;background:#fff;height:21em;transition: 0.25s ease-out; width:70vw; border:1px solid rgb(220, 220, 220); overflow-y:auto;white-space: -webkit-pre-wrap;word-break:break-all;white-space:normal;padding:0.75vw"></div>
									
										<form name="message" action="">
											<input name="usermsg" class="chatbotTextField" type="text" id="` + "message_" +  realID + `" style="width: 53em; border:1px solid rgb(220, 220, 220)" maxlength="1000">
											<button type="button" class="btn btn-primary" id="` + "sendMessage_" + realID + `"  style="width: 5em; margin-right: 0.5em; margin-left: 0.5em">Send</button>
											<button type="button" class="btn btn-secondary chatbotButton" id="` + "sendBot_" + realID + `"  style="width: 6em">Chatbot</button>
										</form>
									</div>
								</div>
								<div class="tab-pane" id="` + id + "Cal" + `" role="tabpanel">
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
					$("#groupMembersList").empty();

					//populate the group settings modal with group information
					var parent = $(event.target).parent().parent().parent().parent();

					$(".groupFriendsList").attr("groupID", parent.attr("groupID"));

					$("#groupSettingsModalName").val(parent.attr("groupName"));
					//info...

					//if the user is not an admin, restrict access
					var data = {};
					data["cookie"] = cookie;
					data = JSON.stringify(data);

					accessServer("POST", "https://scheduleit.duckdns.org/api/user/getsettings", data,
						function(result) { //success
							var data = {};
							data["cookie"] = cookie;
							data["groupid"] = activeGroupID;
							data["groupmember"] = JSON.parse(result)["username"];
							data = JSON.stringify(data);

							accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/admin/check", data,
								function(result) { //success
									var json = JSON.parse(result);
									if(json["value"] == "false") {
										console.log("User is not admin of active group");
										alert("You are not an admin of this group");
										return;
									}

									console.log("User is admin of active group");
									$("#groupSettingsModal").modal("show");

									var data = {};
									data["cookie"] = cookie;
									data["groupid"] = activeGroupID;
									data = JSON.stringify(data);

									accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/members", data,
										function(result) { //success
											console.log("Successfully retrieved group members");

											var json = JSON.parse(result);

											for(var i = 0; i < json.length; i++) {
												if(json[i]["username"] == username || json[i]["username"] == "Clarence") {
													continue;
												}

												var memberHTML = '<li class="list-group-item">' + json[i]["username"] + '<img class="float-right removeAdminPermission" src="resources/minus.png" width="18px" /><img class="float-right giveAdminPermission" src="resources/plus.png" width="18px" style="margin-right: 10px"/></li>';
												$("#groupMembersList").append(memberHTML);
											}

											assignGroupMemberListFunctionality();
										},
										function(result) { //fail
											console.log("Failed to retrieve group members");
										});
								},
								function(result) { //fail
									console.log("Failed to check admin permission");
								});
							
						},
						function(result) { //fail
							console.log("Failed to retrieve user settings");
						});
				});

				$(".leaveGroupButton").click(function() {
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

				$(".createNewEventButton").click(function() {
					var data = {};
					data["cookie"] = cookie;
					data["groupid"] = activeGroupID;
					data["groupmember"] = username;
					data = JSON.stringify(data);

					accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/admin/check", data,
						function(result) { //success
							var json = JSON.parse(result);
							if(json["value"] == "false") {
								console.log("User is not admin of active group");
								alert("You are not an admin of this group");
								return;
							}

							console.log("User is admin of active group");
							$("#createEventModal").modal("show");
						},
						function(result) { //fail
							console.log("Failed to retrieve admin permission");
						});
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

	var assignGroupMemberListFunctionality = function() {
		$("#groupMembersList .giveAdminPermission").off();
		$("#groupMembersList .giveAdminPermission").click(function() {
			var data = {};
			data["cookie"] = cookie;
			data["groupmember"] = $(event.target).parent().text();
			data["groupid"] = activeGroupID;
			data = JSON.stringify(data);

			accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/admin/add", data,
				function(result) { //success
					console.log("Successfully gave user admin permissions");
					alert("Successfully gave user admin permissions");
				},
				function(result) { //fail
					console.log("Failed to give user admin permissions");
				});
		});

		$("#groupMembersList .removeAdminPermission").off();
		$("#groupMembersList .removeAdminPermission").click(function() {
			var data = {};
			data["cookie"] = cookie;
			data["groupmember"] = $(event.target).parent().text();
			data["groupid"] = activeGroupID;
			data = JSON.stringify(data);

			accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/admin/remove", data,
				function(result) { //success
					console.log("Successfully revoked user admin permissions");
					alert("Successfully revoked user admin permissions");
				},
				function(result) { //fail
					console.log("Failed to revoke user admin permissions");
				});
		});
	}

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

		var name = nameField.val();

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

		//must specify event type
		var staticEvent;
		if(!$("#createEventModalStaticRadio").prop("checked") && !$("#createEventModalBestFitRadio").prop("checked")) {
			$("#createEventModalStaticRadio").parent().addClass("is-invalid");
			$("#createEventModalBestFitRadio").parent().addClass("is-invalid");
			return;
		}
		else {
			$("#createEventModalStaticRadio").parent().removeClass("is-invalid");
			$("#createEventModalBestFitRadio").parent().removeClass("is-invalid");

			staticEvent = $("#createEventModalStaticRadio").prop("checked") ? true : false;
		}

		//date must have day, month and year
		var dateArr = date.split("/");
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
		data["groupid"] = activeGroupID;

		if(staticEvent) {
			data["type"] = "group.event";
			data["date"] = new Date(year, month - 1, day, hours, minutes).toUTCString();
			data["expiration_time"] = "None";
		}
		else {
			data["type"] = "Generic";
			data["date"] = "None";
			data["expiration_time"] = new Date(year, month - 1, day, hours, minutes).toUTCString();
		}

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

	$("#createEventModalRadioRow").click(function() {
		if($("#createEventModalStaticRadio").prop("checked")) {
			$("#createEventModalDateLabel").html("Date");
			$("#createEventModalTimeLabel").html("Time");
		}
		else if($("#createEventModalBestFitRadio").prop("checked")) {
			$("#createEventModalDateLabel").html("Expiration date");
			$("#createEventModalTimeLabel").html("Expiration time");
		}
	});

	$("#editEventModalConfirmButon").click(function() {
		var eventNameNew = $("#editEventModalName").val();
		var eventInfoNew = $("#editEventModalInfo").val();
		var eventDateNew = $("#editEventModalDate").val();
		var eventTimeNew = $("#editEventModalTime").val();

		var data = {};
		data["cookie"] = cookie;
		data["groupid"] = activeGroupID;
		data["eventid"] = activeEventID;

		if(eventNameNew != eventNameOld) {
			data["name"] = eventNameNew;
		}

		if(eventInfoNew != eventInfoOld) {
			data["description"] = eventInfoNew;
		}

		if(eventDateNew != eventDateOld || eventTimeNew != eventTimeOld) {
			//date must have day, month and year
			var dateArr = eventDateNew.split("/");
			if(dateArr.length != 3) {
				$("#editEventModalDate").addClass("is-invalid");
				return;
			}

			var year = parseInt(dateArr[2]);
			if(isNaN(year) || year < 1950 || year > 2500) {
				$("#editEventModalDate").addClass("is-invalid");
				return;
			}

			var month = parseInt(dateArr[0]);
			if(isNaN(month) || month < 1 || month > 12) {
				$("#editEventModalDate").addClass("is-invalid");
				return;
			}

			var day = parseInt(dateArr[1]);
			var endDay = new Date(year, month, 0).getDate();
			if(isNaN(day) || day < 1 || day > endDay) {
				$("#editEventModalDate").addClass("is-invalid");
				return;
			}

			$("#editEventModalDate").removeClass("is-invalid");

			//must have time and am/pm
			var timeArr = eventTimeNew.split(" ");
			if(timeArr.length != 2) {
				$("#editEventModalTime").addClass("is-invalid");
				return;
			}

			var meridiem = timeArr[1];
			if(meridiem != "am" && meridiem != "pm") {
				$("#editEventModalTime").addClass("is-invalid");
				return;
			}

			//must have hours and minutes
			var digits = timeArr[0].split(":");
			if(digits.length != 2) {
				$("#editEventModalTime").addClass("is-invalid");
				return;
			}

			var hours = parseInt(digits[0]);
			if(isNaN(hours) || hours < 1 || hours > 12) {
				$("#editEventModalTime").addClass("is-invalid");
				return;
			}

			var minutes = parseInt(digits[1]);
			if(isNaN(minutes) || minutes < 0 || minutes > 59) {
				$("#editEventModalTime").addClass("is-invalid");
				return;
			}

			$("#editEventModalTime").removeClass("is-invalid");

			data["date"] = new Date(year, month - 1, day, hours, minutes).toUTCString();
		}

		data = JSON.stringify(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/calendar/edit", data,
			function(result) { //success
				console.log("Successfully edited event");
				updateCalendar(currentYear, currentMonth, activeGroupID);
				$("#editEventModal").modal("hide");
			},
			function(result) { //fail
				alert("Failed to edit event");
			});
	});

	$("#editEventModalRemoveButton").click(function() {
		var data = {};
		data["cookie"] = cookie;
		data["groupid"] = activeGroupID;
		data["eventid"] = activeEventID;
		data = JSON.stringify(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/calendar/remove", data,
			function(result) { //success
				console.log("Successfully removed event");
				updateCalendar(currentYear, currentMonth, activeGroupID);
				$("#editEventModal").modal("hide");
			},
			function(result) { //fail
				alert("Failed to remove event");
			});
	});
  
   //getting messages
	var messages = [];
	var lastID = 0;

	var updateChat = function() {
		//call endpoint
		var data = {};
		data["cookie"] = cookie;
		data["groupID"] = activeGroupID;

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/getChat", JSON.stringify(data),
			function(result) { //success
				var json = JSON.parse(result);
				
				//Apparently this is the only way for it to update when switching between groups tabs.......k
				if(lastID != activeGroupID) {
					$("#chatbox_" + activeGroupID).empty();
					messages = [];
				}
				lastID = activeGroupID;

				//Print only new messages
				if(messages.length != json.length && messages.length != 0) {
					var difference = json.length - messages.length;
					for(var i = json.length - difference; i < json.length; i++) {

						//Image
						var isURL = checkURL(json[i][0]);
						if(isURL == true) {
							var timeStamp = json[i][2].slice(12,19);
							$("#group" + activeGroupID + "Content .chatBox").append("<p>" + "<strong style='color:rgb(0, 123, 255)'>" + json[i][1] + "</strong>" + " " + "<samp style='color:rgb(150,150,150)'>" + "[" + timeStamp + "]" + "</samp>" + ": " + "</p>");
							$("#group" + activeGroupID + "Content .chatBox").append("<img src='" + json[i][0] + "'>");
	
						} else {
						//Plain Text
						var timeStamp = json[i][2].slice(12,19);
							$("#group" + activeGroupID + "Content .chatBox").append("<p>" + "<strong style='color:rgb(0, 123, 255)'>" + json[i][1] + "</strong>" + " " + "<samp style='color:rgb(150,150,150)'>" + "[" + timeStamp + "]" + "</samp>" + ": " + json[i][0] + "\n" + "</p>");
						} 
					}

					//Push messages to array
					for(var i = json.length - difference; i < json.length; i++) {
						messages.push(json[i]);
					}
				} else if(messages.length == 0) {
					for(var i = 0; i < json.length; i++) {
						//Image
						var isURL = checkURL(json[i][0]);
						if(isURL == true) {
							var timeStamp = json[i][2].slice(12,19);
							$("#group" + activeGroupID + "Content .chatBox").append("<p>" + "<strong style='color:rgb(0, 123, 255)'>" + json[i][1] + "</strong>" + " " + "<samp style='color:rgb(150,150,150)'>" + "[" + timeStamp + "]" + "</samp>" + ": " + "</p>");
							$("#group" + activeGroupID + "Content .chatBox").append("<img src='" + json[i][0] + "'>");
	
						} else {
						//Plain Text
						var timeStamp = json[i][2].slice(12,19);
							$("#group" + activeGroupID + "Content .chatBox").append("<p>" + "<strong style='color:rgb(0, 123, 255)'>" + json[i][1] + "</strong>" + " " + "<samp style='color:rgb(150,150,150)'>" + "[" + timeStamp + "]" + "</samp>" + ": " + json[i][0] + "\n" + "</p>");
						} 
					}
					//Push messages to array
					for(var i = 0; i < json.length; i++) {
						messages.push(json[i]);
					}
				}		
				
			},
			function(result) { //fail
				//alert("Failed to retrieve chat messages");
				console.log("Failed to retrieve chat messages");
		});
	}


	function checkURL(url) {
		//END FORMATS: .gif, .jpg, .jpeg, .png
		var length = url.length;
		if(url.indexOf("www") == 0 || url.indexOf("http") == 0 || url.indexOf("https") == 0) {
			if(url.indexOf(".gif") == length - 4 || url.indexOf(".GIF") == length - 4) {
				return true;
			} else if(url.indexOf(".jpg") == length - 4 || url.indexOf(".JPG") == length - 4) {
				return true;
			} else if(url.indexOf(".jpeg") == length - 4 || url.indexOf(".JPEG") == length - 4) {
				return true;
			} else if(url.indexOf(".png") == length - 4 || url.indexOf(".PNG") == length - 4) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	//update chat every 3 seconds
	setInterval(updateChat, 3000);

});

	