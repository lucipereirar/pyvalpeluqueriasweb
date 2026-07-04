import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import './Auth.css'

export default function Registro() {
  const { registrar } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ nombre: '', apellido: '', email: '', password: '' })
  const [error, setError] = useState('')
  const [cargando, setCargando] = useState(false)

  const cambiar = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const enviar = async (e) => {
    e.preventDefault()
    setError('')
    if (form.password.length < 8) {
      setError('La contraseña debe tener al menos 8 caracteres.')
      return
    }
    setCargando(true)
    try {
      await registrar({ ...form, rol: 'CLIENTE' })
      navigate('/tienda')
    } catch (err) {
      setError(err.message || 'No pudimos crear tu cuenta.')
    } finally {
      setCargando(false)
    }
  }

  return (
    <div className="auth">
      <div className="auth__panel">
        <span className="eyebrow">Únete a Pyval</span>
        <h1 className="auth__titulo">Crea tu cuenta</h1>
        <p className="auth__sub">Regístrate para comprar y guardar tu carrito.</p>

        <form onSubmit={enviar} className="auth__form">
          {error && <div className="auth__error">{error}</div>}
          <div className="auth__fila">
            <div className="campo">
              <label>Nombre</label>
              <input name="nombre" value={form.nombre} onChange={cambiar} placeholder="María" required />
            </div>
            <div className="campo">
              <label>Apellido</label>
              <input name="apellido" value={form.apellido} onChange={cambiar} placeholder="González" required />
            </div>
          </div>
          <div className="campo">
            <label>Correo</label>
            <input type="email" name="email" value={form.email} onChange={cambiar} placeholder="nombre@correo.cl" required />
          </div>
          <div className="campo">
            <label>Contraseña</label>
            <input type="password" name="password" value={form.password} onChange={cambiar} placeholder="Mínimo 8 caracteres" required />
          </div>
          <button className="btn btn--solido btn--bloque" disabled={cargando}>
            {cargando ? 'Creando cuenta…' : 'Crear cuenta'}
          </button>
        </form>

        <p className="auth__pie">
          ¿Ya tienes cuenta? <Link to="/ingresar">Ingresa</Link>
        </p>
      </div>
      <div className="auth__aside auth__aside--registro" />
    </div>
  )
}
