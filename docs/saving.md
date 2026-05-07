
# Saving API Spec

## Create Saving
Endpoint : POST /api/savings

Request Header :
- Authorization: Bearer <TOKEN>

Request Body :
```json
{
  "nameSaving" : "beli motor",
  "targetSaving" : 20000000,
  "deadlineSaving" :"20-04-2026"
}
```

Response Body(Success 201) :
```json
{
  "data" : {
    "savingId" : "1224353638",
    "userId" : "255366728",
    "nameSaving" : "beli motor",
    "targetSaving" : 20000000,
    "deadlineSaving" : "20-04-2026",
    "currentAmount" : 0, // actual amount saving
    "status" : "PROGRESS",
    "createdAt": "2026-02-15T10:15:30",
    "updatedAt" : "2026-02-15T10:15:30"
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

## Get Saving 
Endpoint : GET /api/savings/{savingId}

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success 200) :
```json
{
  "data" : {
    "savingId" : "1224353638",
    "userId" : "255366728",
    "nameSaving" : "beli motor",
    "targetSaving" : 20000000,
    "deadlineSaving" : "20-04-2026",
    "currentAmount" : 0, // actual amount saving
    "status" : "PROGRESS",
    "createdAt": "2026-02-15T10:15:30",
    "updatedAt" : "2026-02-15T10:15:30"
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
  "errors" : "saving not found"
}
```

## Create Saving Transaction
Endpoint : POST /api/savings/{savingId}/saving_transaction

Request Header :
- Authorization: Bearer <TOKEN>

Request Body :
```json
{
  "transactionAmount" : 10000000
}
```

Response Body(Success 201) :
```json
{
  "data" : "OK"
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
  "errors" : "saving not found"
}
```

Response Body(Failed 400) :
```json
{
  "errors" : "Bad Request"
}
```

## Get Saving Progress
Endpoint : GET /api/savings/{savingId}/saving_transaction/progress

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success 200) :
```json
{
  "data" : {
    "savingId" : "12345566667",
    "userId" : "2235363773",
    "nameSaving" : "tabungan beli motor",
    "targetSaving" : 20000000,
    "currentAmount" : 1000000,
    "status" : "PROGRESS",
    "progressPercentage" : 10,
    "remainingAmount" : 19000000
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
  "errors" : "saving not found"
}
```

## List Saving
Endpoint : GET /api/savings

Request Header :
- Authorization: Bearer <TOKEN>

Query Param :
- nameSaving : String , using Like query, optional
- page : Integer, start from 0, default 0
- size : Integer, default 10

Response Body(Success 200) :
```json
{
  "data" : [
    {
      "savingId" : "1224353638",
      "userId" : "255366728",
      "nameSaving" : "beli motor",
      "targetSaving" : 20000000,
      "deadlineSaving" : "20-04-2026",
      "currentAmount" : 10000000, // actual amount saving
      "status" : "PROGRESS",
      "createdAt": "2026-02-15T10:15:30",
      "updatedAt" : "2026-02-15T10:15:30"
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

## Edit Saving
Endpoint : PUT /api/savings/{savingId}

Request Header :
- Authorization: Bearer <TOKEN>

Request Body :
```json
{
  "nameSaving" : "beli mobil",
  "targetSaving" : 50000000,
  "deadlineSaving" :"20-05-2026"
}
```

Response Body(Success 200) :
```json
{
  "data" : {
    "savingId" : "1224353638",
    "userId" : "255366728",
    "nameSaving" : "beli mobil",
    "targetSaving" : 50000000,
    "deadlineSaving" : "20-05-2026",
    "currentAmount" : 10000000, // actual amount saving
    "status" : "PROGRESS",
    "createdAt": "2026-02-15T10:15:30",
    "updatedAt" : "2026-02-20T10:15:30"
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
  "errors" : "saving not found"
}
```

Response Body(Failed 400) :
```json
{
  "errors" : "Bad Request"
}
```

## Remove Saving
Endpoint : DELETE /api/savings/{savingId}

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success 200) :
```json
{
  "data" : "OK"
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
  "errors" : "saving not found"
}
```