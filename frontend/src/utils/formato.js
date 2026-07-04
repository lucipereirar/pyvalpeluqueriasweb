// Formatea un número como precio en pesos chilenos (CLP): $12.990
export function clp(valor) {
  const n = Number(valor) || 0
  return n.toLocaleString('es-CL', {
    style: 'currency',
    currency: 'CLP',
    maximumFractionDigits: 0,
  })
}
