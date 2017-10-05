$(document).ready(function(){
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

		//Group settings function
		$("#groupSettingsButton").click(function() {
			//Show the leave group and delete group buttons in the modal
			$("#groupSettingsDeleteGroupBtn").show();
			$("#groupSettingsLeaveGroupBtn").show();
		});
	}

	//Add new group button function
	$("#addNewGroupButton").click(function() {
		//Don't show the leave group and delete group buttons in the modal
		$("#groupSettingsDeleteGroupBtn").hide();
		$("#groupSettingsLeaveGroupBtn").hide();
	});

	//Create group button in group modal function
	$("#groupSettingsConfirm").click(function() {
		createGroup("Name", "Info", "pic");
		assignFunctionality();
	});


	var numOfGroups = 1;

	var createGroup = function(name, info, pic) {
		//do some backend shit...


		var id = "group" + ++numOfGroups + "Content";

		var tabHTML = `<a class="nav-link" data-toggle="pill" href="#` + id + `" role="tab">` + name + `</a>`;
		
		var contentHTML = `
			<!-- Group -->
			<div class="tab-pane fade" id="` + id + `" role="tabpanel">
				<div class="collapse show" id="` + id + "Collapse" + `">
					<div class="card card-group">
						<div class="card-body">
							<img src="resources/profileDefaultPhoto.png" alt="Default Group Photo" class="img-thumbnail" width="100">
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