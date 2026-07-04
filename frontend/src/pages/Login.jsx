import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Auth.css'

export default function Login() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ email: '', password: '' })
  const [error, setError] = useState('')
  const [cargando, setCargando] = useState(false)

  const cambiar = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const enviar = async (e) => {
    e.preventDefault()
    setError('')
    setCargando(true)
    try {
      await login(form.email, form.password)
      navigate('/tienda')
    } catch (err) {
      setError(err.message || 'No pudimos iniciar sesión. Revisa tus datos.')
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className="auth">
      <div className="auth__panel">
        <span className="eyebrow">Bienvenida de vuelta</span>
        <h1 className="auth__titulo">Ingresa a tu cuenta</h1>
        <p className="auth__sub">Accede para gestionar tu carrito y tus compras.</p>

        <form onSubmit={enviar} className="auth__form">
          {error && <div className="auth__error">{error}</div>}
          <div className="campo">
            <label>Correo</label>
            <input type="email" name="email" value={form.email} onChange={cambiar} placeholder="nombre@correo.cl" required />
          </div>
          <div className="campo">
            <label>Contraseña</label>
            <input type="password" name="password" value={form.password} onChange={cambiar} placeholder="••••••••" required />
          </div>
          <button className="btn btn--solido btn--bloque" disabled={cargando}>
            {cargando ? 'Ingresando…' : 'Ingresar'}
          </button>
        </form>

        <p className="auth__pie">
          ¿No tienes cuenta? <Link to="/registro">Regístrate</Link>
        </p>
      </div>
      <div className="auth__aside" />
    </div>
  )
}
