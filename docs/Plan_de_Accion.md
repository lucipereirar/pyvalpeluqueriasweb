# Plan de Acción

## Pyval — Tienda Online de Productos de Belleza y Peluquería

| Campo | Detalle |
|-------|---------|
| **Proyecto** | Pyval |
| **Versión del documento** | 1.0 |
| **Fecha de emisión** | 04-07-2026 |
| **Autor** | Luciano Alonso Pereira Rodríguez |
| **Documentos relacionados** | Plan de Proyecto (Gantt), Plan de Riesgos, tablero Notion/Trello |

---

## 1. Objetivo

Traducir el Plan de Proyecto en **acciones concretas, con responsable, plazo y criterio de término**, priorizadas para llegar primero al **MVP vendible** (fines de agosto 2026) y luego al **lanzamiento completo** (mediados/fines de octubre 2026).

## 2. Principios de ejecución

1. **Los trámites externos parten primero** (SII, WhatsApp, constitución): no se aceleran con código.
2. **MVP antes que perfección:** catálogo + carrito + pago + despacho básico venden; el resto suma después.
3. **Todo cambio se commitea y pushea** el mismo día (mitiga R12).
4. Cada acción cerrada se refleja en el tablero (Notion/Trello).

## 3. Acciones inmediatas (semana del 7 de julio 2026)

| # | Acción | Resp. | Plazo | Criterio de término |
|---|--------|-------|-------|---------------------|
| 1 | Commit + push de código y documentación al repositorio | Dev | Día 1 | Repo remoto al día |
| 2 | Iniciar constitución de empresa (Empresa en un Día) | Dueño | Día 2 | Solicitud enviada |
| 3 | Iniciar verificación WhatsApp Business (número dedicado) | Dueño | Día 3 | Solicitud a Meta enviada |
| 4 | Cerrar documentación F1 (riesgos, pruebas, acción) | Dev | Día 2 | Docs en Drive y repo |
| 5 | Seguridad del código: secretos por variables de entorno + JWT en gateway | Dev | Día 5 | Rutas protegidas; sin credenciales planas |

## 4. Acciones por fase (siguientes)

### F3 — Infraestructura *(pendiente: requiere contratación cloud — pospuesto por decisión)*
- Contratar cloud, registrar dominio .cl, ambientes y CI/CD → **EN ESPERA** hasta decisión de inversión.

### F4 — Desarrollo (en curso, local)
| Acción | Resp. | Criterio de término |
|--------|-------|---------------------|
| Frontend storefront (catálogo, ficha, carrito, checkout, pedidos, cuenta) | Dev | Compra completa funcionando contra el gateway |
| Panel de administración (productos, pedidos, despachos, reportes) | Dev | CRUD y reportes operativos con rol ADMIN |
| Pasarela de pago real (Webpay integración) | Dev | **PENDIENTE** — requiere cuenta de comercio; mientras, pago simulado |
| Facturación electrónica SII | Dev + Contador | **PENDIENTE** — requiere inicio de actividades |

### F5 — Integraciones
| Acción | Resp. | Criterio de término |
|--------|-------|---------------------|
| Despacho con courier (Shipit/Envíame) | Dev | **PENDIENTE** — requiere cuenta con el proveedor |
| Agente WhatsApp con IA (ms-chatbot) | Dev | **PENDIENTE** — requiere verificación Meta + API key LLM (pago) |
| Notificaciones email/SMS reales | Dev | **PENDIENTE** — requiere cuenta SendGrid/Twilio |

### F6 — Pruebas
| Acción | Resp. | Criterio de término |
|--------|-------|---------------------|
| Ejecutar Plan de Pruebas (unitarias→E2E) | Dev | Criterios de salida del Plan de Pruebas |
| UAT con el dueño | Dueño | Acta de aceptación firmada |

### F7 — Lanzamiento
| Acción | Resp. | Criterio de término |
|--------|-------|---------------------|
| Despliegue en producción (cloud) | Dev | **EN ESPERA** de F3 |
| Carga de catálogo real + campañas | Dueño | Tienda pública vendiendo |

## 5. Dependencias críticas y bloqueos declarados

| Bloqueo | Qué desbloquea | Acción para destrabar |
|---------|----------------|------------------------|
| Inicio de actividades SII | Facturación electrónica, venta legal | Trámite del dueño (F2) |
| Cuenta de comercio Transbank/Flow | Pago real | Contratación tras constituir empresa |
| Verificación Meta WhatsApp | Agente de atención + avisos WhatsApp | Trámite del dueño (F2) |
| API key LLM (pago) | Inteligencia del agente | Decisión de gasto (~USD 15/mes) |
| Contratación cloud | Producción, staging, CI/CD completo | Decisión de inversión (~USD 40/mes) |

## 6. Seguimiento

- Revisión **semanal** de este plan contra el Gantt (desvíos >1 semana activan re-planificación).
- El tablero Notion/Trello es la fuente operativa del día a día; este documento fija los compromisos y bloqueos.
