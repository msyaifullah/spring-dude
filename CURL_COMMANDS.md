# API Curl Commands for Postman

Base URL: `http://localhost:8080`

## Authentication Endpoints

### 1. Login as Admin
```bash
curl -X POST http://localhost:8080/api/v1/dashboard/auth/signin \
  -u admin:changeMeSuperman \
  -H "Content-Type: application/json"
```

### 2. Login as Client
```bash
curl -X POST http://localhost:8080/api/v1/dashboard/auth/signin \
  -u client:changeMeRobin \
  -H "Content-Type: application/json"
```

### 3. Login with Explicit Authorization Header (Admin)
```bash
curl -X POST http://localhost:8080/api/v1/dashboard/auth/signin \
  -H "Authorization: Basic $(echo -n 'admin:changeMeSuperman' | base64)" \
  -H "Content-Type: application/json"
```

### 4. Login with Explicit Authorization Header (Client)
```bash
curl -X POST http://localhost:8080/api/v1/dashboard/auth/signin \
  -H "Authorization: Basic $(echo -n 'client:changeMeRobin' | base64)" \
  -H "Content-Type: application/json"
```

**Note:** After login, you will receive an `access_token` in the response. Use this token in the `Authorization` header for subsequent requests as `Bearer <access_token>`.

---

## User Management Endpoints

### 5. Sign Up (Admin Only)
**Requires:** Bearer token with ROLE_ADMIN

```bash
curl -X POST http://localhost:8080/api/v1/dashboard/auth/signup \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "password": "password123",
    "email": "newuser@email.com",
    "roles": ["ROLE_CLIENT"]
  }'
```

### 6. Sign Out
**Requires:** Bearer token with ROLE_ADMIN or ROLE_CLIENT

```bash
curl -X DELETE http://localhost:8080/api/v1/dashboard/auth/signout \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

### 7. Get Current User Session (Who Am I)
**Requires:** Bearer token with ROLE_ADMIN or ROLE_CLIENT

```bash
curl -X GET http://localhost:8080/api/v1/dashboard/auth/user/me \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

### 8. Search User by Username (Admin Only)
**Requires:** Bearer token with ROLE_ADMIN

```bash
curl -X GET http://localhost:8080/api/v1/dashboard/auth/user/admin \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

### 9. Delete User by Username (Admin Only)
**Requires:** Bearer token with ROLE_ADMIN

```bash
curl -X DELETE http://localhost:8080/api/v1/dashboard/auth/user/username \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

### 10. Refresh Access Token
**Requires:** Bearer token with ROLE_ADMIN or ROLE_CLIENT

```bash
curl -X GET http://localhost:8080/api/v1/dashboard/auth/extend-access \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

---

## Health Check & Root Endpoints

### 11. Health Check
```bash
curl -X GET http://localhost:8080/api/v1/dashboard/health-check \
  -H "Content-Type: application/json"
```

### 12. Root Endpoint
```bash
curl -X GET http://localhost:8080/api/v1/dashboard/ \
  -H "Content-Type: application/json"
```

---

## Admin Payment Option Endpoints

### 13. Create Payment Option (Admin Only)
**Requires:** Bearer token with ROLE_ADMIN

```bash
curl -X POST http://localhost:8080/api/v1/dashboard/admin/payopt/test \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currency": "USD",
    "amount": 100.00
  }'
```

### 14. Get Payment Options (Admin or Client)
**Requires:** Bearer token with ROLE_ADMIN or ROLE_CLIENT

```bash
curl -X GET http://localhost:8080/api/v1/dashboard/admin/payopt/test \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
  -H "Content-Type: application/json"
```

---

## Auditor Endpoints

### 15. Get All Audits
```bash
curl -X GET http://localhost:8080/api/v1/audits \
  -H "Content-Type: application/json"
```

### 16. Get Audit by ID
```bash
curl -X GET http://localhost:8080/api/v1/audits/1 \
  -H "Content-Type: application/json"
```

### 17. Create Audit
```bash
curl -X POST http://localhost:8080/api/v1/audits \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "method": "POST",
    "url": "/api/v1/dashboard/auth/signin",
    "status": 200
  }'
```

### 18. Update Audit
```bash
curl -X PUT http://localhost:8080/api/v1/audits/1 \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "method": "POST",
    "url": "/api/v1/dashboard/auth/signin",
    "status": 200
  }'
```

### 19. Delete Audit
```bash
curl -X DELETE http://localhost:8080/api/v1/audits/1 \
  -H "Content-Type: application/json"
```

---

## Default User Credentials

### Admin User
- **Username:** `admin`
- **Password:** `changeMeSuperman`
- **Email:** `admin@email.com`
- **Roles:** `ROLE_ADMIN`

### Client User
- **Username:** `client`
- **Password:** `changeMeRobin`
- **Email:** `client@email.com`
- **Roles:** `ROLE_CLIENT`, `ROLE_ACCOUNT_MANAGER`

---

## How to Use in Postman

1. **Import Curl Commands:**
   - Open Postman
   - Click "Import" button
   - Select "Raw text" tab
   - Paste any curl command above
   - Click "Continue" and "Import"

2. **Using Bearer Token:**
   - After login, copy the `access_token` from the response
   - In Postman, go to the "Authorization" tab
   - Select "Bearer Token" type
   - Paste your token in the Token field
   - Or manually set header: `Authorization: Bearer YOUR_ACCESS_TOKEN`

3. **Using Basic Auth:**
   - In Postman, go to the "Authorization" tab
   - Select "Basic Auth" type
   - Enter username and password
   - Postman will automatically encode it

---

## Example Workflow

1. **Login as Admin:**
   ```bash
   curl -X POST http://localhost:8080/api/v1/dashboard/auth/signin \
     -u admin:changeMeSuperman \
     -H "Content-Type: application/json"
   ```

2. **Copy the `access_token` from response**

3. **Use the token for authenticated requests:**
   ```bash
   curl -X GET http://localhost:8080/api/v1/dashboard/auth/user/me \
     -H "Authorization: Bearer YOUR_ACCESS_TOKEN" \
     -H "Content-Type: application/json"
   ```

---

## Notes

- Replace `YOUR_ACCESS_TOKEN` with the actual token received from login
- Replace `localhost:8080` with your actual server URL if different
- All endpoints return JSON responses
- Most endpoints require authentication except login, health-check, and root

