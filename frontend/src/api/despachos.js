import { api } from './client'

// Despachos: microservicio ms-despacho (tracking TRK-XXXX).
// La consulta por tracking es pública (el cliente puede rastrear sin sesión).
export const despachosApi = {
  porPedido: (pedidoId) => api.get(`/despachos/pedido/${pedidoId}`, { auth: true }),
  porTracking: (codigo) => api.get(`/despachos/tracking/${codigo}`),
  porUsuario: (usuarioId) => api.get(`/despachos/usuario/${usuarioId}`, { auth: true }),
  // Solo administración
  listarTodos: () => api.get('/despachos', { auth: true }),
  actualizarEstado: (id, estado) =>
    api.patch(`/despachos/${id}/estado?estado=${estado}`, null, { auth: true }),
}

export const ESTADOS_DESPACHO = ['PENDIENTE', 'EN_PREPARACION', 'EN_CAMINO', 'ENTREGADO', 'FALLIDO']
