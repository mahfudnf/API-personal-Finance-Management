
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

Response Body(Success) :
```json
{
  "data" : {
    "income_id" : "1231313",
    "amount" : 100000,
    "category" : "gaji",
    "createAt" : "2026-02-15T10:15:30",
    "description" : "pendapatan gaji bulan juni"
  }
}
```

Response Body(Failed 401) :
```json
{
  "errors" : "Unauthorized"
}
```

## Get Income
Endpoint : GET /api/incomes/{incomeId}

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success) :
```json
{
  "data" : {
    "income_id" : "1231313",
    "amount" : 100000,
    "category" : "gaji",
    "createAt" : "2026-02-15T10:15:30",
    "description" : "pendapatan gaji bulan juni"
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
- category : String, income category, using Like query, optional
- page : Integer, start from 0, default 0
- size : Integer, default 10

Response Body(Success) :
```json
{
  "data" : [
    {
      "income_id" : "1231313",
      "amount" : 100000,
      "category" : "gaji",
      "createdAt" : "2026-02-15T10:15:30",
      "description" : "pendapatan gaji bulan juni"
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
  "amount" : 200000, // put if only one to update amount
  "category" : "freelance", // put if only one to update category
  "description" : "pendapatan gaji dari edit vidio" // put if only one to update description
}
```

Response Body(Success) :
```json
{
  "data" : {
    "income_id" : "1231313",
    "amount" : 200000,
    "category" : "freelance",
    "createdAt" : "2026-02-15T10:15:30",
    "description" : "pendapatan gaji dari edit vidio"
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

## Remove Income
Endpoint : DELETE /api/incomes/{incomeId}

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success) :
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

