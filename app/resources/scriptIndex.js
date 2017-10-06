$(document).ready(function(){
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

	/**
	 * Login button function
	 */
	$("#login").click(function() {
		//Get username and password
		userName = document.getElementById("loginUsername").value;
		passWord = document.getElementById("loginPassword").value;

		/*//Check for blank or null
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
					var json = JSON.parse(recieved);
						document.cookie = "cookie=" + json.cookie;
						window.location.href = "http://scheduleit.duckdns.org/main.html";
				} else { //invalid loging credentials
					document.getElementById("loginError").innerHTML = "The input combination didn't match.";
					$("#loginError").removeClass("invisible");	
					document.getElementById("loginUsername").value = "";
					document.getElementById("loginPassword").value = "";
					console.log(this.responseText);
				}
			});
			request.open("POST", "http://scheduleit.duckdns.org/api/user/login");
			request.send(JSON.stringify({ "name": userName, "pass": passWord }));
		}*/

		var data = {};
		data["name"] = userName;
		data["pass"] = passWord;

		accessServer("POST", "http://scheduleit.duckdns.org/api/user/login", JSON.stringify(data),
			function(result) { //success
				var json = JSON.parse(result);
				document.cookie = "cookie=" + json.cookie;
				window.location.href = "http://scheduleit.duckdns.org/main.html";
			},
			function(result) { //fail
				alert("Failed to log user in");
				console.log(data);
				console.log(result);
			});
	});

	/**
	 * Register account button function
	 */
	$("#create").click(function() {
		/*var s1 = new String($("#newPassword").val()).trim();
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
						var json = JSON.parse(recieved);
						document.cookie = "cookie=" + json.cookie;
						window.location.href = "http://scheduleit.duckdns.org/main.html";
					} else { //Invalid registration, stop
						window.alert("Failed to create account.");
						document.getElementById("userName").value = "";
					}
				});
				request.open("POST", "http://scheduleit.duckdns.org/api/user/create");
				request.send(JSON.stringify({	"email": email,
												"pass": password,
												"name": fullName,
												"phone": phoneNumber,
												"username": userName}));
			}
		}*/

		var data = {};
		data["name"] = $("#fullName").val();
		data["username"] = $("#userName").val();
		data["email"] = $("#email").val();
		data["phone"] = $("#phoneNumber").val();
		data["pass"] = $("#newPassword").val();

		accessServer("POST", "http://scheduleit.duckdns.org/api/user/create", JSON.stringify(data),
			function(result) { //success
				var json = JSON.parse(result);
				document.cookie = "cookie=" + json.cookie;
				window.location.href = "http://scheduleit.duckdns.org/main.html";
			},
			function(result) { //fail
				alert("Failed to create account");
				console.log(data);
				console.log(result);
			});
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
});