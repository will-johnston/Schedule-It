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

		$(".datepicker").datepicker({
			inline: true,
			firstDay: 1,
			showOtherMonths: true,
			dayNamesMin: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']
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
								<input name="usermsg" type="text" id="` + id + "_message" + `" style="width: 53em; border:1px solid rgb(220, 220, 220)">
								<button type="button" class="btn btn-primary" id="` + id + "_sendMessage" + `"  style="width: 5em; margin-right:2.5em; margin-left: 0.5em">Send</button>
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
			console.log("clicked");
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
		var otherUsername = $("#sendFriendRequestTextbox").val();

		var data = {};
		data["cookie"] = cookie;
		data["username"] = otherUsername;
		data = JSON.stringify(data);

		accessServer("POST", "https://scheduleit.duckdns.org/api/user/friends/add", data,
			function(result) { //success
				console.log("Successfully added friend");

				$("#sendFriendRequestTextbox").val("");
				updateFriends();
			},
			function(result) { //fail
				alert("Failed to add friend");
			});
	});
});
