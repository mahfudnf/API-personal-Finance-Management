
# User API Spec

## Register User
Endpoint : POST /api/users/register

Request Body :
```json
{
  "firstName" : "mahfud",
  "lastName" : "nur",
  "email" : "mahfud@gmail.com",
  "password " : "abc123"
}
```

Response Body(Success 201) :
```json
{
  "data" : "OK"
}
```

Response Body(Failed 400)
```json
{
  "errors" : "Email sudah terdaftar"
}
```

## Login User
Endpoint : POST /api/users/login

Request Body :
```json
{
  "email" : "mahfud@gmail.com",
  "password" : "abc123"
}
```

Response Body(Success 200) :
```json
{
  "data" : {
    "token" : "Authorization: Bearer <TOKEN>",
    "expiredAt" : "2341515" //millisecond
  }
}
```

Response Body(Failed 400) :
```json
{
  "errors" : "email atau password salah"
}
```

## Get User
Endpoint : GET /api/users/current

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success 200) :
```json
{
  "data" : {
    "userId" : "245167288292",
    "firstName" : "mahfud",
    "lastName" : "nur",
    "email" : "mahfud@gmail.com",
    "role" : "user"
  }
}
```

Response Body(Failed 401) :
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
  "firstName" : "abdul", // update opsional
  "lastName" : "rudi",  // update opsional
  "password " : "xyz123"  // update opsional
}
```

Response Body(Success 200) :
```json
{
  "data" : {
    "userId" : "245167288292",
    "firstName" : "abdul",
    "lastName" : "rudi",
    "email" : "mahfud@gmail.com",
    "role" : "user"
  }
}
```

Response Body(Failed 401) :
```json
{
  "errors" : "Unauthorized"
}
```

Response Body(Failed 400) :
```json
{
  "errors" : "Bad Request"
}
```

## Logout
Endpoint : POST /api/users/logout

Request Header :
- Authorization: Bearer <TOKEN>

Response Body (Success 200) :
```json
{
  "data" : "OK"
}
```