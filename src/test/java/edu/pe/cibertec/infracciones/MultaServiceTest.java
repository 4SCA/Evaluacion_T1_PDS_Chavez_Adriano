package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.exception.InfractorBloqueadoException;
import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Vehiculo;
import edu.pe.cibertec.infracciones.repository.InfractorRepository;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.VehiculoRepository;
import edu.pe.cibertec.infracciones.service.impl.MultaServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static edu.pe.cibertec.infracciones.model.EstadoMulta.PENDIENTE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MultaServiceTest {
    @Mock
    MultaRepository multaRepository;

    @Mock
    InfractorRepository infractorRepository;

    @InjectMocks
    MultaServiceImpl multaService;
    @Mock
    VehiculoRepository vehiculoRepository;

    @Test
    @DisplayName("Transfering ticket from infractor A to infractor B")
    void transferingTicketFromInfractorA_ToInfractorB_whenInfractorBIsNotBLockedAndTicketIsntPending_ThenTransfer(){

        Infractor a = new Infractor(1L, "A", false, List.of());
        Infractor b = new Infractor(2L, "B", false,List.of());
        Vehiculo vehiculo = new Vehiculo(1L,List.of(a));
        Multa multa = new Multa(1L, PENDIENTE,a, vehiculo);

        a.getVehiculos().add(vehiculo);
        b.getVehiculos().add(vehiculo);

        List<Multa> multas = new ArrayList<>();

        when(multaRepository.findByInfractorAndVehicle(1L,1L)).thenReturn(multas);
        when(infractorRepository.findById(2L)).thenReturn(Optional.of(b));
        when(multaRepository.save(any(Multa.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        multaService.transferirMulta(1L,b.getId());
        assertEquals(b, multa.getInfractor());
    }

    @Test
    @DisplayName("Should throw exception and not save when infractor B is blocked")
    void transferingTicket_WhenInfractorBIsBlocked_ThenThrowExceptionAndDoNotSave() {

        // Arrange
        Infractor a = new Infractor(1L, "A", false, new ArrayList<>());
        Infractor b = new Infractor(2L, "B", true, new ArrayList<>());
        Vehiculo vehiculo = new Vehiculo(1L, new ArrayList<>());
        Multa multa = new Multa(1L, PENDIENTE, a, vehiculo);

        a.getVehiculos().add(vehiculo);
        b.getVehiculos().add(vehiculo);

        when(multaRepository.findById(1L)).thenReturn(Optional.of(multa));
        when(infractorRepository.findById(2L)).thenReturn(Optional.of(b));


        ArgumentCaptor<Multa> multaCaptor = ArgumentCaptor.forClass(Multa.class);

        assertThrows(InfractorBloqueadoException.class, () -> {
            multaService.transferirMulta(1L, b.getId());
        });

        verify(multaRepository, times(0)).save(any(Multa.class));
    }
}
