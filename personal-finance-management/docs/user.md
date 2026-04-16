
# User API Spec

## Register User
Endpoint : POST /api/users/register

Request Body :
```json
{
  "name" : "joko",
  "email" : "joko@email.com",
  "password " : "abc123"
}
```

Response Body(Success) :
```json
{
  "data" : "OK"
}
```

Response Body(Failed)
```json
{
  "errors" : "name already registered"
}
```

## Login User
Endpoint : POST /api/users/login

Request Body :
```json
{
  "email" : "joko@gmail.com",
  "password" : "abc123"
}
```

Response Body(Success) :
```json
{
  "data" : {
    "token" : "Authorization: Bearer <TOKEN>",
    "expiredAt" : "2341515" //millisecond
  }
}
```

Response Body(Failed) :
```json
{
  "errors" : "name or password wrong"
}
```

## Get User
Endpoint : GET /api/users/current

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success) :
```json
{
  "data" : {
    "name" : "joko",
    "email" : "joko@gmail.com"
  }
}
```

Response Body(Failed) :
```json
{
  "errors" : "Unauthorized"
}
```

## Update User
Endpoint : PATCH /api/users/current

Request Header :
- Authorization: Bearer <TOKEN>

Request Body :
```json
{
  "name" : "asep", //put if only one to update
  "password" : "xyz123" //put if only one to update
}
```

Response Body(Success) :
```json
{
  "data" : {
    "name" : "asep",
    "email" : "joko@gmail.com"
  }
}
```

Response Body(Failed) :
```json
{
  "errors" : "Unauthorized"
}
```

## Logout
Endpoint : POST /api/users/logout

Request Header :
- Authorization: Bearer <TOKEN>

Response Body :
```json
{
  "data" : "OK"
}
```