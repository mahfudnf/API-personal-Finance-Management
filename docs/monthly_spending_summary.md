# Monthly spending Summary API Spec

## Generate Spending Summary
Endpoint : POST /api/limits/{limitId}/summaries

Request Header :
- Authorization: Bearer <TOKEN>

Query Param :
- monthValue : Integer, required , month to generate summary
- yearValue : Integer, required , month to generate summary

Response Body(Success 201) :
```json
{
  "data" : {
    "summaryId" : "1223425522",
    "userId" : "21415626262",
    "limitId" : "22551166161",
    "totalSpent" : 2000000, 
    "statusSpent" : "SAFE",
    "monthValue" : "MEI",
    "yearValue" : 2026
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

## Get Current Month Summary By User
Endpoint : GET /api/summaries/current

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success 200) :
```json
{
  "data" : {
    "summaryId" : "1223425522",
    "userId" : "21415626262",
    "limitId" : "22551166161",
    "totalSpent" : 2000000, 
    "statusSpent" : "SAFE",
    "monthValue" : "MEI",
    "yearValue" : 2026
  }
}
```

Response Body(Failed 401) :
```json
{
  "errors" : "Unauthorized"
}
```

## Get Spending Summary By User By Month And Year
Endpoint : GET /api/summaries/{monthValue}/{yearValue}

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success 200) :
```json
{
  "data" : {
    "summaryId" : "1223425522",
    "userId" : "21415626262",
    "limitId" : "22551166161",
    "totalSpent" : 2000000, 
    "statusSpent" : "SAFE",
    "monthValue" : "MEI",
    "yearValue" : 2026
  }
}
```

Response Body(Failed 401) :
```json
{
  "errors" : "Unauthorized"
}
```

## Recalculate Spending Summary
Endpoint : POST /api/limits/{limitId}/summaries/recalculate

Request Header :
- Authorization: Bearer <TOKEN>

Query Param :
- monthValue : Integer, required , month to generate summary
- yearValue : Integer, required , month to generate summary

Response Body(Success 200) :
```json
{
  "data" : {
    "summaryId" : "1223425522",
    "userId" : "21415626262",
    "limitId" : "22551166161",
    "totalSpent" : 3000000, // Berubah saat ada perubahan data expense
    "statusSpent" : "EXCEEDED", // Berubah saat ada perubahan data expense/spending limit
    "monthValue" : "MEI",
    "yearValue" : 2026
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
