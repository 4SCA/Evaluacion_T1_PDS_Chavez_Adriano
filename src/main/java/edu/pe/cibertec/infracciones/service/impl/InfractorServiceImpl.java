package edu.pe.cibertec.infracciones.service.impl;

import edu.pe.cibertec.infracciones.dto.InfractorRequestDTO;
import edu.pe.cibertec.infracciones.dto.InfractorResponseDTO;
import edu.pe.cibertec.infracciones.dto.MultaRequestDTO;
import edu.pe.cibertec.infracciones.exception.InfractorNotFoundException;
import edu.pe.cibertec.infracciones.exception.VehiculoNotFoundException;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.VehiculoRepository;
import edu.pe.cibertec.infracciones.service.IInfractorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

import static edu.pe.cibertec.infracciones.model.EstadoMulta.PENDIENTE;
import static edu.pe.cibertec.infracciones.model.EstadoMulta.VENCIDA;

@Service
@RequiredArgsConstructor
public class InfractorServiceImpl implements IInfractorService {

    private final InfractorRepository infractorRepository;
    private final VehiculoRepository vehiculoRepository;
    private final MultaRepository multaRepository;

    @Override
    public InfractorResponseDTO registrarInfractor(InfractorRequestDTO dto) {
        Infractor infractor = new Infractor();
        infractor.setDni(dto.getDni());
        infractor.setNombre(dto.getNombre());
        infractor.setApellido(dto.getApellido());
        infractor.setEmail(dto.getEmail());
        infractor.setBloqueado(false);
        return mapToResponse(infractorRepository.save(infractor));
    }

    @Override
    public InfractorResponseDTO obtenerInfractorPorId(Long id) {
        Infractor infractor = infractorRepository.findById(id)
                .orElseThrow(() -> new InfractorNotFoundException(id));
        return mapToResponse(infractor);
    }

    @Override
    public List<InfractorResponseDTO> obtenerTodos() {
        return infractorRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void asignarVehiculo(Long infractorId, Long vehiculoId) {
        Infractor infractor = infractorRepository.findById(infractorId)
                .orElseThrow(() -> new InfractorNotFoundException(infractorId));
        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new VehiculoNotFoundException(vehiculoId));
        infractor.getVehiculos().add(vehiculo);
        infractorRepository.save(infractor);
    }

    @Override
    public double calcularDeudaByInfractor(Long id) {
        List<Multa> multas = multaRepository.findByInfractor_Id(id);
        double vencidas=0;
        double pendientes=0;
        double montoVencido=1.15;

        for (int i = 0; multas.size() > i; i++ ){
            if (multas.get(i).getEstado().equals(PENDIENTE)){
                pendientes += multas.get(i).getMonto();
        }else if(multas.get(i).getEstado().equals(VENCIDA)){
                vencidas += (multas.get(i).getMonto() * montoVencido);
            }
        }
        return pendientes+vencidas;
    }


    private InfractorResponseDTO mapToResponse(Infractor infractor) {
        InfractorResponseDTO dto = new InfractorResponseDTO();
        dto.setId(infractor.getId());
        dto.setDni(infractor.getDni());
        dto.setNombre(infractor.getNombre());
        dto.setApellido(infractor.getApellido());
        dto.setEmail(infractor.getEmail());
        dto.setBloqueado(infractor.isBloqueado());
        return dto;
    }
}