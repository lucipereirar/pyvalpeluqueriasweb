import { useState } from 'react'
import './Pagina.css'

export default function Contacto() {
  const [form, setForm] = useState({ nombre: '', email: '', mensaje: '' })
  const [enviado, setEnviado] = useState(false)

  const cambiar = (e) => setForm({ ...form, [e.target.name]: e.target.value })
  const enviar = (e) => {
    e.preventDefault()
    // Frontend por ahora. A futuro puede conectarse a ms-notificaciones.
    setEnviado(true)
  }

  return (
    <div>
      <header className="encabezado-pagina">
        <div className="contenedor centro">
          <span className="eyebrow">Estamos para ti</span>
          <h1 className="titulo-seccion">Contacto</h1>
          <p className="subtitulo-seccion">
            Escríbenos para agendar tu hora o resolver cualquier duda.
          </p>
        </div>
      </header>

      <section className="seccion">
        <div className="contenedor contacto__grid">
          <div className="contacto__info">
            <h3>Visítanos</h3>
            <ul>
              <li><span>Dirección</span>Av. Providencia 1234, Santiago</li>
              <li><span>Horario</span>Lun a Sáb · 10:00 – 20:00</li>
              <li><span>Teléfono</span>+56 9 1234 5678</li>
              <li><span>Correo</span>hola@pyval.cl</li>
            </ul>
            <p className="contacto__nota">
              También puedes escribirnos por WhatsApp para agendar más rápido.
            </p>
          </div>

          <div className="contacto__form tarjeta">
            {enviado ? (
              <div className="contacto__ok">
                <h3>¡Gracias, {form.nombre || 'nos vemos pronto'}!</h3>
                <p>Recibimos tu mensaje y te responderemos a la brevedad.</p>
                <button className="btn btn--contorno btn--pequeno" onClick={() => { setEnviado(false); setForm({ nombre: '', email: '', mensaje: '' }) }}>
                  Enviar otro mensaje
                </button>
              </div>
            ) : (
              <form onSubmit={enviar}>
                <div className="campo">
                  <label>Nombre</label>
                  <input name="nombre" value={form.nombre} onChange={cambiar} placeholder="Tu nombre" required />
                </div>
                <div className="campo">
                  <label>Correo</label>
                  <input type="email" name="email" value={form.email} onChange={cambiar} placeholder="nombre@correo.cl" required />
                </div>
                <div className="campo">
                  <label>Mensaje</label>
                  <textarea name="mensaje" value={form.mensaje} onChange={cambiar} placeholder="¿En qué te ayudamos?" rows="4" required />
                </div>
                <button className="btn btn--solido btn--bloque">Enviar mensaje</button>
              </form>
            )}
          </div>
        </div>
      </section>
    </div>
  )
}
