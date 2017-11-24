'use strict';


// Services for data retrieval from the API
export default{
	ls: function(){
		var data = new URLSearchParams();
		data.append('email', 'my@mail.com');
		return fetch(
			'http://localhost:8080/xDriveRest/api/ls',
			{
  				method: 'POST',
  				headers: {
    				'Content-Type': 'application/x-www-form-urlencoded',
    				'Accept': 'application/json',
					'Access-Control-Allow-Origin':'*',
  				},
				body: data.toString()
			},

		)
      	.then((response) => response.json())
      	.then((responseJson) => {
			console.log(responseJson);
        	return responseJson;
      	}).catch((error) => {
        	console.error(error);
      	});
	}
}
