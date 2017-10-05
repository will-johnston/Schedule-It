// null if failure, calls callback with cookie if success
// also stores the cookie
function login(username, password, callback) {
    if (username == null || password == null) {
        console.log("Request login with null parameters");
        return null;
    }
    var request = new XMLHttpRequest();
    request.addEventListener("load", function() {
        //return cookie on success
        if (this.status == 200) {
            var response = JSON.parse(this.responseText);
            var cookie = response.cookie;
            storeLoginCookie(cookie);
            console.log(cookie);
            if (callback != null) {
                callback(cookie);
            }
        }
        else {
            console.log("Failed to log in:", this.responseText);
            return null;
        }
    });
    var payload = JSON.stringify( { name : username, pass : password });
    request.open("POST", "http://scheduleit.duckdns.org/api/user/login");
    request.send(payload);
}
//This should securely hash the password so that the server doesn't have the actual copy
function makePassword(rawPassword) {
    return rawPassword;
}
// null if failure, calls callback with cookie if success
// also stores the cookie
function create(username, password, email, name, phone, callback) {
    if (username == null || password == null || email == null || name == null || phone == null) {
        console.log("One of the parameters is null");
        return null;
    }
    var request = new XMLHttpRequest();
    request.addEventListener("load", function() {
        if (this.status == 200) {
            var response = JSON.parse(this.responseText);
            var cookie = response.cookie;
            storeLoginCookie(cookie);
            if (callback != null) {
                callback(cookie);
            }
        }
        else {
            console.log("Failed to create user:", this.responseText, this.statusText, this.status);
            return null;
        }
    })
    var payload = JSON.stringify({ email : email, pass : password, name : name, phone : phone, username : username});
    request.open("POST", "http://scheduleit.duckdns.org/api/user/create")
    request.send(payload);
}
function storeLoginCookie(cookie) {
    if (cookie == 0) {
        return;
    }
    else {
        var cookietime = new Date();
        cookietime.setFullYear(cookietime.getFullYear() + 1);          //Expire in a year
        var date = cookietime.toUTCString();
        //var cookiestring = "_schedlogin={0}; expires={1};path=/".format(cookie, date);
        var cookiestring = "_schedlogin=" + cookie +"; expires=" + date + ";path=/";
	document.cookie = cookiestring;
    }
}
//returns cookie on success,0 on failure
function getCookie() {
    var cookies = document.cookie;
    var cookiesplit = cookies.split(";")
    for (var i = 0; i < cookiesplit.length; i++) {
        var valuesplit = cookiesplit[i].split("=");
        if (valuesplit.length == 2 && valuesplit[0] == "_schedLogin") {
            return parseInt(valuesplit[1]);
        }
    }
    return 0;
}
