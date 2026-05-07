
# Dashboard API Spec

## Get Dashboard

Endpoint : GET /api/dashboard

Request Header :
- Authorization: Bearer <TOKEN>

Description :
Mengambil ringkasan data keuangan user yang sedang login,
meliputi total income, total expense, saldo saat ini, spending limit,
dan progress tabungan.

---

Response Body(Success 200)

```json
{
  "data": {
    "totalIncome": 10000000,
    "totalExpense": 4000000,
    "currentBalance": 6000000,
    "spendingLimit": 5000000,
    "savingProgress": [
      {
        "savingId": "1234451455156",
        "userId": "233415516166",
        "nameSaving": "Tabungan Rumah",
        "targetSaving": 20000000,
        "currentAmount": 5000000,
        "remainingAmount": 15000000,
        "progressPercentage": 25.0
      }
    ]
  }
}
```

