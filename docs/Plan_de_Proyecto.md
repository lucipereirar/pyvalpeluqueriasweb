# Plan de Proyecto

## Pyval — Tienda Online de Productos de Belleza y Peluquería

| Campo | Detalle |
|-------|---------|
| **Proyecto** | Pyval |
| **Versión del documento** | 1.1 (Carta Gantt con fechas) |
| **Fecha de emisión** | 02-07-2026 |
| **Autor** | Luciano Alonso Pereira Rodríguez |
| **Documentos relacionados** | Acta de Constitución, Propuesta de Solución, Plan de Marketing |

---

## 1. Introducción y objetivo del plan

Este Plan de Proyecto define **cómo se construirá** la tienda online Pyval: el desglose del trabajo, las fases, el cronograma (Carta Gantt), los hitos, los recursos y las dependencias. Complementa al Acta de Constitución (que autoriza el proyecto) y a la Propuesta de Solución (que describe el producto).

**Objetivo del plan:** llevar el proyecto desde la planificación hasta el lanzamiento de la tienda en producción, de forma controlada y por fases.

---

## 2. Alcance del proyecto

Construcción de una tienda de comercio electrónico **de un solo dueño** con: sitio web público, panel de administración, pagos reales, facturación electrónica, despacho con seguimiento, agente de atención por WhatsApp con IA y analítica de ventas; desplegada en la nube y legalmente habilitada para operar en Chile. El detalle de inclusiones y exclusiones está en el Acta de Constitución (sección 4).

---

## 3. Enfoque y metodología

- **Trabajo por fases** (F1 a F7), con avance iterativo dentro del desarrollo.
- **Entregables verificables** al cierre de cada fase.
- **Control de versiones** con Git: ramas `main` (producción) ← `develop` ← `feature/*`, Pull Requests y versionado semántico (SemVer).
- **Calidad** con pruebas automatizadas y criterios de aceptación por entregable.

---

## 4. Estructura de Desglose del Trabajo (EDT / WBS)

### F1 — Planificación
- Plan de Proyecto y Carta Gantt.
- Presupuesto detallado.
- Plan de gestión de riesgos.
- Plan de pruebas.

### F2 — Legal y fundacional
- Constitución de empresa (SpA) e inicio de actividades en el SII.
- Términos y Condiciones y Política de Privacidad.
- Registro de marca (INAPI).
- Alta y verificación de la cuenta de WhatsApp Business.

### F3 — Infraestructura
- Cuentas cloud y bases de datos administradas (una por servicio).
- Dominio, certificados SSL y correo.
- Gestión de secretos (sacar credenciales del código).
- Ambientes dev / staging / producción, CI/CD, monitoreo y respaldos.

### F4 — Desarrollo del producto
- **Backend:** endurecer seguridad (autenticación, secretos), integrar pagos reales (Webpay/Flow), integrar facturación electrónica (SII).
- **Frontend:** sitio web público (catálogo, ficha de producto, carrito, checkout, seguimiento, cuenta de usuario) y panel de administración del dueño.

### F5 — Integraciones
- Despacho con courier (crear envío, webhook/polling, mapeo de estados).
- Agente de atención por WhatsApp con IA (ms-chatbot: RAG, tools, derivación a persona).
- Notificaciones reales (email/SMS).
- (Opcional, Fase 2) Integración con Meta: Pixel/Conversions API y catálogo Instagram/Facebook Shopping.

### F6 — Pruebas y aseguramiento de calidad
- Pruebas unitarias, de integración y end-to-end.
- Pruebas conversacionales del agente de WhatsApp.
- Pruebas de carga/rendimiento y de seguridad.
- Aceptación de usuario (UAT) con el dueño.

### F7 — Lanzamiento
- Despliegue en producción.
- Carga inicial de catálogo y datos.
- Marketing inicial (según Plan de Marketing).
- Monitoreo y estabilización post-lanzamiento.

---

## 5. Carta Gantt (con fechas)

**Supuesto de ritmo:** desarrollador **solo**, dedicación **intermedia inclinada a full-time** (~6–7 h/día efectivas, con variabilidad). Inicio estimado: **semana del 7 de julio de 2026**. El cronograma incluye un margen para imprevistos. Las celdas marcadas (`██`) indican el período activo de cada fase.

| Fase | Jul 2026 | Ago 2026 | Sep 2026 | Oct 2026 |
|------|:--------:|:--------:|:--------:|:--------:|
| F1 Planificación | ██ | | | |
| F2 Legal y fundacional | ██ | ██ | | |
| F3 Infraestructura | ██ | ██ | | |
| F4 Desarrollo del producto | ██ | ██ | ██ | |
| F5 Integraciones | | ██ | ██ | |
| F6 Pruebas y QA | | | ██ | ██ |
| F7 Lanzamiento | | | | ██ |
| Estabilización / buffer | | | | ██ |

**Fechas clave:**

- 🟢 **MVP vendible** (catálogo + carrito + pago real + despacho básico): **fines de agosto de 2026** (~8 semanas desde el inicio).
- 🚀 **Lanzamiento completo** (con agente WhatsApp IA, facturación e integraciones): **mediados a fines de octubre de 2026** (~3,5 a 4 meses desde el inicio).

> Las fases se solapan a propósito (F2 legal corre en paralelo al desarrollo). Los trámites externos (SII, verificación de WhatsApp Business, constitución) deben **iniciarse la primera semana** para no bloquear el go-live. Si la dedicación baja de full-time, las fechas se corren proporcionalmente.

---

## 6. Hitos principales

| Hito | Descripción | Fase | Fecha estimada |
|------|-------------|------|----------------|
| H1 | Plan de proyecto, presupuesto y riesgos aprobados | F1 | Mediados de julio 2026 |
| H2 | Empresa constituida e inicio de actividades SII | F2 | Fines de julio 2026 |
| H3 | Infraestructura cloud y ambientes operativos | F3 | Principios de agosto 2026 |
| H4 | Pagos reales y facturación integrados | F4 | Mediados de agosto 2026 |
| H5 | Sitio web y panel operativos → **MVP vendible** | F4 | **Fines de agosto 2026** |
| H6 | Despacho con courier y agente de WhatsApp integrados | F5 | Mediados/fines de septiembre 2026 |
| H7 | Pruebas y UAT aprobadas | F6 | Principios de octubre 2026 |
| H8 | **Lanzamiento comercial completo** | F7 | **Mediados/fines de octubre 2026** |

---

## 7. Recursos y roles

| Rol | Responsabilidad |
|-----|-----------------|
| Patrocinador / Dueño | Aprueba, financia y valida (UAT) |
| Gerente de Proyecto | Planifica, coordina y controla el avance |
| Desarrollador(es) | Backend, frontend e integraciones |
| Diseñador / Marketing | UI del sitio, contenidos y campañas |
| Asesor legal | Constitución, contratos y cumplimiento |
| Contador / Asesor tributario | Facturación electrónica y SII |

---

## 8. Dependencias y ruta crítica

- **F2 (SII)** debe estar lista **antes** de activar pagos y facturación reales en producción (F4/F7).
- **F3 (infraestructura)** es prerrequisito del despliegue (F4 en adelante).
- **F4 (backend: pagos y seguridad)** debe preceder a las pruebas (F6).
- **F6 (pruebas y UAT)** debe cerrarse antes del **lanzamiento (F7)**.

**Ruta crítica:** F1 → F3 → F4 → F6 → F7 (con F2 en paralelo pero bloqueante para el go-live comercial).

---

## 9. Gestión de riesgos

Los riesgos de alto nivel y sus mitigaciones están en el Acta de Constitución (sección 9). En la fase F1 se elabora el Plan de Gestión de Riesgos detallado (matriz probabilidad × impacto y planes de contingencia por riesgo).

---

## 10. Supuestos y restricciones

- Disponibilidad de financiamiento y de los proveedores externos (pago, SII, cloud, courier, WhatsApp) para Chile.
- Operación inicial limitada a Chile.
- Alcance y plazos acotados por fases; el Mes 6 actúa como reserva.

---

## 11. Criterios de aceptación y entregables

- Cada fase cierra con sus entregables verificados (documentos, ambientes o funcionalidades).
- El proyecto se considera completo cuando la tienda opera en producción con pagos y facturación reales, despacho con seguimiento y atención por WhatsApp, y con las pruebas y UAT aprobadas.

---

## 12. Control de versiones y calidad

- **Git:** ramas `main` ← `develop` ← `feature/*`; `main` protegida (cambios vía Pull Request).
- **Versionado semántico (SemVer)** y `CHANGELOG` por release.
- **Calidad:** pruebas automatizadas por servicio y criterios de aceptación por entregable.
