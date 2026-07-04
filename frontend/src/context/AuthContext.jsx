import { createContext, useContext, useState, useCallback } from 'react'
import { authApi } from '../api/auth'

const AuthContext = createContext(null)

function leerUsuario() {
  try {
    const raw = localStorage.getItem('pyval_usuario')
    return raw ? JSON.parse(raw) : null
  } catch {
    return null
  }
}

export function AuthProvider({ children }) {
  const [usuario, setUsuario] = useState(leerUsuario)

  const login = useCallback(async (email, password) => {
    const data = await authApi.login(email, password)
    const u = { id: data.id, email: data.email, rol: data.rol }
    localStorage.setItem('pyval_token', data.token)
    localStorage.setItem('pyval_usuario', JSON.stringify(u))
    setUsuario(u)
    return u
  }, [])

  const registrar = useCallback(async (datos) => {
    await authApi.register(datos)
    // Tras registrarse, iniciamos sesión automáticamente.
    return login(datos.email, datos.password)
  }, [login])

  const logout = useCallback(() => {
    localStorage.removeItem('pyval_token')
    localStorage.removeItem('pyval_usuario')
    setUsuario(null)
  }, [])

  return (
    <AuthContext.Provider value={{ usuario, login, registrar, logout, autenticado: !!usuario }}>
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth debe usarse dentro de AuthProvider')
  return ctx
}
