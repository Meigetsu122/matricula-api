package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Patch;

import java.util.Map;

public class CalificarMatricula implements Task {

    private final Long idMatricula;
    private final String nota;

    public CalificarMatricula(Long idMatricula, String nota) {
        this.idMatricula = idMatricula;
        this.nota = nota;
    }

    public static CalificarMatricula conNota(Long idMatricula, String nota) {
        return new CalificarMatricula(idMatricula, nota);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String token = actor.recall("accessToken");
        double valor = Double.parseDouble(nota.replace(",", "."));

        actor.attemptsTo(
                Patch.to("/api/matriculas/" + idMatricula + "/nota")
                        .with(req -> req.contentType(ContentType.JSON)
                                .header("Authorization", "Bearer " + token)
                                .body(Map.of("nota", valor)))
        );
    }

    @Override
    public String toString() {
        return "registra la nota " + nota + " en la matrícula " + idMatricula;
    }
}