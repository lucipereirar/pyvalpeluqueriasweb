import { useNavigate } from 'react-router-dom'
import { useCart } from '../context/CartContext'
import { useAuth } from '../context/AuthContext'
import { clp } from '../utils/formato'
import './CartDrawer.css'

export default function CartDrawer() {
  const { items, total, cantidadTotal, abierto, setAbierto, cambiarCantidad, quitar, vaciar } = useCart()
  const { usuario } = useAuth()
  const navigate = useNavigate()

  const irACuenta = () => {
    setAbierto(false)
    navigate('/ingresar')
  }

  return (
    <>
      <div
        className={`drawer__overlay ${abierto ? 'drawer__overlay--visible' : ''}`}
        onClick={() => setAbierto(false)}
      />
      <aside className={`drawer ${abierto ? 'drawer--abierto' : ''}`} aria-hidden={!abierto}>
        <div className="drawer__cabecera">
          <h3>Tu carrito {cantidadTotal > 0 && <span>({cantidadTotal})</span>}</h3>
          <button className="drawer__cerrar" onClick={() => setAbierto(false)} aria-label="Cerrar">×</button>
        </div>

        {items.length === 0 ? (
          <div className="drawer__vacio">
            <p>Tu carrito está vacío.</p>
            <button className="btn btn--contorno btn--pequeno" onClick={() => { setAbierto(false); navigate('/tienda') }}>
              Ver la tienda
            </button>
          </div>
        ) : (
          <>
            <div className="drawer__items">
              {items.map(({ producto, cantidad }) => (
                <div className="drawer__item" key={producto.id}>
                  <div className="drawer__miniatura">
                    {producto.imagen
                      ? <img src={producto.imagen} alt={producto.nombre} />
                      : <span>{producto.nombre.charAt(0)}</span>}
                  </div>
                  <div className="drawer__detalle">
                    <p className="drawer__nombre">{producto.nombre}</p>
                    <p className="drawer__precio">{clp(producto.precio)}</p>
                    <div className="drawer__cant">
                      <button onClick={() => cambiarCantidad(producto.id, cantidad - 1)}>−</button>
                      <span>{cantidad}</span>
                      <button onClick={() => cambiarCantidad(producto.id, cantidad + 1)}>+</button>
                    </div>
                  </div>
                  <button className="drawer__quitar" onClick={() => quitar(producto.id)} aria-label="Quitar">×</button>
                </div>
              ))}
            </div>

            <div className="drawer__pie">
              <div className="drawer__total">
                <span>Total</span>
                <span className="drawer__total-valor">{clp(total)}</span>
              </div>
              {!usuario && (
                <p className="drawer__aviso">Inicia sesión para finalizar tu compra.</p>
              )}
              <button
                className="btn btn--solido btn--bloque"
                onClick={usuario ? () => { setAbierto(false); navigate('/checkout') } : irACuenta}
              >
                {usuario ? 'Finalizar compra' : 'Ingresar para comprar'}
              </button>
              <button className="drawer__vaciar" onClick={vaciar}>Vaciar carrito</button>
            </div>
          </>
        )}
      </aside>
    </>
  )
}
