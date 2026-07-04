import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { pedidosApi } from '../api/pedidos'
import { despachosApi } from '../api/despachos'
import { clp } from '../utils/formato'
import './MisPedidos.css'

const PASOS_DESPACHO = ['PENDIENTE', 'EN_PREPARACION', 'EN_CAMINO', 'ENTREGADO']

// Historial de pedidos del usuario con el seguimiento de su despacho.
export default function MisPedidos() {
  const { usuario } = useAuth()
  const [pedidos, setPedidos] = useState(null)
  const [despachos, setDespachos] = useState({})

  useEffect(() => {
    if (!usuario) return
    pedidosApi.porUsuario(usuario.id)
      .then((lista) => {
        const orden = [...lista].sort((a, b) => b.id - a.id)
        setPedidos(orden)
        orden.forEach((p) => {
          despachosApi.porPedido(p.id)
            .then((d) => setDespachos((prev) => ({ ...prev, [p.id]: d })))
            .catch(() => {})
        })
      })
      .catch(() => setPedidos([]))
  }, [usuario])

  if (!pedidos) return <section className="pedidos"><div className="contenedor"><p>Cargando…</p></div></section>

  return (
    <section className="pedidos">
      <div className="contenedor">
        <h1>Mis pedidos</h1>
        {pedidos.length === 0 && (
          <div className="pedidos__vacio">
            <p>Aún no tienes pedidos.</p>
            <Link to="/tienda" className="btn btn--contorno btn--pequeno">Ir a la tienda</Link>
          </div>
        )}
        {pedidos.map((p) => {
          const d = despachos[p.id]
          const paso = d ? PASOS_DESPACHO.indexOf(d.estado) : -1
          return (
            <article className="pedido" key={p.id}>
              <header className="pedido__cabecera">
                <div>
                  <h2>Pedido #{p.id}</h2>
                  <span className="pedido__fecha">{new Date(p.fechaCreacion).toLocaleDateString('es-CL')}</span>
                </div>
                <div className="pedido__cabecera-der">
                  <span className={`pedido__estado pedido__estado--${p.estado?.toLowerCase()}`}>{p.estado}</span>
                  <strong>{clp(p.total)}</strong>
                </div>
              </header>

              <ul className="pedido__items">
                {p.items?.map((i) => (
                  <li key={i.id}>
                    {i.nombreProducto} × {i.cantidad} <span>{clp(i.subtotal)}</span>
                  </li>
                ))}
              </ul>

              {d ? (
                <div className="pedido__despacho">
                  <div className="pedido__tracking">
                    Seguimiento: <strong>{d.trackingCode}</strong>
                    {d.estado === 'FALLIDO' && <span className="pedido__fallido"> · Envío fallido, te contactaremos</span>}
                  </div>
                  {d.estado !== 'FALLIDO' && (
                    <ol className="pedido__pasos">
                      {PASOS_DESPACHO.map((etapa, idx) => (
                        <li key={etapa} className={idx <= paso ? 'activo' : ''}>
                          <span className="pedido__punto" />
                          {etapa.replace('_', ' ')}
                        </li>
                      ))}
                    </ol>
                  )}
                </div>
              ) : (
                <p className="pedido__sin-despacho">Despacho en preparación.</p>
              )}
            </article>
          )
        })}
      </div>
    </section>
  )
}
