$(document).ready(function(){
	/**
	 * Login button function
	 */
	$("#login").click(function() {
		//Get username and password
		userName = document.getElementById("loginUsername").value;
		passWord = document.getElementById("loginPassword").value;

		//Check for blank or null
		if(userName == "" || userName == null || passWord == "" || passWord == null) {
			document.getElementById("loginError").innerHTML = "The input combination didn't match.";
			$("#loginError").removeClass("invisible");	
			document.getElementById("loginUsername").value = "";
			document.getElementById("loginPassword").value = "";
		} else { //Send request to server
			//Send request to server
			var request = new XMLHttpRequest();
			request.addEventListener("load", function () {
				var recieved = this.responseText;
				var json = JSON.parse(recieved);
				if(request.status === 200) { //200 status = success
					window.location.href = "http://scheduleit.duckdns.org/main.html";					
				} else { //invalid loging credentials
					document.getElementById("loginError").innerHTML = "The input combination didn't match.";
					$("#loginError").removeClass("invisible");	
					document.getElementById("loginUsername").value = "";
					document.getElementById("loginPassword").value = "";
				}
			});
			request.open("POST", "http://scheduleit.duckdns.org/api/user/login");
			request.send(JSON.stringify({ "name": userName, "pass": passWord }));
		}
	});

	/**
	 * Register account button function
	 */
	$("#create").click(function() {
		var s1 = new String($("#newPassword").val()).trim();
		var s2 = new String($("#verifyPassword").val()).trim();
		if(!(s1 === s2)) { //Passwords don't match, stop
			$("#passwordError").removeClass("invisible");
			document.getElementById("passwordError").value = "Passwords do not match.";
			$("#verifyPassword").addClass("is-invalid");
			document.getElementById("verifyPassword").value = "";
		} else { //Passwords match, continue
			var fullName = document.getElementById("fullName").value;
			var userName = document.getElementById("userName").value;
			var email = document.getElementById("email").value;
			var phoneNumber = document.getElementById("phoneNumber").value;
			var password = document.getElementById("newPassword").value;

			if(fullName == "" || fullName == null || userName == "" || userName == null || email == "" 
			|| email == null || password == "" || password == null) { //Input field is either blank or null, stop
				window.alert("Some required fields are missing.");
			} else { //Input fields are valid, continue
				var request = new XMLHttpRequest();
				request.addEventListener("load", function () {
					var recieved = this.responseText;
					if(request.status == 200) { //Valid registration, continue
						window.location.href = "http://scheduleit.duckdns.org/pictureUpload.html";							
					} else { //Invalid registration, stop
						window.alert("Failed to create account.");
						document.getElementById("userName").value = "";
					}
				});
				request.open("POST", "http://scheduleit.duckdns.org/api/user/create");
				request.send(JSON.stringify({"username" : userName,"pass" : password,
				"phone" : phoneNumber,"fullname" : fullName,"email" : email}));
			}
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
		var userName = document.getElementById("userName").value;
		var newPassword = document.getElementById("newPassword").value;
		let val = passwordStrength(userName, newPassword);
		$("#progressBar").removeClass("noProgressBar");
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
			$("#progressBar").addClass("noProgressBar");
		}
	});

	/**
	 * Skip photo upload function, use default (when creating account)
	 */
	$("#skipPhoto").click(function() {
		//TODO: assign default photo to user in DB
		window.location.href = "http://scheduleit.duckdns.org/main.html";		
	});

	/**
	 * Upload selected photo from user's computer (when creating account)
	 */
	$("#uploadPhoto").click(function() {
		//TODO: get filename from choose file button
		//TODO: upload file to DB
		window.location.href = "http://scheduleit.duckdns.org/main.html";		
	});

	/**
	 * Logout button function
	 */
	$("#logoutButton").click(function() {
		//TODO: not sure what to do when you log out other than redirect
		window.location.href = "http://scheduleit.duckdns.org/index.html";		
	});
});