
# Dashboard API Spec

## Get User Finances
Endpoint : GET /api/dashboard

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success) :
```json
{
  "data" : {
    "totalIncome" : 5000000, // hasil penjumlahan all income
    "totalExpense" : 1000000, // hasil penjumlahan all expense
    "currentBalance" : 4000000 // hasil totalIncome - totalExpense 
  }
}
```

## Get User Saving Progress
Endpoint : GET /api/dashboard/savings/saving_transaction/progress

Request Header :
- Authorization: Bearer <TOKEN>

Response Body(Success) :

- Hasil penjumlahan real time proses saving dalam bentuk grafik

```json
{
  "data": [
    {
      "savingId": 1,
      "nameSaving": "Rumah",
      "targetAmount": 100000000,
      "currentBalance": 30000000,
      "progressPercentage": 30,
      "remainingAmount": 70000000
    },
    {
      "savingId": 2,
      "nameSaving": "Motor",
      "targetAmount": 20000000,
      "currentBalance": 5000000,
      "progressPercentage": 25,
      "remainingAmount": 15000000
    }
  ]
}
```

