
# Saving API Spec

## Create Saving
Endpoint : POST /api/savings

Request Header :
- Authorization: Bearer <TOKEN>

Request Body :
```json
{
  "nameSaving" : "tabungan beli motor",
  "targetAmount" : 20000000,
  "deadline" :"20-04-2026"
}
```

Response Body(Success) :
```json
{
  "data" : {
    "saving_id" : "45566667",
    "nameSaving" : "tabungan beli motor",
    "targetAmount" : 20000000,
    "deadline" :"20-04-2026",
    "createAt" : "2026-02-15T10:15:30"
  }
}
```

Response Body(Failed 401) :
```json
{
  "errors" : "Unauthorized"
}
```

## Get Saving 
Endpoint : GET /api/savings/{savingId}

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success) :
```json
{
  "data" : {
    "saving_id" : "45566667",
    "nameSaving" : "tabungan beli motor",
    "targetAmount" : 20000000,
    "deadline" :"20-04-2026",
    "createAt" : "2026-02-15T10:15:30"
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
  "amount" : 10000000
}
```

Response Body(Success) :
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

## Get Saving Progress
Endpoint : GET /api/savings/{savingId}/saving_transaction/progress

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success) :
```json
{
  "data" : {
    "saving_id" : "45566667",
    "nameSaving" : "tabungan beli motor",
    "targetAmount" : 20000000,
    "currentBalance" : 1000000,
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
- name : String, saving name, using Like query, optional
- page : Integer, start from 0, default 0
- size : Integer, default 10

Response Body(Success) :
```json
{
  "data" : [
    {
      "saving_id" : "45566667",
      "nameSaving" : "tabungan beli motor",
      "targetAmount" : 20000000,
      "deadline" :"20-04-2026",
      "createAt" : "2026-02-15T10:15:30"
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
  "nameSaving" : "tabungan beli motor",
  "targetAmount" : 20000000,
  "deadline" :"20-04-2026"
}
```

Response Body(Success) :
```json
{
  "data" : {
    "saving_id" : "45566667",
    "nameSaving" : "tabungan beli motor",
    "targetAmount" : 20000000,
    "deadline" :"20-04-2026",
    "createAt" : "2026-02-15T10:15:30"
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

## Remove Saving
Endpoint : DELETE /api/savings/{savingId}

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success) :
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