import { api } from './client'

// Pedidos: microservicio ms-pedidos (calcula subtotal, IVA 19% y total).
export const pedidosApi = {
  crear: (usuarioId, items) =>
    api.post('/pedidos', { usuarioId, items }, { auth: true }),
  porUsuario: (usuarioId) => api.get(`/pedidos/usuario/${usuarioId}`, { auth: true }),
  porId: (id) => api.get(`/pedidos/${id}`, { auth: true }),
  cancelar: (id) => api.post(`/pedidos/${id}/cancelar`, null, { auth: true }),
  // Solo administración
  listarTodos: () => api.get('/pedidos', { auth: true }),
  actualizarEstado: (id, estado) =>
    api.patch(`/pedidos/${id}/estado?estado=${estado}`, null, { auth: true }),
}
