# Propuesta de Solución

## Pyval — Tienda Online de Productos de Belleza y Peluquería

| Campo | Detalle |
|-------|---------|
| **Proyecto** | Pyval |
| **Versión del documento** | 2.0 |
| **Fecha de emisión** | 01-07-2026 |
| **Autor** | Luciano Alonso Pereira Rodríguez |
| **Dirigido a** | [COMPLETAR: patrocinador / dueño del negocio] |

---

## 1. Resumen ejecutivo

Se propone construir **Pyval**, una **tienda online de un solo dueño** para vender productos de belleza y peluquería directamente al consumidor final en Chile. La solución convierte la venta informal actual (por ejemplo, WhatsApp atendido a mano) en un canal digital profesional, seguro y automatizado.

Se apoya en una base técnica ya construida —un backend de **microservicios (Spring Boot + Spring Cloud)** orientado a tienda de un solo vendedor— y la evoluciona a un producto vendible incorporando un sitio web, pagos reales, facturación electrónica, despacho con seguimiento, despliegue en la nube y un diferenciador clave: un **agente de atención al cliente por WhatsApp potenciado por IA**.

---

## 2. Contexto y problema

El negocio vende hoy por canales informales y no da abasto para **atender los mensajes de WhatsApp** de sus clientes, perdiendo ventas por falta de respuesta oportuna. Además, los procesos de pago, despacho y reportería son manuales.

**Problemas que aborda la solución:**

- Falta de un canal de venta online propio y profesional.
- Atención al cliente por WhatsApp saturada por falta de personal.
- Procesos manuales de pago, despacho y reportería.

---

## 3. Objetivos de la solución

1. Ofrecer una tienda online funcional, segura y legalmente habilitada para operar en Chile.
2. Automatizar el flujo de compra de extremo a extremo (catálogo → pago → despacho → notificación).
3. Integrar el despacho con una empresa de transporte, con seguimiento hasta la entrega.
4. **Automatizar la atención al cliente por WhatsApp** con un agente de IA, con derivación a una persona.
5. Entregar analítica de ventas útil para el dueño del negocio.

---

## 4. Descripción de la solución

Pyval es una tienda de comercio electrónico **de un solo vendedor (el dueño)**, compuesta por:

- **Backend de microservicios** (base existente): autenticación, catálogo, carrito, pedidos, pagos, despacho, notificaciones y analítica de ventas.
- **Sitio web público (storefront)** para consumidores y **panel de administración** para el dueño.
- **Agente de atención por WhatsApp con IA** (microservicio `ms-chatbot`).
- **Integraciones externas**: pasarela de pago, facturación electrónica (SII), courier de despacho, email/SMS y WhatsApp Business Platform.

---

## 5. Arquitectura de la solución

Arquitectura de microservicios con descubrimiento de servicios (Eureka) y puerta de enlace (API Gateway):

```
Consumidor (Web)        Administrador (Panel)      WhatsApp (Cliente)
       │                        │                        │
       ▼                        ▼                        ▼
                     [ API Gateway :8080 ]          [ Meta Cloud API ]
                             │                            │ (webhook)
   ┌─────────────┬──────────┼───────────┬──────────────┐│
   ▼             ▼          ▼           ▼              ▼▼
 ms-auth   ms-productos  ms-carrito  ms-pedidos     ms-chatbot (nuevo)
                                     ms-pago            │  └─► Agente IA (LLM) + conocimiento (RAG)
                                     ms-despacho ──► Courier (API + webhooks)
                                     ms-notificaciones
                                     ms-reportes

 [ Eureka :8761 ]   [ Bases de datos administradas en cloud, una por servicio ]
```

---

## 6. Módulos funcionales

### 6.1 Catálogo y productos

Gestión del catálogo del dueño desde el panel de administración: alta y edición de productos, categorías, precios y stock; búsqueda y filtros para el consumidor.

### 6.2 Carrito y pedidos

Carrito por usuario y creación de pedidos con cálculo de subtotal, IVA (19%) y total.

### 6.3 Pagos, despacho y notificaciones

Procesamiento de pagos con pasarela real, generación automática de despacho con seguimiento y notificaciones al cliente por email/SMS. El flujo opera como coreografía: al aprobarse el pago se notifica al cliente y se genera el despacho. El detalle del seguimiento se describe en el módulo 6.6.

### 6.4 Analítica de ventas

Indicadores para el dueño (total de ventas, ticket promedio, ventas por método de pago, pedidos por estado, productos más vendidos) y exportación a Excel.

### 6.5 Agente de atención al cliente por WhatsApp (IA)

**Objetivo:** responder automáticamente los mensajes de los clientes, comprendiendo consultas en lenguaje natural con información real del negocio, y derivando a una persona cuando corresponda.

**Cómo funciona:**

- El cliente escribe por WhatsApp; Meta entrega el mensaje al microservicio `ms-chatbot` mediante un *webhook*.
- El agente, potenciado por un **modelo de lenguaje (LLM)**, interpreta la solicitud usando un **prompt de reglas del negocio**, una **base de conocimiento (RAG)** con preguntas frecuentes y **herramientas (tools)** que consultan los servicios reales (`ms-productos`, `ms-pedidos`) para responder con datos verídicos (precio, stock, estado de pedido).
- Si no puede resolver, **deriva la conversación a una persona**.

**Puntos clave:** uso del **canal oficial** (WhatsApp Business Platform), respuestas **ancladas a datos reales** para evitar información incorrecta, sin *fine-tuning* del modelo, y respeto de la **ventana de atención de 24 horas**.

### 6.6 Despacho, seguimiento y logística externalizada

**Enfoque:** el negocio **no opera flota propia**; el transporte se externaliza a una empresa de despacho (courier) o a un **agregador logístico**. `ms-despacho` **orquesta y refleja** el estado que reporta el courier, en lugar de originarlo.

**Flujo:**

1. Al aprobarse el pago se genera el despacho.
2. `ms-despacho` **crea el envío en el courier** vía su API y obtiene el **número de seguimiento (tracking)** y la **etiqueta/guía**.
3. Se **sincroniza** con el estado del courier mediante **webhooks** (tiempo real, preferido) o **polling** (consulta periódica, respaldo).
4. Los estados del courier se **mapean** a estados de negocio (*En preparación → En camino → Entregado → Fallido*) y cada cambio dispara una **notificación** al cliente, visible además en "Mis pedidos".

**Consideraciones:**

- **Fuente de verdad:** el courier; `ms-despacho` es un espejo sincronizado.
- **Idempotencia** ante webhooks duplicados o desordenados (se procesan por *tracking* + marca de tiempo).
- **Un despacho por pedido:** al ser tienda de un solo vendedor, cada pedido se despacha desde un único origen (sin división por vendedores).
- **Integración configurable:** courier o agregador según conveniencia (en Chile, agregadores como Shipit o Envíame; couriers como Chilexpress, Starken o Blue Express).
- **Casos de borde:** envío fallido, dirección incorrecta, reintentos y devoluciones.

### 6.7 Sitio web y panel de administración

- **Sitio público (storefront):** portada, catálogo navegable con búsqueda y filtros, fichas de producto, carrito, checkout, seguimiento de pedidos y cuenta de usuario.
- **Panel de administración (dueño):** gestión de productos, precios y stock, visualización de pedidos y despachos, y analítica de ventas.

**Características transversales:** diseño **responsivo** (móvil y escritorio), criterios de **accesibilidad**, optimización para buscadores (**SEO**) y consumo de la API a través del *gateway*, con autenticación gestionada por `ms-auth`.

### 6.8 Integración con Meta (marketing y canal social) — opcional (Fase 2)

Integración **técnica** que potencia la publicidad y suma Instagram/Facebook como vitrina. **No incluye la gestión de campañas** (eso se aborda en el Plan de Marketing, documento aparte); aquí solo lo que se construye en el producto:

- **Meta Pixel / Conversions API:** registra eventos del sitio (visita, agregar al carrito, compra) para medir la efectividad de las campañas y optimizar la inversión publicitaria.
- **Catálogo en Instagram/Facebook Shopping:** sincroniza el catálogo de la tienda con Meta para que los productos se muestren y enlacen desde el perfil de Instagram y Facebook.

Se marca como **Fase 2 / opcional**: no es imprescindible para el MVP, pero tiene alto retorno para la captación de clientes.

---

## 7. Tecnologías

| Capa | Tecnología |
|------|------------|
| Backend | Java 21, Spring Boot 3, Spring Cloud (Eureka, Gateway, OpenFeign) |
| Datos | MySQL administrado en cloud (una BD por servicio) |
| Frontend | Framework web moderno (React / Angular / Vue) |
| IA / Chatbot | Modelo de lenguaje (LLM, p. ej. Claude) + RAG |
| Mensajería | WhatsApp Business Platform (Cloud API) |
| Pagos | Transbank Webpay / Flow / Mercado Pago |
| Facturación | Integración SII (facturador electrónico) |
| Logística | Courier o agregador logístico vía API + webhooks |
| Marketing / Meta (opcional) | Meta Pixel / Conversions API + catálogo Instagram/Facebook Shopping |
| Infraestructura | Cloud (AWS / GCP / Azure), contenedores, CI/CD |

---

## 8. Integraciones externas

- **Pasarela de pago** (PCI-DSS delegado al proveedor; no se almacenan datos de tarjeta).
- **Facturación electrónica** con el SII (boleta/factura).
- **Empresa de despacho (courier) o agregador logístico** para generar envíos y recibir el estado de seguimiento (webhook/polling).
- **WhatsApp Business Platform** para el agente de atención.
- **Email/SMS** (SendGrid/SES, Twilio) para notificaciones reales.
- **Meta (opcional, Fase 2):** Pixel/Conversions API para medición de conversiones y sincronización de catálogo con Instagram/Facebook Shopping.

---

## 9. Modelo de negocio

Ingresos por **venta directa de productos** (margen comercial). Al ser una tienda de un solo dueño, no existen comisiones a terceros ni liquidaciones a vendedores.

---

## 10. Seguridad y cumplimiento (Chile)

- Gestión de secretos fuera del código; HTTPS en todos los canales.
- Cumplimiento de protección de datos (Ley 19.628 y Ley 21.719), incluido el tratamiento de las conversaciones de WhatsApp.
- Cumplimiento de la Ley del Consumidor 19.496 para comercio electrónico.
- Pagos mediante proveedor certificado (sin almacenar datos de tarjetas).

---

## 11. Plan de pruebas (alto nivel)

- **Unitarias** (JUnit 5 + Mockito) por servicio.
- **Integración** entre servicios (flujo pago → despacho → notificación).
- **Integración con logística:** creación de envío y recepción de estados del courier (webhook y polling), incluyendo idempotencia.
- **End-to-end** del flujo de compra.
- **Conversacional:** batería de mensajes reales para validar el agente de WhatsApp y su derivación a una persona.
- **Carga/rendimiento** y **seguridad** previo a producción.
- **Aceptación (UAT)** con el dueño del negocio.

---

## 12. Supuestos, riesgos y dependencias

- Disponibilidad de proveedores externos para Chile (pago, facturación, cloud, courier, WhatsApp).
- Verificación de la cuenta de WhatsApp Business con Meta y un número dedicado.
- Riesgo de información incorrecta del agente → mitigado con anclaje a datos reales y derivación a una persona.
- Dependencia de la API/webhooks del courier → mitigada con polling de respaldo e idempotencia.

---

## 13. Entregables

- Tienda online desplegada (backend, sitio web y panel de administración).
- Agente de atención por WhatsApp integrado.
- Despacho integrado con courier y seguimiento operativo.
- Documentación de proyecto, legal y técnica.
- Ambientes dev / staging / producción y pipeline CI/CD.

---

## 14. Próximos pasos

1. Aprobación de esta propuesta y del Acta de Constitución.
2. Elaboración del Plan de Proyecto, Carta Gantt y presupuesto detallado.
3. Trámites legales y de habilitación (empresa, SII, WhatsApp Business).
4. Desarrollo por fases y lanzamiento.
