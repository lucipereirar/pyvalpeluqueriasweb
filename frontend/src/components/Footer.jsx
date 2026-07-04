import { Link } from 'react-router-dom'
import './Footer.css'

export default function Footer() {
  return (
    <footer className="pie">
      <div className="contenedor pie__grid">
        <div className="pie__marca">
          <span className="pie__logo">Pyval</span>
          <p className="pie__lema">
            Un salón unisex donde la calma, lo natural y la elegancia se encuentran.
          </p>
        </div>

        <div className="pie__col">
          <h4>Explorar</h4>
          <Link to="/servicios">Servicios</Link>
          <Link to="/tienda">Tienda</Link>
          <Link to="/ingresar">Mi cuenta</Link>
        </div>

        <div className="pie__col">
          <h4>Visítanos</h4>
          <span>Av. Providencia 1234, Santiago</span>
          <span>Lun a Sáb · 10:00 – 20:00</span>
          <span>+56 9 1234 5678</span>
        </div>

        <div className="pie__col">
          <h4>Reserva</h4>
          <span>Agenda tu hora por WhatsApp</span>
          <span>hola@pyval.cl</span>
        </div>
      </div>

      <div className="contenedor pie__base">
        <span>© {new Date().getFullYear()} Pyval · Salón de Belleza</span>
        <span>Hecho con cariño en Chile</span>
      </div>
    </footer>
  )
}
