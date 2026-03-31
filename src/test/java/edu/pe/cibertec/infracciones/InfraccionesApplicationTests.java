package edu.pe.cibertec.infracciones;


import edu.pe.cibertec.infracciones.model.Infractor;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.InfractorServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import static edu.pe.cibertec.infracciones.model.EstadoMulta.PENDIENTE;
import static edu.pe.cibertec.infracciones.model.EstadoMulta.VENCIDA;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InfraccionesApplicationTests {

//    Reglas de negocio:
//            • La deuda de un infractor es la suma de los montos de todas sus multas PENDIENTES y VENCIDAS. Las
//    multas VENCIDAS acumulan un recargo del 15% sobre su monto original.
//            • Un vehículo solo puede desasignarse de un infractor si no tiene multas PENDIENTES activas. Si el vehículo
//    estaba suspendido y no quedan más infractores asignados con multas activas, se reactiva
//    automáticamente.
//• Una multa puede transferirse a otro infractor siempre que: el nuevo infractor no esté bloqueado, el
//    vehículo de la multa le pertenezca y la multa esté en estado PENDIENTE.
//
    @Mock
    MultaRepository multaRepository;

    @InjectMocks
    InfractorServiceImpl infractorService;

	@Test
	void contextLoads() {
	}



    @Test
    @DisplayName("Calculate total lawbreaker tickets if is EXPIRED or PENDING")
    void givenLawbreakerId_whenStateIsExpiredOrPending_thenReturnTotalWithExtraChargeIfExpired()
    {
        //ARRANGE
        Long infractorId= 1L;
        Infractor infractor = new Infractor(1L, "Juan","Perez");
        List<Multa> multas =List.of(
                new Multa(1L, infractor, PENDIENTE,200.00),
                new Multa(2L, infractor, VENCIDA,300.00)
        );
        when(multaRepository.findByInfractor_Id(infractorId)).thenReturn(multas);

        double resultado = infractorService.calcularDeudaByInfractor(infractorId);

        assertEquals(545.0, resultado);
    }

}
