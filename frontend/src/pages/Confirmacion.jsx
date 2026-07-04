import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { pedidosApi } from '../api/pedidos'
import { pagosApi } from '../api/pagos'
import { despachosApi } from '../api/despachos'
import { clp } from '../utils/formato'
import './Checkout.css'

// Confirmación de compra: muestra pedido, pago y despacho (con tracking).
export default function Confirmacion() {
  const { pedidoId } = useParams()
  const [pedido, setPedido] = useState(null)
  const [pago, setPago] = useState(null)
  const [despacho, setDespacho] = useState(null)

  useEffect(() => {
    pedidosApi.porId(pedidoId).then(setPedido).catch(() => {})
    pagosApi.porPedido(pedidoId).then(setPago).catch(() => {})
    despachosApi.porPedido(pedidoId).then(setDespacho).catch(() => {})
  }, [pedidoId])

  return (
    <section className="confirmacion">
      <div className="contenedor">
        <div className="confirmacion__icono">✓</div>
        <h1>¡Gracias por tu compra!</h1>
        <p>Tu pedido #{pedidoId} fue recibido y el pago está aprobado.</p>

        <div className="confirmacion__detalle">
          {pedido && (
            <>
              <p><span>Total pagado</span><strong>{clp(pedido.total)}</strong></p>
              <p><span>Estado del pedido</span><strong>{pedido.estado}</strong></p>
            </>
          )}
          {pago && (
            <p><span>Transacción</span><strong>{pago.transaccionId}</strong></p>
          )}
          <p>
            <span>Despacho</span>
            <strong>{despacho ? `${despacho.estado} · ${despacho.trackingCode}` : 'En preparación'}</strong>
          </p>
        </div>

        <div className="confirmacion__acciones">
          <Link to="/mis-pedidos" className="btn btn--solido">Ver mis pedidos</Link>
          <Link to="/tienda" className="btn btn--contorno">Seguir comprando</Link>
        </div>
      </div>
    </section>
  )
}
