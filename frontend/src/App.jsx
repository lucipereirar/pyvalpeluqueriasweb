import { Routes, Route } from 'react-router-dom'
import Navbar from './components/Navbar'
import Footer from './components/Footer'
import CartDrawer from './components/CartDrawer'
import { RequiereSesion, RequiereAdmin } from './components/RutaProtegida'
import Home from './pages/Home'
import Servicios from './pages/Servicios'
import Catalogo from './pages/Catalogo'
import QuienesSomos from './pages/QuienesSomos'
import Contacto from './pages/Contacto'
import Login from './pages/Login'
import Registro from './pages/Registro'
import Checkout from './pages/Checkout'
import Confirmacion from './pages/Confirmacion'
import MisPedidos from './pages/MisPedidos'
import Admin from './pages/admin/Admin'

export default function App() {
  return (
    <>
      <Navbar />
      <CartDrawer />
      <main>
        <Routes>
          <Route path="/" element={<Home />} />
          <Route path="/servicios" element={<Servicios />} />
          <Route path="/tienda" element={<Catalogo />} />
          <Route path="/quienes-somos" element={<QuienesSomos />} />
          <Route path="/contacto" element={<Contacto />} />
          <Route path="/ingresar" element={<Login />} />
          <Route path="/registro" element={<Registro />} />
          <Route path="/checkout" element={<RequiereSesion><Checkout /></RequiereSesion>} />
          <Route path="/confirmacion/:pedidoId" element={<RequiereSesion><Confirmacion /></RequiereSesion>} />
          <Route path="/mis-pedidos" element={<RequiereSesion><MisPedidos /></RequiereSesion>} />
          <Route path="/admin" element={<RequiereAdmin><Admin /></RequiereAdmin>} />
        </Routes>
      </main>
      <Footer />
    </>
  )
}
