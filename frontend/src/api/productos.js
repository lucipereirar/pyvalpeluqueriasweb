import { api } from './client'
import { productosDemo } from '../data/productosDemo'

// Catálogo de productos: proviene del microservicio ms-productos
// (GET /api/productos). Si el backend no está disponible (por ejemplo,
// durante una vista previa solo del frontend), se usa un catálogo demo
// para que la página siga siendo navegable.
export async function listarProductos() {
  try {
    const data = await api.get('/productos')
    if (Array.isArray(data) && data.length) return { productos: data, demo: false }
    return { productos: productosDemo, demo: true }
  } catch (e) {
    return { productos: productosDemo, demo: true }
  }
}

export async function buscarProductos(nombre) {
  return api.get(`/productos/buscar?nombre=${encodeURIComponent(nombre)}`)
}

export async function productosPorCategoria(categoria) {
  return api.get(`/productos/categoria/${categoria}`)
}

// --- Administración (rol ADMIN vía gateway) ---
export const productosAdmin = {
  listarTodos: () => api.get('/productos/todos'),
  crear: (producto) => api.post('/productos', producto, { auth: true }),
  actualizar: (id, producto) => api.put(`/productos/${id}`, producto, { auth: true }),
  eliminar: (id) => api.del(`/productos/${id}`, { auth: true }),
}

export const CATEGORIAS = [
  'CUIDADO_CABELLO', 'CUIDADO_PIEL', 'COLORACION', 'HERRAMIENTAS', 'ACCESORIOS', 'OTROS',
]
