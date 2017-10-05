$(document).ready(function(){
	//variables
	var numOfGroups = 1;

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

	var ajax = function(method, url, data, onSuccess, onFail) {
		$.ajax({
			method: method,
			url: url,
			data: data,
			datatype: "jsonp"
		}).done(onSuccess(result)).fail(onFail(result));
	};

	//SETTINGS MODAL
	$("#accountSettingsButton").click(function() {
		//populate the account settings modal fields
		var fullName;
		var username;
		var email;
		var phoneNumber;
		var changePassword;
		var confirmPassword;
		var picURL;

		$("#settingsModalFullNameField").val(fullName);
		$("#settingsModalUsernameField").val(username);
		$("#settingsModalEmailField").val(email);
		$("#settingsModalPhoneNumberField").val(phoneNumber);
		$("#settingsModalChangePasswordField").val(changePassword);
		$("#settingsModalConfirmPasswordField").val(confirmPassword);
		$("#settingsModalPicture").attr("src", picURL);

	});
	$("#accountSettingsModalSaveButton").click(function() {
		
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

		cookie = document.cookie;
		console.log(cookie);

		ajax("GET", "http://scheduleit.duckdns.org/api/user/getsettings", 
			JSON.stringify({cookie: cookie}),
			function(result) {
				console.log("success");
				console.log(result);
			},
			function(result) {
				console.log("failed");
				console.log(result);
			});

	});
	$("#groupSettingsSaveButton").click(function() {
		//Write the changed values to the database


		$("#groupSettingsModal").modal("hide");
	});


	var createGroup = function(name, info, pic) {
		//Call the create group endpoint with parameters


		var id = "group" + ++numOfGroups + "Content";

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