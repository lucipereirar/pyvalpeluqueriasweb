import { api } from './client'

// Reportes: microservicio ms-reportes (solo rol ADMIN).
// Analítica de ventas en JSON y exportación a Excel (Apache POI).
export const reportesApi = {
  resumenVentas: (desde, hasta) => {
    const qs = new URLSearchParams()
    if (desde) qs.set('desde', desde)
    if (hasta) qs.set('hasta', hasta)
    const sufijo = qs.toString() ? `?${qs}` : ''
    return api.get(`/reportes/ventas/resumen${sufijo}`, { auth: true })
  },
  descargarExcel: async (desde, hasta) => {
    const qs = new URLSearchParams()
    if (desde) qs.set('desde', desde)
    if (hasta) qs.set('hasta', hasta)
    const sufijo = qs.toString() ? `?${qs}` : ''
    const blob = await api.blob(`/reportes/ventas/excel${sufijo}`, { auth: true })
    const url = URL.createObjectURL(blob)
    const a = document.createElement('a')
    a.href = url
    a.download = 'reporte_ventas.xlsx'
    a.click()
    URL.revokeObjectURL(url)
  },
}
