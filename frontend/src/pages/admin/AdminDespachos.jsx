import { useEffect, useState } from 'react'
import { despachosApi, ESTADOS_DESPACHO } from '../../api/despachos'

// Gestión de despachos (ms-despacho). Cada cambio de estado dispara
// automáticamente una notificación al cliente (coreografía del backend).
export default function AdminDespachos() {
  const [despachos, setDespachos] = useState([])
  const [error, setError] = useState('')
  const [ok, setOk] = useState('')

  const cargar = () => {
    despachosApi.listarTodos()
      .then((l) => setDespachos([...l].sort((a, b) => b.id - a.id)))
      .catch((e) => setError(e.message))
  }
  useEffect(cargar, [])

  const cambiarEstado = async (id, estado) => {
    setError(''); setOk('')
    try {
      await despachosApi.actualizarEstado(id, estado)
      setOk(`Despacho #${id} → ${estado}. El cliente fue notificado.`)
      cargar()
    } catch (e) { setError(e.message) }
  }

  return (
    <div>
      {error && <div className="admin__error">{error}</div>}
      {ok && <div className="admin__ok">{ok}</div>}
      <table>
        <thead>
          <tr><th>ID</th><th>Pedido</th><th>Tracking</th><th>Dirección</th><th>Ciudad</th><th>Estado</th></tr>
        </thead>
        <tbody>
          {despachos.map((d) => (
            <tr key={d.id}>
              <td>{d.id}</td>
              <td>#{d.pedidoId}</td>
              <td><code>{d.trackingCode}</code></td>
              <td>{d.direccion}</td>
              <td>{d.ciudad}</td>
              <td>
                <select className="admin__estado" value={d.estado}
                  onChange={(e) => cambiarEstado(d.id, e.target.value)}>
                  {ESTADOS_DESPACHO.map((s) => <option key={s} value={s}>{s.replace('_', ' ')}</option>)}
                </select>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {despachos.length === 0 && !error && <p>No hay despachos registrados.</p>}
      <p style={{ marginTop: 14, fontSize: '0.8rem', color: 'var(--tinta-tenue)' }}>
        La integración con una empresa de courier (tracking automático vía webhook) está
        planificada; por ahora el estado se actualiza manualmente desde este panel.
      </p>
    </div>
  )
}
