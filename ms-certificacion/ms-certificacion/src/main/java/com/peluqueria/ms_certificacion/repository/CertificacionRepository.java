package com.peluqueria.ms_certificacion.repository;

import com.peluqueria.ms_certificacion.model.Certificacion;
import com.peluqueria.ms_certificacion.model.EstadoCertificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface CertificacionRepository extends JpaRepository<Certificacion, Long> {
    List<Certificacion> findByUsuarioId(Long usuarioId);
    List<Certificacion> findByEstado(EstadoCertificacion estado);
    Optional<Certificacion> findByCodigoVerificacion(String codigoVerificacion);
}
