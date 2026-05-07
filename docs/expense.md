
# Expense API Spec

## Create Expense
Endpoint : POST /api/expenses

Request Header :
Authorization: Bearer <TOKEN>

Request Body :
```json
{
  "amount" : 20000,
  "category" : "makan",
  "description" : "pengeluaran untuk makan"
}
```

Response Body(Success 201) :
```json
{
  "data" : {
    "expenseId" : "1231313726",
    "userId" : "425616171",
    "amount" : 20000,
    "category" : "makan",
    "description" : "pengeluaran untuk makan",
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

## Get Expense
Endpoint : GET /api/expenses/{expenseId}

Request Header :
Authorization: Bearer <TOKEN>

Response Body(Success 200) :
```json
{
  "data" : {
    "expenseId" : "1231313726",
    "userId" : "425616171",
    "amount" : 20000,
    "category" : "makan",
    "description" : "pengeluaran untuk makan",
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

Response Body(Failed 404) :
```json
{
  "errors" : "expense not found"
}
```

## List Expense
Endpoint : GET /api/expenses

Request Header :
Authorization: Bearer <TOKEN>

Query Param :
- category : String, using Like query, optional
- page : Integer, start from 0, default 0
- size : Integer, default 10

Response Body(Success 200) :
```json
{
  "data" : [
    {
      "expenseId" : "1231313726",
      "userId" : "425616171",
      "amount" : 20000,
      "category" : "makan",
      "description" : "pengeluaran untuk makan",
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

## Edit Expense
Endpoint : PATCH /api/expenses/{expenseId}

Request Header :
Authorization: Bearer <TOKEN>

Request Body :
```json
{
  "amount" : 500000, // update opsional
  "category" : "liburan", // update opsional
  "description" : "pengeluaran untuk liburan" // update opsional
}
```

Response Body(Success 200) :
```json
{
  "data" : {
    "expenseId" : "1231313726",
    "userId" : "425616171",
    "amount" : 500000,
    "category" : "liburan",
    "description" : "pengeluaran untuk liburan",
    "createdAt" : "2026-02-15T10:15:30",
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
  "errors" : "expense not found"
}

```

Response Body(Failed 400) :
```json
{
  "errors" : "Bad Request"
}
```

## Remove Expense
Endpoint : DELETE /api/expenses/{expenseId}

Request Header :
Authorization: Bearer <TOKEN>

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
  "errors" : "expense not found"
}
```