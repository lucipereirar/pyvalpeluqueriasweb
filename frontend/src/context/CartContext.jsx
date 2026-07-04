import { createContext, useContext, useState, useEffect, useCallback } from 'react'
import { carritoApi } from '../api/carrito'
import { useAuth } from './AuthContext'

const CartContext = createContext(null)

// El carrito se apoya en el microservicio ms-carrito cuando el usuario
// está autenticado (best-effort). La UI mantiene siempre una copia local
// en localStorage para que el carrito funcione de inmediato y sobreviva
// aunque el backend no esté disponible durante una vista previa.
function leerCarrito() {
  try {
    const raw = localStorage.getItem('pyval_carrito')
    return raw ? JSON.parse(raw) : []
  } catch {
    return []
  }
}

export function CartProvider({ children }) {
  const { usuario } = useAuth()
  const [items, setItems] = useState(leerCarrito) // [{ producto, cantidad }]
  const [abierto, setAbierto] = useState(false)

  useEffect(() => {
    localStorage.setItem('pyval_carrito', JSON.stringify(items))
  }, [items])

  const sincronizarBackend = useCallback((productoId, cantidad) => {
    if (!usuario) return
    carritoApi.agregarItem(usuario.id, productoId, cantidad).catch(() => {})
  }, [usuario])

  const agregar = useCallback((producto, cantidad = 1) => {
    setItems((prev) => {
      const idx = prev.findIndex((i) => i.producto.id === producto.id)
      if (idx >= 0) {
        const copia = [...prev]
        copia[idx] = { ...copia[idx], cantidad: copia[idx].cantidad + cantidad }
        return copia
      }
      return [...prev, { producto, cantidad }]
    })
    sincronizarBackend(producto.id, cantidad)
    setAbierto(true)
  }, [sincronizarBackend])

  const cambiarCantidad = useCallback((productoId, cantidad) => {
    setItems((prev) =>
      prev
        .map((i) => (i.producto.id === productoId ? { ...i, cantidad } : i))
        .filter((i) => i.cantidad > 0)
    )
  }, [])

  const quitar = useCallback((productoId) => {
    setItems((prev) => prev.filter((i) => i.producto.id !== productoId))
  }, [])

  const vaciar = useCallback(() => {
    setItems([])
    if (usuario) carritoApi.vaciar(usuario.id).catch(() => {})
  }, [usuario])

  const total = items.reduce((acc, i) => acc + Number(i.producto.precio) * i.cantidad, 0)
  const cantidadTotal = items.reduce((acc, i) => acc + i.cantidad, 0)

  return (
    <CartContext.Provider
      value={{
        items, total, cantidadTotal,
        agregar, cambiarCantidad, quitar, vaciar,
        abierto, setAbierto,
      }}
    >
      {children}
    </CartContext.Provider>
  )
}

export function useCart() {
  const ctx = useContext(CartContext)
  if (!ctx) throw new Error('useCart debe usarse dentro de CartProvider')
  return ctx
}
