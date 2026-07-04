import { useCart } from '../context/CartContext'
import { clp } from '../utils/formato'
import './ProductCard.css'

const etiquetaCategoria = {
  CUIDADO_CABELLO: 'Cabello',
  CUIDADO_PIEL: 'Piel',
  COLORACION: 'Color',
  HERRAMIENTAS: 'Herramientas',
  ACCESORIOS: 'Accesorios',
  OTROS: 'Otros',
}

export default function ProductCard({ producto }) {
  const { agregar } = useCart()
  const sinStock = producto.stock != null && producto.stock <= 0

  return (
    <article className="tarjeta producto">
      <div className="producto__imagen">
        {producto.imagen
          ? <img src={producto.imagen} alt={producto.nombre} />
          : <span className="producto__inicial">{producto.nombre.charAt(0)}</span>}
        <span className="badge producto__cat">{etiquetaCategoria[producto.categoria] || producto.categoria}</span>
      </div>

      <div className="producto__cuerpo">
        <h3 className="producto__nombre">{producto.nombre}</h3>
        <p className="producto__desc">{producto.descripcion}</p>

        <div className="producto__pie">
          <span className="producto__precio">{clp(producto.precio)}</span>
          <button
            className="btn btn--salvia btn--pequeno"
            disabled={sinStock}
            onClick={() => agregar(producto, 1)}
          >
            {sinStock ? 'Agotado' : 'Agregar'}
          </button>
        </div>
      </div>
    </article>
  )
}
