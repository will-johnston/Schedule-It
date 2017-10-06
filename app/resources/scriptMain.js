$(document).ready(function(){
	//variables
	var cookie = -1035393116;

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
			}
			else {
				$(this).find("img").attr("src","resources/chevronUp.png");
			}
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
		var data = JSON.stringify({cookie: cookie});

		accessServer("POST", "http://scheduleit.duckdns.org/api/user/getsettings", data,
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
				alert("Failed to obtain user account settings");
				console.log(result);
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

		accessServer("POST", "http://scheduleit.duckdns.org/api/user/edit", JSON.stringify(data),
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


		var id = "group" + ++$("vPillsContent").childre().length + "Content";

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
					<div class="tab-pane show" id="` + id + "Chat" + `" role="tabpanel">chat...</div>
					<div class="tab-pane" id="` + id + "Cal" + `" role="tabpanel">cal...</div>
				</div>
			</div>`;

		$("#vPillsContent").append(contentHTML);
		$("#vPillsTab").append(tabHTML);
	};
});