# Quickstart Validation Guide: S6 Pruebas Unitarias en Microservicios

**Purpose**: Verify the feature works end-to-end after implementation is complete.
**Prerequisites**: Java 17+ (or JDK 25 with the build fix), Maven 3.8+.

---

## Scenario 1 — Run all unit tests

Validates: FR-001 through FR-005, SC-002, SC-003, criteria 1–3.

```bash
# From project root
JAVA_HOME=/usr/lib/jvm/jdk-25.0.3-oracle-x64 \
  /home/kabes/.m2/wrapper/dists/apache-maven-3.9.9-bin/4nf9hui3q3djbarqar9g711ggc/apache-maven-3.9.9/bin/mvn \
  test -Djacoco.skip=true --no-transfer-progress
```

**Expected output**:
```
Tests run: 66, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

**Evidence files**: `target/surefire-reports/*.xml`

---

## Scenario 2 — Start the application

Validates: FR-006 through FR-010, SC-004.

```bash
JAVA_HOME=/usr/lib/jvm/jdk-25.0.3-oracle-x64 \
  /home/kabes/.m2/wrapper/dists/apache-maven-3.9.9-bin/4nf9hui3q3djbarqar9g711ggc/apache-maven-3.9.9/bin/mvn \
  spring-boot:run
```

**Expected**: Application starts on port 8080 with `Started MinimarketApplication`.

---

## Scenario 3 — Authenticate and get JWT

Validates: FR-006, User Story 2.

```bash
curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | python3 -m json.tool
```

**Expected response**:
```json
{
  "token": "<JWT string>",
  "username": "admin",
  "roles": ["ROLE_ADMIN"]
}
```

Store the token: `TOKEN=$(curl -s ... | python3 -c "import sys,json; print(json.load(sys.stdin)['token'])")`

---

## Scenario 4 — Access protected endpoint with JWT

Validates: FR-007, RBAC enforcement.

```bash
# Authorized (ROLE_ADMIN accessing productos)
curl -s http://localhost:8080/api/productos \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool

# Unauthorized (no token)
curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/productos
# Expected: 401
```

---

## Scenario 5 — Verify Swagger UI

Validates: FR-009, FR-010, SC-004, User Story 3.

Open in browser: `http://localhost:8080/swagger-ui/index.html`

Checks:
- [ ] Page loads with title "Minimarket Plus API"
- [ ] 9 resource groups visible (auth, categorias, productos, inventario, carrito, ventas, detalle-ventas, usuarios, public)
- [ ] Each endpoint shows description, parameters, and response codes
- [ ] "Authorize" button visible; after entering token, protected endpoints return 200 (not 401)

---

## Scenario 6 — Public endpoint (no auth)

Validates: Security config open route.

```bash
curl -s http://localhost:8080/public/hola
# Expected: "Hola Mundo desde el Minimarket!"
```

---

## Scenario 7 — Verify Surefire reports exist

Validates: FR-013, criterion 4 evidence.

```bash
ls target/surefire-reports/*.xml
```

**Expected**: XML files for each test class (CarritoServiceTest, InventarioServiceTest, ProductoServiceTest, UsuarioServiceTest, UsuarioTest, VentaServiceTest).

---

## What Is NOT Validated Here

- Technical report completeness (`doc/md/PBY2202_Exp2_S6_Grupo7.md`) — reviewed manually.
- README quality — reviewed by reading `README.md` at repo root.
- GitHub branch publication — verified with `git log --oneline -5` and checking the remote.

See [contracts/api-endpoints.md](contracts/api-endpoints.md) for full endpoint reference and [data-model.md](data-model.md) for entity details.
