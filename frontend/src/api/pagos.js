import { api } from './client'

// Pagos: microservicio ms-pago. Al aprobarse un pago se dispara la
// coreografía: notificación al usuario + creación del despacho.
// NOTA: hoy el procesamiento es simulado; la integración con una pasarela
// real (Webpay/Flow) está planificada y requiere cuenta de comercio.
export const pagosApi = {
  procesar: ({ pedidoId, usuarioId, monto, metodoPago, direccion, ciudad, region, codigoPostal }) =>
    api.post('/pagos', { pedidoId, usuarioId, monto, metodoPago, direccion, ciudad, region, codigoPostal }, { auth: true }),
  porPedido: (pedidoId) => api.get(`/pagos/pedido/${pedidoId}`, { auth: true }),
  porUsuario: (usuarioId) => api.get(`/pagos/usuario/${usuarioId}`, { auth: true }),
  // Solo administración
  listarTodos: () => api.get('/pagos', { auth: true }),
  reembolsar: (id) => api.post(`/pagos/${id}/reembolsar`, null, { auth: true }),
}

export const METODOS_PAGO = [
  { valor: 'WEBPAY', texto: 'Webpay (tarjetas)' },
  { valor: 'TARJETA_CREDITO', texto: 'Tarjeta de crédito' },
  { valor: 'TARJETA_DEBITO', texto: 'Tarjeta de débito' },
  { valor: 'TRANSFERENCIA', texto: 'Transferencia bancaria' },
]
