# CoolApp
The coolest app in existence.

## About
### Tools
* [Maven](https://maven.apache.org/) - Project management
* [Jersey (with Grizzly)](https://jersey.github.io/) - RESTful web service
* [JUnit 4](http://junit.org/junit4/) - Unit testing
* [RestFB](http://restfb.com/) - Facebook Graph API client
* [Amazon DynamoDB](https://aws.amazon.com/dynamodb/) - Database

### Setup
Run the `runDB.sh` script to start the DynamoDB database. It will attempt to download the database if it is missing:
```
./runDB.sh
```

**Notes:** 
* The database *must* be running while the App is being tested or run
* Maven is required to run the following commands

To test the project
```
mvn test
```

To create an executable jar file `target/coolapp-[VERSON].jar` of the app
```
mvn package
```

## API

### `POST createUser`
POST request to create a new user from a Facebook user access token
#### Query Parameter
[`access_token`](https://developers.facebook.com/docs/facebook-login/access-tokens#usertokens)
#### Required Permissions
none
#### Success
Status code: 201
```
 { "message": "User created",
   "user_id": [Facebook ID of new user]
 }
```
#### Example
```
curl -i -X POST http://{app_base_uri}/createUser?access_token={user_access_token}
```

<hr>

### `GET popular`
GET request for whether user is popular. User considered popular if they have more than 50 friends.
#### Query Parameter
[`access_token`](https://developers.facebook.com/docs/facebook-login/access-tokens#usertokens)
#### Required Permissions
[`user_friends`](https://developers.facebook.com/docs/facebook-login/permissions/#reference-user_friends)
#### Success
Status code: 200
```
 { "is_popular": true/false
   "userId": [Facebook ID of user]
 }
```
#### Example
```
curl -i http://{app_base_uri}/popular?access_token={user_access_token}
```

<hr>

### `GET band`
GET request for users favourite band. This is the band that was liked longest ago.

Returns `"is_valid": false` if user has not liked any bands, `true` otherwise.
#### Query Parameter
[`access_token`](https://developers.facebook.com/docs/facebook-login/access-tokens#usertokens)
#### Required Permissions
[`user_likes`](https://developers.facebook.com/docs/facebook-login/permissions/#reference-user_likes)
#### Success
Status code: 200
```
 { "favourite_band": [Band liked longest ago]
   "is_valid": true/false
   "userId": [Facebook ID of user]
 }
```
#### Example
```
curl -i http://{app_base_uri}/band?access_token={user_access_token}
```

<hr>

### `GET place`
GET request for users favourite place. This is the place they are most [tagged](https://developers.facebook.com/docs/graph-api/reference/user/tagged_places).

Returns `"is_valid": false` if user has not been tagged anywhere, `true` otherwise.
#### Query Parameter
[`access_token`](https://developers.facebook.com/docs/facebook-login/access-tokens#usertokens)
#### Required Permissions
[`user_tagged_places`](https://developers.facebook.com/docs/facebook-login/permissions/#reference-user_tagged_places)
#### Success
Status code: 200
```
 { "favourite_place": [Band liked longest ago]
   "is_valid": true/false
   "userId": [Facebook ID of user]
 }
```
#### Example
```
curl -i http://{app_base_uri}/place?access_token={user_access_token}
```

<hr>

### Errors
Error responses are given as JSON with the following format
```
 * { "error": {
 *     "message": [Error message],
 *     [Additional fields]
 *   }
 * }
```

The following error messages are returned with a status code of 400 'Bad Request':
* `User does not exist` : The access token is well formed, but the user is not in the database.
* `Malformed user access token` : The access token is malformed, or could not be understood.
* `Missing required permissions`: One or more required permisions for the request are missing from the access token provided.
* `Query parameter 'access_token' missing from request`: The query parameter `access_token` left empty or was entirely excluded from the request.

#### Additional fields
* `"permissions"`: A list of permissions that were missing from the request.
* `"code"`: If a Facebook Graph API exception is thrown, its message and [error code](http://fbdevwiki.com/wiki/Error_codes) is forwarded on.
