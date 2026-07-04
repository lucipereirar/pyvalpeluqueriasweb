import { useState } from 'react'
import AdminProductos from './AdminProductos'
import AdminPedidos from './AdminPedidos'
import AdminDespachos from './AdminDespachos'
import AdminReportes from './AdminReportes'
import './Admin.css'

const TABS = [
  { id: 'productos', texto: 'Productos', Comp: AdminProductos },
  { id: 'pedidos', texto: 'Pedidos', Comp: AdminPedidos },
  { id: 'despachos', texto: 'Despachos', Comp: AdminDespachos },
  { id: 'reportes', texto: 'Reportes de venta', Comp: AdminReportes },
]

// Panel de administración del dueño (requiere rol ADMIN).
export default function Admin() {
  const [tab, setTab] = useState('productos')
  const { Comp } = TABS.find((t) => t.id === tab)

  return (
    <section className="admin">
      <div className="contenedor">
        <h1>Panel de administración</h1>
        <p className="admin__sub">Gestiona el catálogo, los pedidos, los despachos y la analítica de ventas.</p>

        <div className="admin__tabs">
          {TABS.map((t) => (
            <button
              key={t.id}
              className={`admin__tab ${tab === t.id ? 'admin__tab--activo' : ''}`}
              onClick={() => setTab(t.id)}
            >
              {t.texto}
            </button>
          ))}
        </div>

        <div className="admin__panel">
          <Comp />
        </div>
      </div>
    </section>
  )
}
