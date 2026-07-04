import { useEffect, useState } from 'react'
import { pedidosApi } from '../../api/pedidos'
import { clp } from '../../utils/formato'

const ESTADOS_PEDIDO = ['PENDIENTE', 'CONFIRMADO', 'DESPACHADO', 'ENTREGADO', 'CANCELADO']

// Gestión de pedidos (ms-pedidos) para el administrador.
export default function AdminPedidos() {
  const [pedidos, setPedidos] = useState([])
  const [error, setError] = useState('')

  const cargar = () => {
    pedidosApi.listarTodos()
      .then((l) => setPedidos([...l].sort((a, b) => b.id - a.id)))
      .catch((e) => setError(e.message))
  }
  useEffect(cargar, [])

  const cambiarEstado = async (id, estado) => {
    setError('')
    try {
      await pedidosApi.actualizarEstado(id, estado)
      cargar()
    } catch (e) { setError(e.message) }
  }

  return (
    <div>
      {error && <div className="admin__error">{error}</div>}
      <table>
        <thead>
          <tr><th>ID</th><th>Usuario</th><th>Fecha</th><th>Ítems</th><th>Total</th><th>Estado</th></tr>
        </thead>
        <tbody>
          {pedidos.map((p) => (
            <tr key={p.id}>
              <td>#{p.id}</td>
              <td>{p.usuarioId}</td>
              <td>{p.fechaCreacion ? new Date(p.fechaCreacion).toLocaleDateString('es-CL') : '—'}</td>
              <td>{p.items?.map((i) => `${i.nombreProducto} ×${i.cantidad}`).join(', ')}</td>
              <td>{clp(p.total)}</td>
              <td>
                <select className="admin__estado" value={p.estado}
                  onChange={(e) => cambiarEstado(p.id, e.target.value)}>
                  {ESTADOS_PEDIDO.map((s) => <option key={s} value={s}>{s}</option>)}
                </select>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {pedidos.length === 0 && !error && <p>No hay pedidos registrados.</p>}
    </div>
  )
}
