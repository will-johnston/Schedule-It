$(document).ready(function(){
	//variables
	var cookie = document.cookie.split("=")[1];

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
			var timeStamp = "[" + a.getHours() + ":" + a.getMinutes() + ":" + a.getSeconds() + "]"; //Timestamp for message

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

	var accessServer = function(method, url, data, onSuccess, onFail) {
		var xhr = new XMLHttpRequest();
		xhr.open(method, url);
		xhr.onload = function () {
			if (xhr.status === 200)
				onSuccess(xhr.response);
			else
				onFail(xhr.response);
		};

		xhr.send(data);
	};

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

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/getsettings", JSON.stringify(data),
			function(result) { //success
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
				console.log(data);
				console.log(result);
				alert("Failed to obtain user account settings");
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

		console.log(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/edit", JSON.stringify(data),
			function(result) { //success
				console.log("Successfully changed user account settings");
				$("#accountSettingsModal").modal("hide");
			},
			function(result) { //fail
				alert("Failed to change user account settings");
				console.log(result);
			});

	});
	$("#accountSettingsModalDeleteAccountButton").click(function() {
		
	});

	assignFunctionality();

	//LOGOUT BUTTON
	$("#logoutButton").click(function() {
		document.cookie = "cookie=";
		window.location.href = "https://scheduleit.duckdns.org/";
	});


	//NEW GROUP MODAL
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

	//GROUP SETTINGS MODAL
	$("#groupSettingsButton").click(function() {
		//populate the group settings modal with group information

	});
	$("#groupSettingsSaveButton").click(function() {
		//Write the changed values to the database


		$("#groupSettingsModal").modal("hide");
	});


	var createGroup = function(name, info, pic) {
		//Call the create group endpoint with parameters


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
							<button type="button" class="btn btn-secondary btn-sm" id="groupSettingsButton" data-toggle="modal" data-target="#groupSettingsModal">Group settings</button>
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

		var data = {};
		data["cookie"] = cookie;

		console.log(JSON.stringify(data));

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/friends/get", JSON.stringify(data),
			function(result) { //success
				console.log(result);

				var json = JSON.parse(result);

				for(var i = 0; i < json.friends.length; i++) {
					var friendHTML = '<button type="button" class="btn btn btn-outline-secondary">' + json.friends[i] + '</button>';
					$("#friendsList").append(friendHTML);
				}
			},
			function(result) { //fail
				console.log(result);
				alert("Failed to get friends");
			});
	};
	updateFriends();

	$("#sendFriendRequestButton").click(function() {
		var otherUsername = $("#sendFriendRequestTextbox").val();

		var data = {};
		data["cookie"] = cookie;
		data["username"] = otherUsername;

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/friends/add", JSON.stringify(data),
			function(result) { //success
				$("#sendFriendRequestTextbox").val("");
				updateFriends();
				alert("Successfully added friend");
			},
			function(result) { //fail
				alert("Failed to add friend");
				console.log(result);
			});
	});
});
