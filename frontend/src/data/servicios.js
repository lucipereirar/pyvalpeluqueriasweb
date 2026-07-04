// ============================================================
// SERVICIOS DEL SALÓN — solo frontend.
// Estos valores son los precios ACTUALIZADOS que se muestran en
// la página (no provienen de los microservicios). Edita aquí para
// mantener la lista y las tarifas al día.
// Precios en pesos chilenos (CLP).
// ============================================================

export const categoriasServicios = [
  {
    id: 'cabello',
    nombre: 'Cabello',
    descripcion: 'Corte, peinado y tratamiento para todo tipo de pelo.',
    icono: 'tijeras',
    servicios: [
      { nombre: 'Corte mujer', detalle: 'Lavado, corte y peinado', precio: 18000, duracion: '50 min' },
      { nombre: 'Corte hombre', detalle: 'Corte y acabado', precio: 12000, duracion: '35 min' },
      { nombre: 'Corte niño/niña', detalle: 'Hasta 10 años', precio: 9000, duracion: '30 min' },
      { nombre: 'Peinado / brushing', detalle: 'Ondas o liso', precio: 15000, duracion: '40 min' },
      { nombre: 'Tratamiento hidratación', detalle: 'Nutrición profunda', precio: 22000, duracion: '45 min' },
    ],
  },
  {
    id: 'color',
    nombre: 'Color',
    descripcion: 'Coloración, mechas y matices con productos de primera.',
    icono: 'gota',
    servicios: [
      { nombre: 'Color raíz', detalle: 'Retoque de raíz', precio: 28000, duracion: '75 min' },
      { nombre: 'Color completo', detalle: 'Cabello completo', precio: 38000, duracion: '110 min' },
      { nombre: 'Mechas / balayage', detalle: 'Iluminación natural', precio: 55000, duracion: '150 min' },
      { nombre: 'Matiz / tono', detalle: 'Neutraliza el color', precio: 16000, duracion: '40 min' },
    ],
  },
  {
    id: 'unas',
    nombre: 'Uñas',
    descripcion: 'Manicure y pedicure para manos y pies impecables.',
    icono: 'mano',
    servicios: [
      { nombre: 'Manicure tradicional', detalle: 'Limado y esmaltado', precio: 12000, duracion: '40 min' },
      { nombre: 'Manicure permanente', detalle: 'Esmalte semipermanente', precio: 16000, duracion: '55 min' },
      { nombre: 'Pedicure spa', detalle: 'Exfoliación e hidratación', precio: 18000, duracion: '60 min' },
    ],
  },
  {
    id: 'estetica',
    nombre: 'Estética',
    descripcion: 'Rostro y mirada, con un enfoque relajante y natural.',
    icono: 'hoja',
    servicios: [
      { nombre: 'Diseño de cejas', detalle: 'Perfilado y depilación', precio: 8000, duracion: '25 min' },
      { nombre: 'Lifting de pestañas', detalle: 'Curvatura natural', precio: 20000, duracion: '60 min' },
      { nombre: 'Limpieza facial', detalle: 'Piel luminosa', precio: 25000, duracion: '55 min' },
      { nombre: 'Maquillaje social', detalle: 'Eventos y ocasiones', precio: 24000, duracion: '50 min' },
    ],
  },
]
