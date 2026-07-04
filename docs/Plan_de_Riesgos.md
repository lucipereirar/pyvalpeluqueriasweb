# Plan de Gestión de Riesgos

## Pyval — Tienda Online de Productos de Belleza y Peluquería

| Campo | Detalle |
|-------|---------|
| **Proyecto** | Pyval |
| **Versión del documento** | 1.0 |
| **Fecha de emisión** | 04-07-2026 |
| **Autor** | Luciano Alonso Pereira Rodríguez |
| **Documentos relacionados** | Acta de Constitución (§9), Plan de Proyecto, Plan de Acción |

---

## 1. Objetivo

Identificar, evaluar y planificar la respuesta a los riesgos del proyecto Pyval, definiendo para cada uno su probabilidad, impacto, severidad, estrategia de mitigación, plan de contingencia, responsable y señal de alerta temprana.

## 2. Metodología

- **Probabilidad (P):** Baja (1) · Media (2) · Alta (3)
- **Impacto (I):** Bajo (1) · Medio (2) · Alto (3)
- **Severidad = P × I:** 1–2 Aceptable · 3–4 Vigilar · 6–9 Crítico (acción obligatoria)
- Revisión del registro: **quincenal** y ante cualquier hito (H1–H8).

## 3. Matriz de evaluación

| Sev. | Impacto Bajo (1) | Impacto Medio (2) | Impacto Alto (3) |
|------|:---:|:---:|:---:|
| **Prob. Alta (3)** | 3 Vigilar | 6 Crítico | 9 Crítico |
| **Prob. Media (2)** | 2 Aceptable | 4 Vigilar | 6 Crítico |
| **Prob. Baja (1)** | 1 Aceptable | 2 Aceptable | 3 Vigilar |

## 4. Registro de riesgos

| ID | Riesgo | Cat. | P | I | Sev. | Mitigación | Contingencia | Señal de alerta | Resp. |
|----|--------|------|:-:|:-:|:----:|------------|--------------|-----------------|-------|
| R1 | Dependencia de un único desarrollador (enfermedad, sobrecarga, abandono temporal) | Operativo | 3 | 3 | **9** | Documentación al día, control de versiones, checklists por tarjeta, ritmo sostenible | Congelar alcance al MVP; re-planificar fechas; apoyo externo puntual | Retraso >1 semana en un hito | GP |
| R2 | Baja adopción / pocas ventas tras el lanzamiento | Negocio | 2 | 3 | **6** | Plan de Marketing, propuesta de valor clara, MVP temprano para validar | Ajustar precios/canales; reforzar pauta; pivotear catálogo | Conversión < 0,5% tras 4 semanas | Dueño |
| R3 | Fuga o exposición de datos personales | Seguridad | 2 | 3 | **6** | Gestión de secretos, JWT en gateway, HTTPS, mínimos privilegios | Aislar sistema, rotar credenciales, notificar según Ley 21.719 | Credenciales en repo; accesos anómalos | Dev |
| R4 | Incumplimiento normativo (SII, consumidor, datos) | Legal | 2 | 3 | **6** | Asesoría contable/legal desde F2; checklist de cumplimiento por release | Suspender venta hasta regularizar; plan de remediación | Observaciones del SII; reclamo SERNAC | Dueño |
| R5 | Retraso en trámites externos (SII, verificación WhatsApp, INAPI) | Externo | 3 | 2 | **6** | Iniciar trámites en semana 1; seguimiento semanal | Lanzar MVP con boleta manual transitoria (si es legalmente viable); WhatsApp en fase 2 | Trámite sin avance en 2 semanas | Dueño |
| R6 | El agente de IA entrega información incorrecta (alucinación) | Técnico | 2 | 3 | **6** | Respuestas ancladas a APIs reales (tools), límites del prompt, derivación a humano | Desactivar agente y volver a atención manual; revisar prompt/base RAG | Quejas de clientes; respuestas sin fuente | Dev |
| R7 | Sobrecosto o retraso técnico del desarrollo | Gestión | 2 | 2 | **4** | Alcance por fases, buffer del mes 6, reserva 10–15% | Recortar alcance a MVP; posponer F5 opcionales | Desvío >20% en horas estimadas | GP |
| R8 | Falla o cambio de API de terceros (courier, pasarela, Meta) | Externo | 2 | 2 | **4** | Adaptadores desacoplados, polling de respaldo, idempotencia | Cambiar de proveedor (agregador facilita); operación manual temporal | Errores 5xx sostenidos del proveedor | Dev |
| R9 | Pérdida de datos (sin backups verificados) | Técnico | 1 | 3 | **3** | Backups automáticos diarios en cloud; prueba de restauración mensual | Restaurar último backup; comunicación a afectados | Backup fallido o no probado >30 días | Dev |
| R10 | Costos variables (pauta, LLM, mensajería) fuera de control | Financiero | 2 | 2 | **4** | Topes de consumo, monitoreo mensual, alertas de presupuesto | Pausar pauta; degradar agente a FAQs estáticas | Gasto mensual >120% de lo presupuestado | Dueño |
| R11 | Vulnerabilidad explotada en producción (inyección, XSS, fuerza bruta) | Seguridad | 2 | 3 | **6** | Validación de entradas, JWT, dependencias actualizadas, revisión de seguridad pre-lanzamiento | Parche de emergencia; rotación de secretos; análisis forense | Alertas del monitoreo; dependencias con CVE | Dev |
| R12 | Pérdida del entorno local de desarrollo (equipo único) | Operativo | 2 | 2 | **4** | Push frecuente al remoto GitHub; documentación de setup en README | Reconstruir entorno desde el repo; restaurar BD de respaldo | Trabajo sin push >3 días | Dev |

## 5. Riesgos aceptados

- **Latencia adicional por arquitectura de microservicios a pequeña escala** — aceptado por valor de aprendizaje/portafolio (decisión documentada).
- **Servicios internos confían en la red local (seguridad perimetral en el gateway)** — aceptado para esta fase; se re-evalúa al pasar a cloud.

## 6. Seguimiento

- El registro se revisa **quincenalmente** y al cierre de cada fase (F1–F7).
- Todo riesgo que se materialice se registra como incidente con causa raíz y acción correctiva.
- Los riesgos críticos (≥6) deben tener su mitigación **activa** antes del hito que afectan.
