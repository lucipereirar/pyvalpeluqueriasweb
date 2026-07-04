// Cliente HTTP base para el API Gateway.
// En desarrollo, VITE_API_URL = '/api' y Vite proxea a http://localhost:8080.
const BASE = import.meta.env.VITE_API_URL || '/api'

function token() {
  return localStorage.getItem('pyval_token')
}

async function request(path, { method = 'GET', body, auth = false, blob = false } = {}) {
  const headers = {}
  if (body) headers['Content-Type'] = 'application/json'
  if (auth && token()) headers.Authorization = `Bearer ${token()}`

  const res = await fetch(`${BASE}${path}`, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  })

  if (res.status === 204) return null

  if (blob) {
    if (!res.ok) throw new Error(`Error ${res.status}`)
    return res.blob()
  }

  const text = await res.text()
  const data = text ? JSON.parse(text) : null

  if (!res.ok) {
    const mensaje = (data && (data.message || data.error)) || `Error ${res.status}`
    throw new Error(mensaje)
  }
  return data
}

export const api = {
  get: (p, opts) => request(p, { ...opts, method: 'GET' }),
  post: (p, body, opts) => request(p, { ...opts, method: 'POST', body }),
  put: (p, body, opts) => request(p, { ...opts, method: 'PUT', body }),
  patch: (p, body, opts) => request(p, { ...opts, method: 'PATCH', body }),
  del: (p, opts) => request(p, { ...opts, method: 'DELETE' }),
  blob: (p, opts) => request(p, { ...opts, method: 'GET', blob: true }),
}
