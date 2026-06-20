# Plan de Acción — Semana 1 · Desarrollo Backend II (PBY2202)

**Actividad:** Utilizando estrategias y configuración de frameworks de seguridad en backend  
**Proyecto:** Sistema Minimarket (reutilizado de Desarrollo Backend I)  
**Fecha:** 23 de mayo de 2026

---

## Checklist de entregables

- [x] `PBY2202_Exp1_S1_Informe_Minimarket.docx` — informe completo con análisis, configuración y evidencias
- [x] `src/main/java/com/minimarket/security/config/SecurityConfig.java` — configuración mejorada con RBAC y cabeceras OWASP
- [x] `pom.xml` — dependencia Oracle JDBC agregada, H2 movida a scope=test
- [x] `src/main/resources/application.properties` — Oracle DB vía variables de entorno
- [x] `src/main/resources/data-init.sql` — datos iniciales con BCrypt
- [x] `src/test/resources/application-test.properties` — H2 para tests

---

## Comandos Git — flujo de trabajo

```bash
# 1. Verificar estado del repositorio
git status && git diff

# 2. Crear rama semántica para la actividad
git checkout -b feat/security-spring-security-s1

# 3. Stage de los archivos modificados
git add src/main/java/com/minimarket/security/config/SecurityConfig.java
git add src/main/resources/application.properties
git add src/main/resources/data-init.sql
git add src/test/resources/application-test.properties
git add pom.xml
git add PBY2202_Exp1_S1_Informe_Minimarket.docx
git add PLAN_ACCION_S1.md

# 4. Commit semántico (Conventional Commits)
git commit -m "feat: configurar Spring Security con RBAC y Oracle DB para Semana 1

- Agregar SecurityConfig con autorización por roles (GERENTE/EMPLEADO/CLIENTE)
- Configurar cabeceras de seguridad HTTP: HSTS, X-Frame-Options, Referrer-Policy
- Migrar datasource de H2 a Oracle DB en AWS RDS con credenciales en env vars
- Agregar ojdbc11 a pom.xml, mover H2 a scope=test
- Crear data-init.sql con usuarios de prueba (BCrypt strength=12)
- Agregar application-test.properties para tests con H2"

# 5. Push de la rama (primera vez)
git push -u origin feat/security-spring-security-s1
```

> ⚠️ **NO ejecutar** `git push --force` ni merge a main sin revisión explícita.

---

## Configuración de variables de entorno (AWS RDS)

Antes de ejecutar la aplicación, exportar en la terminal o configurar en el servidor:

```bash
export AWS_RDS_ENDPOINT="minimarket-db.xxxxxxxxx.us-east-1.rds.amazonaws.com"
export DB_SERVICE_NAME="MINIMARKETDB"
export DB_USERNAME="minimarket_app"
export DB_PASSWORD="TuPasswordSegura123!"
```

Para IntelliJ IDEA: `Run > Edit Configurations > Environment Variables`.

---

## Verificación funcional post-configuración

```bash
# Compilar y ejecutar (con variables de entorno seteadas)
./mvnw spring-boot:run

# Endpoints a probar:
# GET  /public/index        → 200 sin autenticación
# POST /auth/login          → form-data: username=gerente&password=gerente123
# GET  /api/productos       → 200 con sesión activa (cualquier rol)
# GET  /api/usuarios        → 200 solo ROLE_GERENTE, 403 para EMPLEADO/CLIENTE
# GET  /api/inventario      → 200 para GERENTE/EMPLEADO, 403 para CLIENTE
# POST /auth/logout         → invalida sesión y redirige
```

---

## Criterios de evaluación cubiertos

| Criterio (Pauta formativa) | Estado | Evidencia |
|---|---|---|
| Explica importancia de seguridad y relación con normativas | ✅ | Informe §1.4 |
| Describe amenazas (SQLi, XSS, CSRF) y estrategias de mitigación | ✅ | Informe §1.2 + §2.6 |
| Identifica puntos críticos en el backend del proyecto | ✅ | Informe §1.1 |
| Configura e implementa framework con configuración funcional | ✅ | SecurityConfig.java + application.properties |
| Propone estructura de autenticación/autorización por roles | ✅ | Informe §1.3-1.4 + SecurityConfig.java §3 |
| Documenta análisis, configuraciones y mitigación de amenazas | ✅ | Informe completo (6 secciones) |
