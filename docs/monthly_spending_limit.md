
# Monthly spending limit API Spec

## Create Spending Limit
Endpoint : POST /api/limits

Request Header :
- Authorization: Bearer <TOKEN>

Request Body :
```json
{
  "limitValue" : 2000000,
  "monthValue" : "MEI",
  "yearValue" : 2026
}
```

Response Body(Success 201) :
```json
{
  "data" : {
    "limitId" : "122363773",
    "userId" : "134252626",
    "limitValue" : 2000000,
    "monthValue" : "MEI",
    "yearValue" : 2026,
    "isActive" : true
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

## Get Current Limit
Endpoint : GET /api/limits/current

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success 200) :
```json
{
  "data" : {
    "limitId" : "122363773",
    "userId" : "134252626",
    "limitValue" : 2000000,
    "monthValue" : "MEI",
    "yearValue" : 2026,
    "isActive" : true
  }
}
```

Response Body(Failed 401) :
```json
{
  "errors" : "Unauthorized"
}
```

Response Body(Failed 404) :
```json
{
  "errors" : "Limit Not Found"
}
```

## Get Limit By Month And Year
Endpoint : GET /api/limits/{month}/{year}

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success 200) :
```json
{
  "data" : {
    "limitId" : "122363773",
    "userId" : "134252626",
    "limitValue" : 2000000,
    "monthValue" : "MEI",
    "yearValue" : 2026,
    "isActive" : true
  }
}
```

Response Body(Failed 401) :
```json
{
  "errors" : "Unauthorized"
}
```

Response Body(Failed 404) :
```json
{
  "errors" : "Limit Not Found"
}
```

## list Limits
Endpoint : GET /api/limits

Request Header :
- Authorization: Bearer <TOKEN>

Query Param :
- limitValue : Long, optional, for filtering
- page : Integer, start from 0, default 0
- size : Integer, default 10

Response Body(Success 200) :
```json
{
  "data" : [
    {
      "limitId" : "122363773",
      "userId" : "134252626",
      "limitValue" : 2000000,
      "monthValue" : "MEI",
      "yearValue" : 2026,
      "isActive" : true
    }
  ],
  "paging" : {
    "currentPage" : 0,
    "totalPages" : 10,
    "size" : 10
  }
}
```

Response Body(Failed 401) :
```json
{
  "errors" : "Unauthorized"
}
```

## Update Limit
Endpoint : PUT /api/limits/{limitId}

Request Header :
- Authorization: Bearer <TOKEN>

Request Body :
```json
{
  "limitValue" : 1000000,
  "monthValue" : "JUNI",
  "yearValue" : 2026
}
```

Response Body(Success 200) :
```json
{
  "data" : {
    "limitId" : "122363773",
    "userId" : "134252626",
    "limitValue" : 1000000,
    "monthValue" : "JUNI",
    "yearValue" : 2026,
    "isActive" : true
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