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
	
			//Calendar
			$(".datepicker").datepicker({
				inline: true,
				firstDay: 1,
				showOtherMonths: true,
				dayNamesMin: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
			});
	
			//Send message buttons
			$('[id^="sendMessage_"]').click(function() {
				var buttonId = this.id.toString();
				//var groupId = buttonId.replace('sendMessage_', '').toString(); USE KYLES METHOD
				var groupId = 1;
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
					},
					function(result) { //fail
						console.log(data);
						console.log(result);
						alert("Failed to obtain user account settings");
				});
				
			});
		};
	
		assignFunctionality();
	
		var accessServer = function(method, url, data, onSuccess, onFail) {
	
			//get notifications sub
			if(url == "get-notifications-stub") {
				var r = `{
					"notifications": [
						{
							"type": "friend-request",
							"data": {
								"fullname": "[fullname]",
								"username": "[username]",
								"picture": "[url]"
							}
						},
						{
							"type": "group-invite",
							"data": {
								"name": "[name]",
								"id": "[id]",
								"picture": "[url]"
							}
						}
					]
					}`;
	
				onSuccess(r);
				return;
			}
	
			//send friend request stub
			if(url == "send-friend-request") {
				onFail();
				return;
			}
	
			//join group stub
			if(url == "https://scheduleit.duckdns.org/api/user/groups/join") {
				onFail();
				return;
			}
	
	
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
		var notificationResponseComplete = function(event) {
			//remove the notification from the menu
			$(event.target).parent().parent().parent().remove();
			//decrement the badge
			$("#notificationsBadge").text(parseInt($("#notificationsBadge").text()) - 1);
		}
	
		var assignNotificationFunctionality = function() {
			$(".friendRequestAcceptButton").off();
			$(".friendRequestDeclineButton").off();
			$(".groupInviteAcceptButton").off();
			$(".groupInviteDeclineButton").off();
	
			$(".friendRequestAcceptButton").click(function(event) {
				var data = {};
				data["cookie"] = cookie;
				data["username"] = $(event.target).parent().attr("username");
				data = JSON.stringify(data);
	
				accessServer("POST", "https://scheduleit.duckdns.org/api/user/friends/add", data,
					function(result) { //success
						console.log("Successfully added friend");
	
						updateFriends();
	
						notificationResponseComplete();
					},
					function(result) { //fail
						alert("Failed to add friend");
					});
			});
	
			$(".friendRequestDeclineButton").click(function(event) {
				notificationResponseComplete(event);
			});
	
			$(".groupInviteAcceptButton").click(function(event) {
				var data = {};
				data["cookie"] = cookie;
				data["id"] = $(event.target).parent().attr("groupID");
				data = JSON.stringify(data);
	
				accessServer("POST", "https://scheduleit.duckdns.org/api/user/groups/join", data,
					function(result) { //success
						notificationResponseComplete();
					},
					function(result) { //fail
						alert("Failed to join group");
					});
			});
	
			$(".groupInviteDeclineButton").click(function(event) {
				notificationResponseComplete(event);
			});
		};
	
		var updateNotifications = function() {
			var data = {};
			data["cookie"] = cookie;
			data = JSON.stringify(data);
	
			accessServer("POST", "get-notifications-stub", data,
				function(result) { //success
					console.log("Successfully obtained notifications");
	
					$("#notificationMenu").empty();
	
					var json = JSON.parse(result);
	
					$("#notificationsBadge").text(json["notifications"].length);
	
					for(var i = 0; i < json["notifications"].length; i++) {
						var notification = json["notifications"][i];
	
						if(notification["type"] == "friend-request") {
							var fullName = notification["data"]["fullname"];
							var username = notification["data"]["username"];
							var picture = notification["data"]["picture"];
	
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
										<div class="float-right" username="` + username + `">
											<button type="button" class="btn btn-primary btn-sm friendRequestAcceptButton">Accept</button>
											<button type="button" class="btn btn-danger btn-sm friendRequestDeclineButton">Decline</button>
										</div>
									</div>
								</div>
								`;
	
							$("#notificationMenu").append(html);
							//might need to assign the functionality of the accept/decline buttons
						}
						else if(notification["type"] == "group-invite") {
							var name = notification["data"]["name"];
							var id = notification["data"]["id"];
							var picture = notification["data"]["picture"];
	
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
										<div class="float-right" groupID="` + id + `">
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
					alert("Failed to obtain notifications");
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
		$("#addNewGroupButton").click(function() {
			$("#groupFriendsList").empty();
			$("body").off("click", "#groupFriendsList img");
	
			var data = {};
			data["cookie"] = cookie;
			data = JSON.stringify(data);
	
			accessServer("POST", "https://scheduleit.duckdns.org/api/user/friends/get", data,
				function(result) { //success
					console.log("Successfully retrieved friends");
	
					var json = JSON.parse(result);
	
					for(var i = 0; i < json.friends.length; i++) {
						var friendHTML = '<li class="list-group-item"><img class="float-right" src="resources/plus.png" width="18px" />' + json.friends[i] + '</li>';
						$("#groupFriendsList").append(friendHTML);
					}
				},
				function(result) { //fail
					alert("Failed to retrieved friends");
				});
	
			$("body").on("click", "#groupFriendsList img", function() {
				//call endpoint to invite user to group
	
				console.log("clicked");
	
			});
		});
	
		$("#newGroupModalCreateButton").click(function() {
			var nameField = $("#newGroupModalName");
			var infoField = $("#newGroupModalInfo");
	
			var name = nameField.val();
			var info = infoField.val();
	
			if(name == "") {
				nameField.addClass("is-invalid");
			}
			else {
				createGroup(name, info, "pic");
				assignFunctionality();
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
	
		$("#groupSettingsButton").click(function() {
			//populate the group settings modal with group information
	
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
	
		var createGroup = function(name, info, pic) {
			var data = {};
			data["cookie"] = cookie;
			data["name"] = name;
			data["info"] = info;
			data = JSON.stringify(data);
	
			/*accessServer("POST", "...", data,
				function(result) { //success
					console.log("Successfully created group");
				},
				function(result) { //fail
					alert("Failed to create group");
				});*/
	
	
			var id = "group" + ++$("#vPillsContent").children().length + "Content";
	
			var tabHTML = `<a class="nav-link" data-toggle="pill" href="#` + id + `" role="tab">` + name + `</a>`;
			
			var contentHTML = `
				<!-- Group -->
				<div class="tab-pane fade" id="` + id + `" role="tabpanel">
					<div class="collapse show" id="` + id + "Collapse" + `">
						<div class="card card-group">
							<div class="card-body">
								<img src="resources/groupDefaultPhoto.jpg" alt="Default Group Photo" class="img-thumbnail" width="100">
								<h3>` + name + `</h3>
								<p>` + info + `</p>
								<button type="button" class="btn btn-secondary btn-sm invisible" id="groupSettingsButton" data-toggle="modal" data-target="#groupSettingsModal">Group settings</button>
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
							<div class="datepicker"></div>
						</div>
					</div>
				</div>`;
	
			$("#vPillsContent").append(contentHTML);
			$("#vPillsTab").append(tabHTML);
		};
	
		//FRIENDS --------------------------------
		var updateFriends = function() {
			$("#friendsList").empty();
			$("body").off("click", "#friendsList img");
	
			var data = {};
			data["cookie"] = cookie;
			data = JSON.stringify(data);
	
			accessServer("POST", "https://scheduleit.duckdns.org/api/user/friends/get", data,
				function(result) { //success
					console.log("Successfully retrieved friends");
	
					var json = JSON.parse(result);
	
					for(var i = 0; i < json.friends.length; i++) {
						var friendHTML = '<li class="list-group-item"><img class="float-right" src="resources/remove.png" width="18px" />' + json.friends[i] + '</li>';
						$("#friendsList").append(friendHTML);
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
			data["type"] = "friend-request";
			data["cookie"] = cookie;
			data["data"] = {};
			data["data"]["username"] = username;
			data = JSON.stringify(data);
	
			accessServer("POST", "send-friend-request", data,
					function(result) { //success
						console.log("Successfully sent friend request");
						alert("Successfully sent friend request");
						$("#sendFriendRequestTextbox").empty();
					},
					function(result) { //fail
						alert("Failed to send friend request");
					});
		});
	});
	