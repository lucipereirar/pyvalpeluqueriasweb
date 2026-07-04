import { useState } from 'react'
import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'
import Logo from './Logo'
import './Navbar.css'

const ENLACES = [
  { to: '/', texto: 'Inicio', end: true },
  { to: '/quienes-somos', texto: 'Quiénes somos' },
  { to: '/servicios', texto: 'Servicios' },
  { to: '/tienda', texto: 'Tienda' },
  { to: '/contacto', texto: 'Contacto' },
]

export default function Navbar() {
  const { cantidadTotal, setAbierto } = useCart()
  const { usuario, logout } = useAuth()
  const [menuAbierto, setMenuAbierto] = useState(false)
  const navigate = useNavigate()
  const cerrar = () => setMenuAbierto(false)

  // Enlaces según la sesión: pedidos para clientes, panel para el admin.
  const enlaces = [
    ...ENLACES,
    ...(usuario ? [{ to: '/mis-pedidos', texto: 'Mis pedidos' }] : []),
    ...(usuario?.rol === 'ADMIN' ? [{ to: '/admin', texto: 'Administración' }] : []),
  ]

  return (
    <header className="nav">
      <div className="contenedor nav__top">
        <button
          className={`nav__burger ${menuAbierto ? 'nav__burger--activo' : ''}`}
          onClick={() => setMenuAbierto((v) => !v)}
          aria-label="Menú"
        >
          <span /><span /><span />
        </button>

        <Link to="/" className="nav__logo" onClick={cerrar}>
          <Logo variant="nav" />
        </Link>

        <div className="nav__acciones">
          {usuario ? (
            <div className="nav__usuario nav__solo-desktop">
              <span className="nav__saludo">Hola, {usuario.email.split('@')[0]}</span>
              <button className="nav__texto-btn" onClick={logout}>Salir</button>
            </div>
          ) : (
            <button className="nav__texto-btn nav__solo-desktop" onClick={() => navigate('/ingresar')}>
              Ingresar
            </button>
          )}

          <button className="nav__carrito" onClick={() => setAbierto(true)} aria-label="Abrir carrito">
            <CarritoIcono />
            {cantidadTotal > 0 && <span className="nav__contador">{cantidadTotal}</span>}
          </button>
        </div>
      </div>

      {/* Segunda fila: menú centrado (escritorio) */}
      <nav className="nav__links">
        {enlaces.map((e) => (
          <NavLink
            key={e.to}
            to={e.to}
            end={e.end}
            className={({ isActive }) => `nav__link ${isActive ? 'nav__link--activo' : ''}`}
          >
            {e.texto}
          </NavLink>
        ))}
      </nav>

      {/* Menú desplegable (móvil) */}
      <nav className={`nav__movil ${menuAbierto ? 'nav__movil--abierto' : ''}`}>
        {enlaces.map((e) => (
          <NavLink
            key={e.to}
            to={e.to}
            end={e.end}
            className={({ isActive }) => `nav__link ${isActive ? 'nav__link--activo' : ''}`}
            onClick={cerrar}
          >
            {e.texto}
          </NavLink>
        ))}
        <div className="nav__movil-cuenta">
          {usuario ? (
            <button className="btn btn--contorno btn--pequeno" onClick={() => { logout(); cerrar() }}>Salir</button>
          ) : (
            <Link to="/ingresar" className="btn btn--contorno btn--pequeno" onClick={cerrar}>Ingresar</Link>
          )}
        </div>
      </nav>
    </header>
  )
}

function CarritoIcono() {
  return (
    <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6" strokeLinecap="round" strokeLinejoin="round">
      <path d="M6 2 3 6v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2V6l-3-4Z" />
      <path d="M3 6h18" />
      <path d="M16 10a4 4 0 0 1-8 0" />
    </svg>
  )
}
