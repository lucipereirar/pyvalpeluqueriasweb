# Acta de Constitución del Proyecto

## Pyval — Tienda Online de Productos de Belleza y Peluquería

| Campo | Detalle |
|-------|---------|
| **Nombre del proyecto** | Pyval |
| **Versión del documento** | 2.1 (hitos con fechas) |
| **Fecha de emisión** | 01-07-2026 |
| **Patrocinador (Sponsor)** | [COMPLETAR: nombre del patrocinador / dueño del negocio] |
| **Gerente de Proyecto** | Luciano Alonso Pereira Rodríguez |
| **Estado** | Propuesto |

---

## 1. Propósito y justificación del proyecto

El negocio necesita un **canal de venta online propio, profesional y seguro** para comercializar sus productos de belleza y peluquería directamente al consumidor final. Hoy la venta depende de canales informales (por ejemplo, mensajes de WhatsApp atendidos manualmente), lo que limita las ventas, satura la atención y no escala.

**Pyval** resuelve esto con una tienda en línea de un solo dueño: un sitio web donde el negocio publica su catálogo y los clientes compran, pagan y reciben sus productos, con toda la operación (pago, despacho, notificaciones y reportes) automatizada. El proyecto aprovecha una base técnica ya desarrollada: un backend de **microservicios (Spring Boot + Spring Cloud)**.

---

## 2. Descripción del proyecto y del producto

El producto es una **tienda de comercio electrónico de un solo vendedor (el dueño del negocio)**, compuesta por:

- Un **backend de microservicios** que gestiona usuarios, catálogo, carrito, pedidos, pagos, despacho, notificaciones y analítica de ventas.
- Un **sitio web público (storefront)** para consumidores —catálogo, fichas de producto, búsqueda, carrito, compra (checkout), seguimiento de pedidos y cuenta de usuario.
- Un **panel de administración** para el dueño (gestión de productos, stock, precios, pedidos, despachos y reportes de venta).
- Un **agente de atención al cliente por WhatsApp**, potenciado por IA (LLM), que responde preguntas frecuentes, informa estado de pedidos y disponibilidad con datos reales, y deriva a una persona cuando corresponde.
- Integraciones con **pasarela de pago real** (Transbank Webpay / Flow), **facturación electrónica (SII)**, **empresa de despacho (courier)** y **notificaciones por email/SMS**.

---

## 3. Objetivos del proyecto

**Objetivo general:** Lanzar al mercado una tienda online funcional, segura y legalmente habilitada para operar en Chile, que permita al negocio vender sus productos directamente al consumidor final.

**Objetivos específicos (SMART):**

1. Publicar un sitio web con catálogo, carrito y proceso de compra completo.
2. Integrar una pasarela de pago real y la emisión de boleta/factura electrónica.
3. Integrar el despacho con una empresa de transporte, con seguimiento del estado hasta la entrega.
4. Habilitar la atención al cliente por WhatsApp con un agente de IA y derivación a una persona.
5. Desplegar la solución en infraestructura cloud con bases de datos administradas, respaldos y monitoreo.
6. Contar con la documentación legal exigida (constitución, T&C, política de privacidad).

---

## 4. Alcance de alto nivel

**Incluye:**

- Sitio web público (storefront) y panel de administración del dueño.
- Flujo completo de compra: catálogo → carrito → pedido → pago → despacho → notificación.
- Integración de pago, facturación electrónica y despacho con courier (con seguimiento).
- Agente de atención por WhatsApp con IA y derivación a una persona.
- Analítica de ventas para el dueño (incluida exportación a Excel).
- Despliegue en cloud y documentación legal y de proyecto.
- (Opcional, Fase 2) Integración técnica con Meta: Pixel/Conversions API y catálogo en Instagram/Facebook Shopping.

**Excluye (fuera de alcance en esta fase):**

- Modelo marketplace / multi-vendedor (la tienda tiene un único dueño vendedor).
- Aplicación móvil nativa (se prioriza web responsiva).
- Logística/despacho propio (se integra con un courier externo; no se opera flota).
- Expansión internacional (foco inicial: Chile).
- Programas avanzados de fidelización e inteligencia artificial de recomendación.
- Gestión de campañas publicitarias y redes sociales (pauta en Meta Ads, contenidos de Instagram): se aborda en el Plan de Marketing, documento aparte.

---

## 5. Requisitos de alto nivel

- El sitio web debe ser **responsivo** (móvil y escritorio), accesible y optimizado para buscadores (SEO).
- Los pagos deben procesarse mediante un proveedor certificado (PCI-DSS) sin almacenar datos de tarjetas.
- El sistema debe cumplir la normativa chilena de protección de datos y de comercio electrónico.
- La disponibilidad objetivo del servicio en producción debe ser de al menos 99,5%.
- Toda venta debe generar el documento tributario correspondiente (boleta/factura electrónica).
- El despacho debe sincronizar su estado con el courier e informar al cliente hasta la entrega.
- La atención por WhatsApp debe usar el canal oficial (WhatsApp Business Platform), responder con datos reales (precio, stock, estado de pedido) y escalar a una persona cuando no pueda resolver.

---

## 6. Interesados (Stakeholders)

| Interesado | Rol / Interés |
|------------|---------------|
| Patrocinador / Dueño del negocio | Financia el proyecto y opera la tienda; espera retorno |
| Gerente de Proyecto | Responsable de la ejecución y entregables |
| Equipo de desarrollo | Construye y mantiene la plataforma |
| Consumidores finales | Compran los productos en la tienda |
| Proveedor de pagos (Transbank/Flow) | Procesa transacciones |
| Empresa de despacho (courier) | Transporta y entrega los productos |
| Proveedor WhatsApp Business (Meta) / IA (LLM) | Canal de mensajería y motor del agente de atención |
| Contador / Asesor tributario | Facturación y cumplimiento SII |
| Asesor legal | Constitución, contratos y cumplimiento normativo |

---

## 7. Hitos principales

| Hito | Descripción | Fecha estimada |
|------|-------------|----------------|
| H1 | Aprobación del Acta y definición del modelo de negocio | Principios de julio 2026 |
| H2 | Plan de proyecto, presupuesto y plan de riesgos aprobados | Mediados de julio 2026 |
| H3 | Constitución de empresa e inicio de actividades SII | Fines de julio 2026 |
| H4 | Pagos reales e integración de despacho con courier | Mediados de agosto (pagos) – mediados de septiembre (courier) 2026 |
| H5 | Sitio web (storefront) y panel de administración operativos | Fines de agosto 2026 (MVP vendible) |
| H6 | Agente de atención por WhatsApp integrado | Fines de septiembre 2026 |
| H7 | Despliegue en cloud (staging + producción) | Staging: agosto · Producción: octubre 2026 |
| H8 | Lanzamiento comercial | Mediados/fines de octubre 2026 |

> Fechas estimadas según la Carta Gantt del Plan de Proyecto (inicio: semana del 7 de julio de 2026, dedicación intermedia inclinada a full-time). Ajustar si cambia la fecha de inicio o el ritmo de trabajo.

---

## 8. Presupuesto estimado (alto nivel)

| Categoría | Estimación inicial |
|-----------|--------------------|
| Infraestructura cloud (compute + BD administrada) | [COMPLETAR: USD/mes] |
| Dominio, certificados y correo | [COMPLETAR] |
| Constitución de empresa y contabilidad | [COMPLETAR] |
| Asesoría legal (contratos, privacidad) | [COMPLETAR] |
| Desarrollo (horas equipo) | [COMPLETAR] |
| Marketing y adquisición inicial | [COMPLETAR] |
| Canal WhatsApp Business (Meta) | [COMPLETAR] |
| Uso de IA / LLM (agente conversacional) | [COMPLETAR] |
| Comisión pasarela de pago | ~1,5% – 3,5% por transacción |
| **Reserva de contingencia** | 10% – 15% del total |

> El presupuesto detallado se desarrollará como documento independiente en la fase de planificación.

---

## 9. Riesgos de alto nivel

| Riesgo | Impacto | Estrategia inicial |
|--------|---------|--------------------|
| Baja adopción / pocas ventas | Alto | Propuesta de valor clara, marketing y plan de captación |
| Incumplimiento normativo (datos, tributario) | Alto | Asesoría legal y contable desde el inicio |
| Fallas de seguridad o fuga de datos | Alto | Gestión de secretos, pagos delegados a proveedor PCI, auditorías |
| Sobrecosto o retraso técnico | Medio | Alcance por fases, reserva de contingencia |
| El agente de IA entrega información incorrecta (alucinación) | Alto | Respuestas ancladas a datos reales vía API, límites claros y derivación a una persona |
| Dependencia de la API/webhooks del courier | Medio | Polling de respaldo e idempotencia en el procesamiento de estados |
| Dependencia de un único desarrollador | Medio | Documentación, control de versiones y buenas prácticas |

---

## 10. Supuestos y restricciones

**Supuestos:**

- Existe una base técnica funcional de microservicios reutilizable, ya orientada a tienda de un solo vendedor.
- El patrocinador dispone del financiamiento para las fases planificadas.
- Los proveedores externos (pago, facturación, cloud, despacho, mensajería) están disponibles para Chile.
- El negocio puede habilitar un número dedicado y verificar la cuenta de WhatsApp Business con Meta.

**Restricciones:**

- La operación inicial se limita al territorio chileno.
- El presupuesto y los plazos serán acotados por fases.
- Debe cumplirse la legislación chilena vigente en cada release.

---

## 11. Criterios de éxito

- Flujo de compra completo operando en producción con pagos y facturación reales.
- Despacho integrado con courier y seguimiento visible para el cliente.
- Cumplimiento legal y tributario verificado.
- Disponibilidad ≥ 99,5% durante el lanzamiento.
- Satisfacción de los clientes sobre el umbral definido.

---

## 12. Autorización

La firma de este documento autoriza formalmente el inicio del proyecto y otorga al Gerente de Proyecto la autoridad para asignar recursos según lo planificado.

| Rol | Nombre | Firma | Fecha |
|-----|--------|-------|-------|
| Patrocinador / Dueño | [COMPLETAR] | | |
| Gerente de Proyecto | Luciano Alonso Pereira Rodríguez | | |
