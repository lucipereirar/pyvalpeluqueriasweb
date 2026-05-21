package com.peluqueria.ms_productos.repository;

import com.peluqueria.ms_productos.model.Categoria;
import com.peluqueria.ms_productos.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByActivoTrue();
    List<Producto> findByCategoriaAndActivoTrue(Categoria categoria);
    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);
}
