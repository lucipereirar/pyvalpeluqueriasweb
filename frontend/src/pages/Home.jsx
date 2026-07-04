import { Link } from 'react-router-dom'
import { categoriasServicios } from '../data/servicios'
import { clp } from '../utils/formato'
import Logo from '../components/Logo'
import './Home.css'

export default function Home() {
  const destacados = categoriasServicios
    .map((c) => ({ categoria: c.nombre, ...c.servicios[0] }))
    .slice(0, 4)

  return (
    <div className="home">
      {/* HERO */}
      <section className="hero">
        <div className="hero__bg" />
        <div className="hero__contenido aparecer">
          <Logo variant="hero" />
          <p className="hero__sub">
            Un jardín de belleza profesional — marketplace de productos de salón,
            servicios y rutinas de cuidado.
          </p>
          <div className="hero__acciones">
            <Link to="/tienda" className="btn btn--solido">Ver productos →</Link>
            <Link to="/servicios" className="btn btn--contorno-claro">Reservar hora</Link>
          </div>
        </div>
      </section>

      {/* PILARES */}
      <section className="seccion seccion--crema">
        <div className="contenedor pilares">
          {[
            { t: 'Ambiente relajante', d: 'Vegetación, luz natural y aromas suaves para desconectar.', i: 'hoja' },
            { t: 'Productos naturales', d: 'Seleccionados con ingredientes nobles y cuidado real.', i: 'gota' },
            { t: 'Manos expertas', d: 'Un equipo unisex con años de experiencia y cariño.', i: 'estrella' },
          ].map((p) => (
            <div className="pilar" key={p.t}>
              <div className="pilar__icono"><Icono nombre={p.i} /></div>
              <h3>{p.t}</h3>
              <p>{p.d}</p>
            </div>
          ))}
        </div>
      </section>

      {/* SERVICIOS DESTACADOS */}
      <section className="seccion">
        <div className="contenedor">
          <div className="centro" style={{ marginBottom: '52px' }}>
            <span className="eyebrow">Lo que hacemos</span>
            <h2 className="titulo-seccion">Servicios destacados</h2>
            <p className="subtitulo-seccion">
              Una muestra de nuestro trabajo. Precios siempre actualizados en la página.
            </p>
          </div>

          <div className="grid grid--auto">
            {destacados.map((s) => (
              <div className="tarjeta destacado" key={s.nombre}>
                <span className="badge">{s.categoria}</span>
                <h3 className="destacado__nombre">{s.nombre}</h3>
                <p className="destacado__detalle">{s.detalle} · {s.duracion}</p>
                <span className="destacado__precio">{clp(s.precio)}</span>
              </div>
            ))}
          </div>

          <div className="centro" style={{ marginTop: '48px' }}>
            <Link to="/servicios" className="btn btn--contorno">Ver todos los servicios</Link>
          </div>
        </div>
      </section>

      {/* FRANJA CTA */}
      <section className="cta">
        <div className="contenedor cta__inner">
          <div>
            <h2 className="cta__titulo">Tu momento de calma te espera</h2>
            <p className="cta__sub">Reserva tu hora y déjate cuidar en un ambiente pensado para ti.</p>
          </div>
          <Link to="/servicios" className="btn btn--salvia">Reservar ahora</Link>
        </div>
      </section>
    </div>
  )
}

function Icono({ nombre }) {
  const comun = { width: 30, height: 30, viewBox: '0 0 24 24', fill: 'none', stroke: 'currentColor', strokeWidth: 1.5, strokeLinecap: 'round', strokeLinejoin: 'round' }
  if (nombre === 'hoja') return <svg {...comun}><path d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 17 4.48 19 2c1 2 2 4.18 2 8 0 5.5-4.78 10-10 10Z" /><path d="M2 21c0-3 1.85-5.36 5.08-6" /></svg>
  if (nombre === 'gota') return <svg {...comun}><path d="M12 22a7 7 0 0 0 7-7c0-2-1-3.9-3-5.5s-3.5-4-4-6.5c-.5 2.5-2 4.9-4 6.5S5 13 5 15a7 7 0 0 0 7 7Z" /></svg>
  return <svg {...comun}><path d="m12 3 2.6 5.3 5.9.9-4.3 4.1 1 5.8L12 16.5 6.8 19.2l1-5.8L3.5 9.2l5.9-.9Z" /></svg>
}
