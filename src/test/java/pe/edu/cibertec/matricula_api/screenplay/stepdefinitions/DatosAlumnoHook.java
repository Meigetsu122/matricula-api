package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.BeforeAll;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatosAlumnoHook {

    @BeforeAll
    public static void mostrarDatosDelAlumno() {
        String fechaHora = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        System.out.println("=====================================");
        System.out.println("Alumno : Ayelen Odette Dolmos Cahuana");
        System.out.println("Código : i202508046");
        System.out.println("Fecha  : " + fechaHora);
        System.out.println("=====================================");
    }
}