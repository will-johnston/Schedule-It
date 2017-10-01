$(document).ready(function(){
	$("#v-pills-tab a").on("shown.bs.tab", function(event) {
		//Fix previous pill stuff
		prevPill = event.relatedTarget.getAttribute("href");;
		$(prevPill + " .nav a").removeClass("active");
		$(prevPill + " .tab-content .tab-pane").removeClass("active show");

		//Fix current pill stuff
		var currPill = event.target.getAttribute("href");
		$(currPill + " .nav a:first").tab("show");
	});

	$(".chevron").click(function() {
		if(!$(this).hasClass("collapsed")) {
			$(this).find("img").attr("src","resources/chevron-down.png");
		}
		else {
			$(this).find("img").attr("src","resources/chevron-up.png");
		}

	});

	//Function to verify display name and check if both passwords match
	$("#create").click(function() {
		//Check username first in if and place password check in the else
			//Clear both password textboxes
			//Clear red text and red border classes
		var s1 = new String($("#newPassword").val()).trim();
		var s2 = new String($("#verifyPassword").val()).trim();
		if(!(s1 === s2))
		{
			$("#passwordError").removeClass("invisible");
			$("#verifyPassword").addClass("is-invalid");
		}

	});

	function passwordStrength(userName, password) {
		let strength = password.length;
		var regex = /[a-z]/;
		if(regex.test(password)) {
			strength += 3;
		}
		regex = /[A-Z]/
		if(regex.test(password)) {
			strength += 3;
		}
		regex = /[0-9]/
		if(regex.test(password)) {
			strength += 3;
		}
		regex = /[!"#$%&')(*+,-./:;<=>?@^_`}{}|~]/
		if(regex.test(password)) {
			strength += 3;
		}
		if(password.indexOf(userName) !== -1 && userName !== '') {
			if(strength >= 15) {
				strength -= 15;
			} else {
				strength = 0;
			}
		}
		regex = /([a-z])\3+/
		if(regex.test(password)) {
			if(strength >= 2) {
				strength -= 2;				
			} else {
				strength = 0;
			}
		}
		regex = /([A-Z])\2+/
		if(regex.test(password)) {
			if(strength >= 2) {
				strength -= 2;				
			} else {
				strength = 0;
			}
		}
		regex = /([0-9])\2+/
		if(regex.test(password)) {
			if(strength >= 2) {
				strength -= 2;				
			} else {
				strength = 0;
			}
		}
		return strength;
	}

	//Function for password strength progress bar
	$('#newPassword').on('input', function() {
		let val = passwordStrength(($("#displayname").val().trim()),($("#newPassword").val()).trim());
		console.log(val);
		//Red:bg-danger, yellow:bg-warning, blue:bg-info, green:bg-success
		$("#invisibility").removeClass("invisible");
		if(val >= 0 && val <=11) {
			$("#progressBar").removeClass("bg-success");
			$("#progressBar").removeClass("bg-info");
			$("#progressBar").removeClass("bg-warning");
			$("#progressBar").removeClass("bg-danger");
			$("#progressBar").addClass("bg-danger");
		} else if(val >= 12 && val <= 22) {
			$("#progressBar").removeClass("bg-success");
			$("#progressBar").removeClass("bg-info");
			$("#progressBar").removeClass("bg-warning");
			$("#progressBar").removeClass("bg-danger");
			$("#progressBar").addClass("bg-warning");
		} else if(val >= 23 && val <= 33) {
			$("#progressBar").removeClass("bg-success");
			$("#progressBar").removeClass("bg-info");
			$("#progressBar").removeClass("bg-warning");
			$("#progressBar").removeClass("bg-danger");
			$("#progressBar").addClass("bg-info");
		} else if(val >= 34 && val <= 44) {
			$("#progressBar").removeClass("bg-success");
			$("#progressBar").removeClass("bg-info");
			$("#progressBar").removeClass("bg-warning");
			$("#progressBar").removeClass("bg-danger");
			$("#progressBar").addClass("bg-success");
		}
		$("#progressBar").attr("style", "width: " + ((val/44) * 100) + "%");
		$("#progressBar").attr("aria-valuenow", val);
		if(($("#newPassword").val()).trim() === "") {
			$("#invisibility").addClass("invisible");
		}
	});
});