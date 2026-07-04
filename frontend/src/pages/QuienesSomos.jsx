import { Link } from 'react-router-dom'
import './Pagina.css'

export default function QuienesSomos() {
  const valores = [
    { t: 'Cuidado natural', d: 'Productos nobles y tratamientos respetuosos con tu piel y tu cabello.' },
    { t: 'Ambiente que relaja', d: 'Luz cálida, vegetación y música suave para que desconectes de verdad.' },
    { t: 'Atención cercana', d: 'Un equipo unisex que escucha y aconseja según lo que tú necesitas.' },
  ]

  return (
    <div>
      <header className="encabezado-pagina">
        <div className="contenedor centro">
          <span className="eyebrow">Nuestra historia</span>
          <h1 className="titulo-seccion">Quiénes somos</h1>
          <p className="subtitulo-seccion">
            Un salón unisex donde la belleza se vive con calma, naturalidad y elegancia.
          </p>
        </div>
      </header>

      <section className="seccion">
        <div className="contenedor pagina__texto">
          <p>
            <strong>Pyval Peluquerías</strong> nació con una idea simple: que cuidarse
            fuera también un momento de descanso. Creamos un espacio luminoso, cálido y
            rodeado de plantas, pensado para que cada persona se sienta cómoda desde que
            entra.
          </p>
          <p>
            Atendemos a todo público, con un enfoque especial en el cuidado femenino, y
            trabajamos con productos seleccionados y técnicas actuales. Cada corte, color
            o tratamiento se hace con dedicación, sin prisa y con la estética que nos
            distingue.
          </p>
        </div>

        <div className="contenedor">
          <div className="grid grid--3 pagina__valores">
            {valores.map((v) => (
              <div className="tarjeta pagina__valor" key={v.t}>
                <h3>{v.t}</h3>
                <p>{v.d}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="pagina__cta">
        <div className="contenedor centro">
          <h2>¿Nos visitas?</h2>
          <p>Conoce nuestros servicios o escríbenos para agendar tu hora.</p>
          <div className="pagina__cta-acciones">
            <Link to="/servicios" className="btn btn--solido">Ver servicios</Link>
            <Link to="/contacto" className="btn btn--contorno">Contáctanos</Link>
          </div>
        </div>
      </section>
    </div>
  )
}
