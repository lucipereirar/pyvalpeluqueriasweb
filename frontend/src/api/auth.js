import { api } from './client'

// Autenticación: microservicio ms-auth (/api/auth/login, /api/auth/register).
export const authApi = {
  login: (email, password) => api.post('/auth/login', { email, password }),
  register: (datos) => api.post('/auth/register', datos),
}
