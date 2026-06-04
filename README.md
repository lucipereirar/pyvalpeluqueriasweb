# pyvalpeluqueriasweb
Este proyecto busca construir una página web funcional para la venta de productos asociados al área de belleza y de publicitar el negocio junto con mostrar información relacionada al mismo

# Base de datos
Copiar y pegar sentencia en workbench para la creación de la base de datos: 

CREATE DATABASE peluqueria_auth;
CREATE DATABASE peluqueria_productos;
CREATE DATABASE peluqueria_carrito;
CREATE DATABASE peluqueria_pedidos;
CREATE DATABASE peluqueria_pago;
CREATE DATABASE peluqueria_despacho;
CREATE DATABASE peluqueria_certificacion;
CREATE DATABASE peluqueria_notificaciones;
CREATE DATABASE peluqueria_reportes;

# Arranque de los servicios
Ejecutar desde la raíz del proyecto:

powershell -ExecutionPolicy Bypass -File .\setup_admin.ps1
powershell -ExecutionPolicy Bypass -File .\start_all_services.ps1
