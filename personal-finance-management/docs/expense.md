
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

Response Body(Success) :
```json
{
  "data" : {
    "expense_id" : "2341441",
    "amount" : 20000,
    "category" : "makan",
    "createdAt" : "2026-02-15T10:15:30",
    "description" : "pengeluaran untuk makan"
  }
}
```

Response Body(Failed 401) :
```json
{
  "errors" : "Unauthorized"
}
```

## Get Expense
Endpoint : GET /api/expenses/{expenseId}

Request Header :
Authorization: Bearer <TOKEN>

Response Body(Success) :
```json
{
  "data" : {
    "expense_id" : "2341441",
    "amount" : 20000,
    "category" : "makan",
    "createdAt" : "2026-02-15T10:15:30",
    "description" : "pengeluaran untuk makan"
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
- category : String, expense category, using Like query, optional
- page : Integer, start from 0, default 0
- size : Integer, default 10

Response Body(Success) :
```json
{
  "data" : [
    {
      "expense_id" : "2341441",
      "amount" : 20000,
      "category" : "makan",
      "createdAt" : "2026-02-15T10:15:30",
      "description" : "pengeluaran untuk makan"
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
  "amount" : 500000, // put if only one to update amount
  "category" : "liburan", // put if only one to update category
  "description" : "pengeluaran untuk liburan" // put if only one to update description
}
```

Response Body(Success) :
```json
{
  "data" : {
    "expense_id" : "2341441",
    "amount" : 500000,
    "category" : "liburan",
    "createdAt" : "2026-02-15T10:15:30",
    "description" : "pengeluaran untuk liburan"
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

## Remove Expense
Endpoint : DELETE /api/expenses/{expenseId}

Request Header :
Authorization: Bearer <TOKEN>

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
  "errors" : "expense not found"
}
```