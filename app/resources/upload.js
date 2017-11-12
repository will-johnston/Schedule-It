var name, size, type, chunks, uid, upload_type = "image";
var url = "https://scheduleit.duckdns.org/api/upload", url2 = "https://scheduleit.duckdns.org/api/upload/chunk", url3 = "https://scheduleit.duckdns.org/api/upload/info";
var start = null;
var success = null;
var path = null;
function getChunks(buffer) {
	var arr = new Array();
	//var CHUNK_SIZE = 8192;  //8KB
	//var CHUNK_SIZE = 5120;  //5KB to account for base64
	var CHUNK_SIZE = 18432;  //18KB to account for base64 (frame size is 20KB)
	var iterations = Math.ceil(buffer.length / CHUNK_SIZE);
	//console.log("Rounder: " + (buffer.length / CHUNK_SIZE) + " to " + (Math.ceil(buffer.length / CHUNK_SIZE)));
	//console.log("Iterations: " + iterations);
	var totalSize = buffer.length; 
	for (var i = 0; i < iterations - 1; i++) {
		//console.log("Pushing from " + CHUNK_SIZE * i + " to " + CHUNK_SIZE * (i + 1));
		arr.push(buffer.slice(CHUNK_SIZE * i, CHUNK_SIZE * (i + 1)));
	}
	arr.push(buffer.slice(CHUNK_SIZE * (iterations - 1), totalSize));
	//console.log("Pusing from " + CHUNK_SIZE * (iterations - 1) + " to " + totalSize);'
	console.log("Chunk count:", iterations, "with size:", CHUNK_SIZE);
	return arr;
}
//Generates a 16-bit checksum
function checksum(array) {
	var MODULUS = 65535;
	var sum = 0;
	for (var i in array) {
		sum = (sum + i) % MODULUS;
	}
	return sum;
}
//Don't use this
function test() {
	console.log("Called test");
	var fileElement = document.getElementById("filePicker");
	if (fileElement.value == null) {
		console.log("File Element is null");
		return;
	}
	var files = fileElement.files;
	var file = files[0];
	console.log("File name: ", file.name, "Size: ", file.size, "mime: ", file.type);
	var reader = new FileReader();
	//for readAsString()
	/*reader.onload = (function(theFile) {
		console.log("Called reader.onload");
		var resArea = document.getElementById("rasult");
		console.log("Outputting file");
		resArea.value = theFile.target.result;
		console.log(theFile);
	});*/
	reader.onload = (function(theFile) {
		//var arr = theFile.target.result;      //This only returns the ArrayBugger object (not an array)
		var buffer = theFile.target.result;
		var newBuf = new Uint8Array(buffer);
		console.log(theFile.target.result);
		console.log(theFile.target.result.byteLength);
		console.log(newBuf);
		console.log("newbuf length: ", newBuf.length);
		var chunks = getChunks(newBuf);
		console.log("chunks[0] checksum: " + checksum(chunks[0]));
	});
	//reader.readAsText(file);  //works great
	//reader.readAsBinaryString(file);
	reader.readAsArrayBuffer(file);
}
//Starts the upload process
function requestUpload(cookie, success) {
	console.log("requesting upload");
	// {{"type": "image/jpg",
	//   "size": 65535,
	//   "length": 78,
	//   "cookie": -892866160
	//}
	var request = new XMLHttpRequest();
	request.addEventListener("load", function () {
		console.log(this.responseText);
		//handle response, if success start the upload loop;
		var parsed = JSON.parse(this.responseText);
		/*for (var key in parsed) {
			if (key == "error") {
				console.log("Error:", parsed[key]);
				break;
			}
		}*/
		if (this.status == 200) {
			//OK
			uid = parsed.uploadid;
			console.log("Recieved id:", uid);
			success();
		}
		else {
			//failed
			console.log("Error:", parsed.uploadid);
		}
		console.log(parsed);
	});
	//request.open("POST", "scheduleit.duckdns.org/api/upload");
	request.open("POST", url);
	//console.log("Sending request:", JSON.stringify({ mimeType : type, size: size, length: chunks.length, cookie: cookie, uploadType: upload_type}));
	request.send(JSON.stringify({ mimeType : type, size: size, length: chunks.length, cookie: cookie, uploadType: upload_type}));
	console.log("sent upload request");
}
function toBase64(chunk) {
	if (chunk == null) {
		return null;
	}
	try {
		var decoder = new TextDecoder('utf-8');
		//console.log(btoa(String.fromCharCode.apply(null, chunk)));
		return btoa(String.fromCharCode.apply(null, chunk));
	}
	catch (e) {
		console.log("Caught exception:", e.message);
		return null;
	}
}
function uploadchunk(chunk, cookie, cid, callback) {
	var sum = checksum(chunk);
	var len = chunk.length;
	console.log("Uploading chunk with id:", cid, "size:", len, "and checksum:", sum);
	var request = new XMLHttpRequest();
	request.addEventListener("load", function () {
		//console.log(this.responseText);
		var parsed = JSON.parse(this.responseText);
		if (this.status == 200) {
			//OK
			console.log("200success:", parsed.success);
			if (callback != null) {
				callback();
			}
		}
		else {
			//failed
			console.log("ERRORsuccess:", parsed.success);
		}
		console.log(parsed);
	});
	var message = {
		checksum : sum,
		cookie : cookie,
		uploadid : uid,
		length : len,
		chunkid : cid,
		data : toBase64(chunk)
	};
	var json = JSON.stringify(message);
	var message = json;
	//request.open("POST", "scheduleit.duckdns.org/api/upload");
	request.open("POST", url2);
	//console.log("Sending request:", message);
	request.send(message);
	//console.log("sent request");
}
//Uploads all chunks from array
//Calls passCallback(path)
//Calls failCallback(null)
function uploadLoop(cookie, passCallback, failCallback) {
	//console.log("length:", chunks.length);
	for (var i = 0; i < chunks.length; i++) {
		var chunk = chunks[i];
		if (i == (chunks.length - 1)) {
			//console.log("uploading last chunk");
			//last one
			uploadchunk(chunk, cookie, i, function () {
			   //get image info and set uploadedImg to new image
				// /upload/info
				var xhr = new XMLHttpRequest();
				xhr.addEventListener("load", function() {
					if (this.status == 200) {
						//Successful request
						var json = JSON.parse(this.responseText);
						success = json.success;
						path = json.path;
						console.log("Success",success,"path",path);
						//setTestImage(path);
						//var end = new Date();
						//alert('It took ' + (end - start) + ' ms.');
						if (passCallback != null) {
							passCallback(path);
						}
					}
					else if (this.status == 400) {
						//API error
						var json = JSON.parse(this.responseText);
						alert("failed to upload");
						console.log("error", json.error);
						if (failCallback != null) {
							failCallback();
						}
					}
					else {
						console.log("statusCode", this.status);
						alert("super failed to upload");
					}
				});
				xhr.open("POST", url3);
				//console.log("Sending request to", url3);
				//console.log({cookie : cookie, uploadid : uid});
				xhr.send(JSON.stringify({cookie : cookie, uploadid : uid}));
			});
		}
		else {
			//upload like normal
			//console.log("uploading normal chunk");
			uploadchunk(chunk, cookie, i, null);
		}
	}
}
//Calls success(path)
//Calls fail(null)
function upload(fileElement,cookie,success, fail) {
	start = new Date();
	console.log("Called upload");
	//var fileElement = document.getElementById(filePickerID);
	if (fileElement == null) {
		consol.log("Coudln't get element");
		return;
	}
	if (fileElement.value == null) {
		console.log("File Element is null");
		return;
	}
	var files = fileElement.files;
	var file = files[0];
	//console.log("File name: ", file.name, "Size: ", file.size, "mime: ", file.type);
	var reader = new FileReader();
	name = file.name;
	size = file.size;
	type = file.type;
	reader.onload = (function(theFile) {
		//var arr = theFile.target.result;      //This only returns the ArrayBuffer object (not an array)
		console.log("Name:", name, "size:", size, "type:", type);
		var buffer = theFile.target.result;
		var newBuf = new Uint8Array(buffer);
		//console.log(theFile.target.result);
		//console.log(theFile.target.result.byteLength);
		console.log(newBuf);
		console.log("newbuf length: ", newBuf.length);
		chunks = getChunks(newBuf);
		//console.log("chunks[0] checksum: " + checksum(chunks[0]));
		/*requestUpload(function() {
			uploadchunk(chunks[0], 0, null);
		});*/
		//uploadchunk(newBuf[0], 0, null)
		requestUpload(cookie, function() {
			uploadLoop(cookie, success, fail);
		})
	});
	reader.readAsArrayBuffer(file);
}