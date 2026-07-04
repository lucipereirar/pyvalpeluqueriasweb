import { useEffect, useState } from 'react'
import { reportesApi } from '../../api/reportes'
import { clp } from '../../utils/formato'

// Analítica de ventas (ms-reportes): KPIs + exportación a Excel.
export default function AdminReportes() {
  const [desde, setDesde] = useState('')
  const [hasta, setHasta] = useState('')
  const [resumen, setResumen] = useState(null)
  const [error, setError] = useState('')

  const consultar = () => {
    setError('')
    reportesApi.resumenVentas(desde, hasta)
      .then(setResumen)
      .catch((e) => setError(e.message))
  }
  useEffect(consultar, []) // carga inicial sin filtros

  const descargar = () => {
    reportesApi.descargarExcel(desde, hasta).catch((e) => setError(e.message))
  }

  return (
    <div>
      {error && <div className="admin__error">{error}</div>}

      <div className="admin__filtros">
        <label>Desde
          <input type="date" value={desde} onChange={(e) => setDesde(e.target.value)} />
        </label>
        <label>Hasta
          <input type="date" value={hasta} onChange={(e) => setHasta(e.target.value)} />
        </label>
        <button className="btn btn--contorno btn--pequeno" onClick={consultar}>Consultar</button>
        <button className="btn btn--solido btn--pequeno" onClick={descargar}>Descargar Excel</button>
      </div>

      {resumen && (
        <>
          <div className="admin__kpis">
            <div className="admin__kpi"><span>Total de ventas</span><strong>{clp(resumen.totalVentas)}</strong></div>
            <div className="admin__kpi"><span>Pagos aprobados</span><strong>{resumen.cantidadPagosAprobados}</strong></div>
            <div className="admin__kpi"><span>Ticket promedio</span><strong>{clp(resumen.ticketPromedio)}</strong></div>
            <div className="admin__kpi"><span>Pedidos</span><strong>{resumen.cantidadPedidos}</strong></div>
          </div>

          {resumen.ventasPorMetodoPago && Object.keys(resumen.ventasPorMetodoPago).length > 0 && (
            <>
              <h3 style={{ margin: '10px 0' }}>Ventas por método de pago</h3>
              <table>
                <thead><tr><th>Método</th><th>Monto</th></tr></thead>
                <tbody>
                  {Object.entries(resumen.ventasPorMetodoPago).map(([k, v]) => (
                    <tr key={k}><td>{k.replace('_', ' ')}</td><td>{clp(v)}</td></tr>
                  ))}
                </tbody>
              </table>
            </>
          )}

          {resumen.pedidosPorEstado && Object.keys(resumen.pedidosPorEstado).length > 0 && (
            <>
              <h3 style={{ margin: '18px 0 10px' }}>Pedidos por estado</h3>
              <table>
                <thead><tr><th>Estado</th><th>Cantidad</th></tr></thead>
                <tbody>
                  {Object.entries(resumen.pedidosPorEstado).map(([k, v]) => (
                    <tr key={k}><td>{k}</td><td>{v}</td></tr>
                  ))}
                </tbody>
              </table>
            </>
          )}

          {resumen.topProductos?.length > 0 && (
            <>
              <h3 style={{ margin: '18px 0 10px' }}>Productos más vendidos</h3>
              <table>
                <thead><tr><th>Producto</th><th>Unidades</th><th>Ingresos</th></tr></thead>
                <tbody>
                  {resumen.topProductos.map((p) => (
                    <tr key={p.productoId ?? p.nombreProducto}>
                      <td>{p.nombreProducto}</td>
                      <td>{p.cantidadVendida}</td>
                      <td>{clp(p.ingresos)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </>
          )}
        </>
      )}
    </div>
  )
}
