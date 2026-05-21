package com.peluqueria.ms_reportes.repository;

import com.peluqueria.ms_reportes.model.Reporte;
import com.peluqueria.ms_reportes.model.TipoReporte;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReporteRepository extends JpaRepository<Reporte, Long> {
    List<Reporte> findByTipo(TipoReporte tipo);
    List<Reporte> findByGeneradoPorId(Long generadoPorId);
    List<Reporte> findAllByOrderByFechaGeneracionDesc();
}
