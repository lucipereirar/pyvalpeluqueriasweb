import { api } from './client'

// Carrito: microservicio ms-carrito (rutas /api/carrito/{usuarioId}/...).
export const carritoApi = {
  obtener: (usuarioId) => api.get(`/carrito/${usuarioId}`, { auth: true }),
  agregarItem: (usuarioId, productoId, cantidad) =>
    api.post(`/carrito/${usuarioId}/items`, { productoId, cantidad }, { auth: true }),
  actualizarCantidad: (usuarioId, itemId, cantidad) =>
    api.put(`/carrito/${usuarioId}/items/${itemId}?cantidad=${cantidad}`, null, { auth: true }),
  eliminarItem: (usuarioId, itemId) =>
    api.del(`/carrito/${usuarioId}/items/${itemId}`, { auth: true }),
  vaciar: (usuarioId) => api.del(`/carrito/${usuarioId}/vaciar`, { auth: true }),
  procesar: (usuarioId) => api.post(`/carrito/${usuarioId}/procesar`, null, { auth: true }),
}
