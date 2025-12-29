#!/bin/bash

BASE_URL="http://localhost:8080"
USERNAME="testuser_$(date +%s)"
EMAIL="${USERNAME}@example.com"
PASSWORD="password123"

echo "1. Registering new user..."
REGISTER_PAYLOAD=$(cat <<EOF
{
  "idGimnasio": 1,
  "idSucursalPorDefecto": 1,
  "nombre": "Test",
  "apellido": "User",
  "email": "$EMAIL",
  "username": "$USERNAME",
  "password": "$PASSWORD",
  "telefono": "1234567890"
}
EOF
)

echo "Payload: $REGISTER_PAYLOAD"

# Use -v for verbose to see headers and errors
curl -v -X POST "$BASE_URL/auth/register" \
  -H "Content-Type: application/json" \
  -d "$REGISTER_PAYLOAD" > register_response.txt 2>&1

cat register_response.txt
echo ""

echo "2. Logging in..."
LOGIN_PAYLOAD=$(cat <<EOF
{
  "username": "$USERNAME",
  "password": "$PASSWORD"
}
EOF
)

curl -v -X POST "$BASE_URL/auth/login" \
  -H "Content-Type: application/json" \
  -d "$LOGIN_PAYLOAD" > login_response.txt 2>&1

cat login_response.txt

LOGIN_RESPONSE=$(cat login_response.txt)
# Extract token from the response body (last line usually)
BODY=$(tail -n 1 login_response.txt)
TOKEN=$(echo $BODY | grep -o '"token":"[^"]*' | cut -d'"' -f4)

if [ -z "$TOKEN" ]; then
  echo "Failed to get token. Exiting."
  exit 1
fi

echo "Token: $TOKEN"
echo ""

echo "3. Listing Gyms (Protected Endpoint)..."
curl -v -X GET "$BASE_URL/api/gimnasios" \
  -H "Authorization: Bearer $TOKEN"
