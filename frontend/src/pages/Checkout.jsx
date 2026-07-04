import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { useCart } from '../context/CartContext'
import { pedidosApi } from '../api/pedidos'
import { pagosApi, METODOS_PAGO } from '../api/pagos'
import { carritoApi } from '../api/carrito'
import { clp } from '../utils/formato'
import './Checkout.css'

const IVA = 0.19

// Checkout: crea el pedido en ms-pedidos y procesa el pago en ms-pago.
// Al aprobarse el pago, el backend genera el despacho y la notificación
// automáticamente (coreografía pago → despacho → notificación).
export default function Checkout() {
  const { usuario } = useAuth()
  const { items, total, vaciar } = useCart()
  const navigate = useNavigate()

  const [form, setForm] = useState({
    direccion: '', ciudad: '', region: '', codigoPostal: '', metodoPago: 'WEBPAY',
  })
  const [error, setError] = useState('')
  const [procesando, setProcesando] = useState(false)

  const subtotal = total
  const impuesto = Math.round(subtotal * IVA)
  const totalConIva = subtotal + impuesto

  const cambiar = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const pagar = async (e) => {
    e.preventDefault()
    setError('')
    if (!items.length) { setError('Tu carrito está vacío.'); return }
    setProcesando(true)
    try {
      // 1. Crear el pedido (el backend valida stock y calcula IVA/total)
      const pedido = await pedidosApi.crear(
        usuario.id,
        items.map((i) => ({ productoId: i.producto.id, cantidad: i.cantidad })),
      )
      // 2. Procesar el pago con los datos de envío
      await pagosApi.procesar({
        pedidoId: pedido.id,
        usuarioId: usuario.id,
        monto: pedido.total,
        metodoPago: form.metodoPago,
        direccion: form.direccion,
        ciudad: form.ciudad,
        region: form.region,
        codigoPostal: form.codigoPostal,
      })
      // 3. Cerrar el carrito del backend (best-effort) y vaciar el local
      carritoApi.procesar(usuario.id).catch(() => {})
      vaciar()
      navigate(`/confirmacion/${pedido.id}`)
    } catch (err) {
      setError(err.message || 'No se pudo completar la compra.')
    } finally {
      setProcesando(false)
    }
  }

  return (
    <section className="checkout">
      <div className="contenedor">
        <h1>Finalizar compra</h1>
        <p className="checkout__sub">Revisa tu pedido y completa los datos de envío.</p>

        {error && <div className="checkout__error">{error}</div>}

        <form className="checkout__grid" onSubmit={pagar}>
          <div className="checkout__panel">
            <h2>Datos de envío</h2>
            <div className="checkout__campo">
              <label htmlFor="direccion">Dirección</label>
              <input id="direccion" name="direccion" required value={form.direccion}
                onChange={cambiar} placeholder="Calle y número, depto/casa" />
            </div>
            <div className="checkout__fila">
              <div className="checkout__campo">
                <label htmlFor="ciudad">Ciudad / Comuna</label>
                <input id="ciudad" name="ciudad" required value={form.ciudad} onChange={cambiar} />
              </div>
              <div className="checkout__campo">
                <label htmlFor="region">Región</label>
                <input id="region" name="region" value={form.region} onChange={cambiar} />
              </div>
            </div>
            <div className="checkout__fila">
              <div className="checkout__campo">
                <label htmlFor="codigoPostal">Código postal</label>
                <input id="codigoPostal" name="codigoPostal" required value={form.codigoPostal} onChange={cambiar} />
              </div>
              <div className="checkout__campo">
                <label htmlFor="metodoPago">Método de pago</label>
                <select id="metodoPago" name="metodoPago" value={form.metodoPago} onChange={cambiar}>
                  {METODOS_PAGO.map((m) => (
                    <option key={m.valor} value={m.valor}>{m.texto}</option>
                  ))}
                </select>
              </div>
            </div>
            <p className="checkout__nota">
              El pago se procesa de forma segura. Recibirás el seguimiento de tu despacho
              en “Mis pedidos”.
            </p>
          </div>

          <div className="checkout__panel">
            <h2>Tu pedido</h2>
            {items.map(({ producto, cantidad }) => (
              <div className="checkout__resumen-item" key={producto.id}>
                <span>{producto.nombre} × {cantidad}</span>
                <span>{clp(Number(producto.precio) * cantidad)}</span>
              </div>
            ))}
            <div className="checkout__totales">
              <div><span>Subtotal</span><span>{clp(subtotal)}</span></div>
              <div><span>IVA (19%)</span><span>{clp(impuesto)}</span></div>
              <div className="checkout__total"><span>Total</span><span>{clp(totalConIva)}</span></div>
            </div>
            <br />
            <button className="btn btn--solido btn--bloque" disabled={procesando || !items.length}>
              {procesando ? 'Procesando…' : 'Pagar ahora'}
            </button>
          </div>
        </form>
      </div>
    </section>
  )
}
