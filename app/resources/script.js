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
	
	/**
	 * Login button function
	 */
	$("#login").click(function() {
		if(true) { //Verify username is in DB
			if(true) { //Verify password matches username in DB
				window.location.href = "http://scheduleit.duckdns.org/programMain.html";
			}
			//Password doesn't match username
			else {
				document.getElementById("loginError").innerHTML = "The input combination didn't match.";
				$("#loginError").removeClass("invisible");				
				$("#loginError").removeClass("invisible");	
				document.getElementById("loginUsername").value = "";
				document.getElementById("loginPassword").value = "";
			}
		//Username isn't found in DB
		} else {
			document.getElementById("loginError").innerHTML = "The input username doesn't exist.";
			$("#loginError").removeClass("invisible");	
			document.getElementById("loginUsername").value = "";
			document.getElementById("loginPassword").value = "";
		}
	});

	/**
	 * Register account button function
	 */
	$("#create").click(function() {
		//TODO: check to ensure everything but phone number is not empty
		if(true) { //If username isnt in DB
			//TODO: Change true to check if username exists in DB, if not then check passwords
			var s1 = new String($("#newPassword").val()).trim();
			var s2 = new String($("#verifyPassword").val()).trim();
			if(!(s1 === s2)) { //Username isn't in DB && passwords don't match
				$("#passwordError").removeClass("invisible");
				$("#verifyPassword").addClass("is-invalid");
				document.getElementById("verifyPassword").value = "";
			} else { //Username isn't in DB && passwords match
				//TODO: Send information to DB for user
				window.location.href = "http://scheduleit.duckdns.org/pictureUpload.html";
			}
		} else { //If username is in DB
			$("#usernameError").removeClass("invisible");
			$("#displayname").addClass("is-invalid");
			document.getElementById("newPassword").value = "";
			document.getElementById("verifyPassword").value = "";
			$("#progressBar").attr("style", "width: 0%");
			$("#progressBar").attr("aria-valuenow", 0);
			$("#invisibility").addClass("invisible");		
		}
	});

	/**
	 * Regex for getting password strength
	 * @param {*} userName 
	 * @param {*} password 
	 */
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

	/**
	 * Password verification error message updater
	 */
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

	/**
	 * Skip photo upload function, use default (when creating account)
	 */
	$("#skipPhoto").click(function() {
		//TODO: assign default photo to user in DB
		window.location.href = "http://scheduleit.duckdns.org/programMain.html";		
	});

	/**
	 * Upload selected photo from user's computer (when creating account)
	 */
	$("#uploadPhoto").click(function() {
		//TODO: get filename from choose file button
		//TODO: upload file to DB
		window.location.href = "http://scheduleit.duckdns.org/programMain.html";		
	});

	/**
	 * Logout button function
	 */
	$("#logoutButton").click(function() {
		//TODO: not sure what to do when you log out other than redirect
		window.location.href = "http://scheduleit.duckdns.org/createOrLogin.html";		
	});
});