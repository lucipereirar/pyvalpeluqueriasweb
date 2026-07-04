import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

// Protege rutas que requieren sesión iniciada.
export function RequiereSesion({ children }) {
  const { usuario } = useAuth()
  const location = useLocation()
  if (!usuario) return <Navigate to="/ingresar" state={{ desde: location.pathname }} replace />
  return children
}

// Protege rutas exclusivas del administrador (rol ADMIN).
export function RequiereAdmin({ children }) {
  const { usuario } = useAuth()
  if (!usuario) return <Navigate to="/ingresar" replace />
  if (usuario.rol !== 'ADMIN') return <Navigate to="/" replace />
  return children
}
