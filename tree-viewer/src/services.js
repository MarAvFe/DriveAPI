'use strict';

const apiUrl = 'http://localhost:8080/xDriveRest/api/'

// Services for data retrieval from the API
export default{
	ls: function(email){
		let files = post(apiUrl+'ls', {'email': email});
		console.log(files);
		return files;
	},
	cd: function(email, path){
		 post(apiUrl+'cd', {'email': email, "path":path});
		return;
	},
	touch: function(email, fileName, content){
		 return post(apiUrl+'createFile', {'email': email, "fileName":fileName, "content":content});
	}
}

function post(url, parameters){
	let data = new URLSearchParams();
	Object.keys(parameters).forEach( (key) => data.append(key, parameters[key]));
	return fetch(
		url,
		{ method: 'POST', body: data },

	)
	.then((response) => response.json())
	.then((responseJson) => {
		console.log("dayum"+responseJson);
		return responseJson;
	}).catch((error) => {
		console.error(error);
	});
}
