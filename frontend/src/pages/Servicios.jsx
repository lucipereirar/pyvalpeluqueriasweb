import { useState } from 'react'
import { categoriasServicios } from '../data/servicios'
import { clp } from '../utils/formato'
import './Servicios.css'

export default function Servicios() {
  const [activa, setActiva] = useState('todos')

  const categorias = activa === 'todos'
    ? categoriasServicios
    : categoriasServicios.filter((c) => c.id === activa)

  return (
    <div className="servicios">
      <header className="encabezado-pagina">
        <div className="contenedor centro">
          <span className="eyebrow">Nuestra carta</span>
          <h1 className="titulo-seccion">Servicios del salón</h1>
          <p className="subtitulo-seccion">
            Todos nuestros servicios con valores actualizados. Los precios están en
            pesos chilenos e incluyen la atención de nuestro equipo.
          </p>
        </div>
      </header>

      <div className="contenedor">
        <div className="filtros">
          <button
            className={`filtro ${activa === 'todos' ? 'filtro--activo' : ''}`}
            onClick={() => setActiva('todos')}
          >
            Todos
          </button>
          {categoriasServicios.map((c) => (
            <button
              key={c.id}
              className={`filtro ${activa === c.id ? 'filtro--activo' : ''}`}
              onClick={() => setActiva(c.id)}
            >
              {c.nombre}
            </button>
          ))}
        </div>

        {categorias.map((c) => (
          <section className="cat-servicio" key={c.id}>
            <div className="cat-servicio__cab">
              <h2>{c.nombre}</h2>
              <p>{c.descripcion}</p>
            </div>
            <ul className="lista-servicios">
              {c.servicios.map((s) => (
                <li className="linea-servicio" key={s.nombre}>
                  <div className="linea-servicio__info">
                    <span className="linea-servicio__nombre">{s.nombre}</span>
                    <span className="linea-servicio__detalle">{s.detalle}</span>
                  </div>
                  <span className="linea-servicio__puntos" aria-hidden="true" />
                  <div className="linea-servicio__meta">
                    <span className="linea-servicio__duracion">{s.duracion}</span>
                    <span className="linea-servicio__precio">{clp(s.precio)}</span>
                  </div>
                </li>
              ))}
            </ul>
          </section>
        ))}
      </div>

      <section className="reserva-nota">
        <div className="contenedor centro">
          <h2>¿Lista o listo para tu cita?</h2>
          <p>Escríbenos por WhatsApp al +56 9 1234 5678 o al correo hola@pyval.cl para agendar tu hora.</p>
        </div>
      </section>
    </div>
  )
}
