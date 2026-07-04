import { useEffect, useMemo, useState } from 'react'
import ProductCard from '../components/ProductCard'
import { listarProductos } from '../api/productos'
import './Catalogo.css'

const CATEGORIAS = [
  { valor: 'todos', etiqueta: 'Todos' },
  { valor: 'CUIDADO_CABELLO', etiqueta: 'Cabello' },
  { valor: 'CUIDADO_PIEL', etiqueta: 'Piel' },
  { valor: 'COLORACION', etiqueta: 'Color' },
  { valor: 'HERRAMIENTAS', etiqueta: 'Herramientas' },
  { valor: 'ACCESORIOS', etiqueta: 'Accesorios' },
  { valor: 'OTROS', etiqueta: 'Otros' },
]

export default function Catalogo() {
  const [productos, setProductos] = useState([])
  const [cargando, setCargando] = useState(true)
  const [demo, setDemo] = useState(false)
  const [categoria, setCategoria] = useState('todos')
  const [busqueda, setBusqueda] = useState('')

  useEffect(() => {
    let vivo = true
    listarProductos().then(({ productos, demo }) => {
      if (!vivo) return
      setProductos(productos)
      setDemo(demo)
      setCargando(false)
    })
    return () => { vivo = false }
  }, [])

  const filtrados = useMemo(() => {
    return productos.filter((p) => {
      const okCat = categoria === 'todos' || p.categoria === categoria
      const okBusq = p.nombre.toLowerCase().includes(busqueda.toLowerCase().trim())
      return okCat && okBusq && p.activo !== false
    })
  }, [productos, categoria, busqueda])

  return (
    <div className="catalogo">
      <header className="encabezado-pagina">
        <div className="contenedor centro">
          <span className="eyebrow">Tienda</span>
          <h1 className="titulo-seccion">Catálogo de productos</h1>
          <p className="subtitulo-seccion">
            Productos seleccionados para cuidar tu belleza en casa. Agrégalos al
            carrito y completa tu compra.
          </p>
        </div>
      </header>

      <div className="contenedor">
        {demo && !cargando && (
          <div className="aviso-demo">
            Mostrando catálogo de demostración. Inicia los microservicios
            (<code>ms-productos</code> vía API Gateway) para ver el catálogo real.
          </div>
        )}

        <div className="catalogo__controles">
          <div className="buscador">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.6"><circle cx="11" cy="11" r="7" /><path d="m21 21-4.3-4.3" /></svg>
            <input
              type="text"
              placeholder="Buscar producto…"
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
            />
          </div>
          <div className="catalogo__cats">
            {CATEGORIAS.map((c) => (
              <button
                key={c.valor}
                className={`filtro ${categoria === c.valor ? 'filtro--activo' : ''}`}
                onClick={() => setCategoria(c.valor)}
              >
                {c.etiqueta}
              </button>
            ))}
          </div>
        </div>

        {cargando ? (
          <div className="catalogo__estado">Cargando productos…</div>
        ) : filtrados.length === 0 ? (
          <div className="catalogo__estado">No encontramos productos con esos criterios.</div>
        ) : (
          <div className="grid grid--3 catalogo__grid">
            {filtrados.map((p) => <ProductCard key={p.id} producto={p} />)}
          </div>
        )}
      </div>
    </div>
  )
}
