import { useEffect, useState } from 'react'
import { productosAdmin, CATEGORIAS } from '../../api/productos'
import { clp } from '../../utils/formato'

const FORM_VACIO = { nombre: '', descripcion: '', precio: '', stock: '', categoria: 'CUIDADO_CABELLO', imagen: '' }

// CRUD del catálogo (ms-productos) para el administrador.
export default function AdminProductos() {
  const [productos, setProductos] = useState([])
  const [form, setForm] = useState(FORM_VACIO)
  const [editando, setEditando] = useState(null) // id en edición
  const [error, setError] = useState('')
  const [ok, setOk] = useState('')

  const cargar = () => {
    productosAdmin.listarTodos()
      .then(setProductos)
      .catch((e) => setError(e.message))
  }
  useEffect(cargar, [])

  const cambiar = (e) => setForm({ ...form, [e.target.name]: e.target.value })

  const guardar = async (e) => {
    e.preventDefault()
    setError(''); setOk('')
    const cuerpo = { ...form, precio: Number(form.precio), stock: Number(form.stock) }
    try {
      if (editando) {
        await productosAdmin.actualizar(editando, cuerpo)
        setOk(`Producto #${editando} actualizado.`)
      } else {
        await productosAdmin.crear(cuerpo)
        setOk('Producto creado.')
      }
      setForm(FORM_VACIO); setEditando(null); cargar()
    } catch (err) { setError(err.message) }
  }

  const editar = (p) => {
    setEditando(p.id)
    setForm({
      nombre: p.nombre, descripcion: p.descripcion || '', precio: p.precio,
      stock: p.stock, categoria: p.categoria, imagen: p.imagen || '',
    })
  }

  const eliminar = async (p) => {
    if (!window.confirm(`¿Eliminar "${p.nombre}"?`)) return
    setError(''); setOk('')
    try {
      await productosAdmin.eliminar(p.id)
      setOk(`Producto "${p.nombre}" eliminado.`)
      cargar()
    } catch (err) { setError(err.message) }
  }

  return (
    <div>
      {error && <div className="admin__error">{error}</div>}
      {ok && <div className="admin__ok">{ok}</div>}

      <form className="admin__form" onSubmit={guardar}>
        <label>Nombre
          <input name="nombre" required value={form.nombre} onChange={cambiar} />
        </label>
        <label>Descripción
          <input name="descripcion" value={form.descripcion} onChange={cambiar} />
        </label>
        <label>Precio (CLP)
          <input name="precio" type="number" min="0" required value={form.precio} onChange={cambiar} />
        </label>
        <label>Stock
          <input name="stock" type="number" min="0" required value={form.stock} onChange={cambiar} />
        </label>
        <label>Categoría
          <select name="categoria" value={form.categoria} onChange={cambiar}>
            {CATEGORIAS.map((c) => <option key={c} value={c}>{c.replace('_', ' ')}</option>)}
          </select>
        </label>
        <label>Imagen (URL)
          <input name="imagen" value={form.imagen} onChange={cambiar} placeholder="https://…" />
        </label>
        <div className="admin__form-acciones">
          <button className="btn btn--solido btn--pequeno">
            {editando ? `Guardar cambios (#${editando})` : 'Agregar producto'}
          </button>
          {editando && (
            <button type="button" className="btn btn--contorno btn--pequeno"
              onClick={() => { setEditando(null); setForm(FORM_VACIO) }}>
              Cancelar edición
            </button>
          )}
        </div>
      </form>

      <table>
        <thead>
          <tr><th>ID</th><th>Nombre</th><th>Categoría</th><th>Precio</th><th>Stock</th><th>Activo</th><th></th></tr>
        </thead>
        <tbody>
          {productos.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.nombre}</td>
              <td>{p.categoria?.replace('_', ' ')}</td>
              <td>{clp(p.precio)}</td>
              <td>{p.stock}</td>
              <td>{p.activo ? 'Sí' : 'No'}</td>
              <td className="admin__acciones-td">
                <button className="btn--mini" onClick={() => editar(p)}>Editar</button>
                <button className="btn--mini btn--mini--peligro" onClick={() => eliminar(p)}>Eliminar</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  )
}
