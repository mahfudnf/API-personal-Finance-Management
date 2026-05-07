
# Income API Spec

## Create Income
Endpoint : POST /api/incomes

Request Header :
- Authorization: Bearer <TOKEN>

Request Body :
```json
{
  "amount" : 100000,
  "category" : "gaji",
  "description" : "pendapatan gaji bulan juni"
}
```

Response Body(Success 201) :
```json
{
  "data" : {
    "incomeId" : "1231313726",
    "userId" : "425616171",
    "amount" : 100000,
    "category" : "gaji",
    "description" : "pendapatan gaji bulan juni",
    "createdAt" : "2026-02-15T10:15:30",
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

## Get Income
Endpoint : GET /api/incomes/{incomeId}

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success 200) :
```json
{
  "data" : {
    "incomeId" : "1231313726",
    "userId" : "425616171",
    "amount" : 100000,
    "category" : "gaji",
    "description" : "pendapatan gaji bulan juni",
    "createdAt" : "2026-02-15T10:15:30",
    "updatedAt" : "2026-02-15T10:15:30"
  }
}
```

Response Body(Failed 404) :
```json
{ 
  "errors" : "income not found"
}
```

Response Body(Failed 401) :
```json
{
  "errors" : "Unauthorized"
}
```

## List Income
Endpoint : GET /api/incomes

Request Header :
- Authorization: Bearer <TOKEN>

Query Param :
- category : String, using Like query, optional
- page : Integer, start from 0, default 0
- size : Integer, default 10

Response Body(Success 200) :
```json
{
  "data" : [
    {
      "incomeId" : "1231313726",
      "userId" : "425616171",
      "amount" : 100000,
      "category" : "gaji",
      "description" : "pendapatan gaji bulan juni",
      "createdAt" : "2026-02-15T10:15:30",
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

## Edit Income
Endpoint : PATCH /api/incomes/{incomeId}

Request Header :
- Authorization: Bearer <TOKEN>

Request Body :
```json
{
  "amount" : 200000, // update opsional
  "category" : "freelance", // update opsional
  "description" : "pendapatan gaji dari edit vidio" // update opsional
}
```

Response Body(Success 200) :
```json
{
  "data" : {
    "incomeId" : "1231313726",
    "userId" : "425616171",
    "amount" : 200000,
    "category" : "freelance",
    "description" : "pendapatan gaji dari edit vidio",
    "createdAt" : "2026-02-15T10:15:30",
    "updatedAt" : "2026-02-20T10:15:30"
  }
}
```

Response Body(Failed 404) :
```json
{ 
  "errors" : "income not found"
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

## Remove Income
Endpoint : DELETE /api/incomes/{incomeId}

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success 200) :
```json
{
  "data" : "OK"
}
```

Response Body(Failed 404) :
```json
{
  "errors" : "income not found"
}
```

Response Body(Failed 401) :
```json
{
  "errors" : "Unauthorized"
}
```

