package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.interactions.Get;
import pe.edu.cibertec.matricula_api.screenplay.questions.ElHistorial;
import pe.edu.cibertec.matricula_api.screenplay.task.CalificarMatricula;
import pe.edu.cibertec.matricula_api.screenplay.task.ConsultarHistorial;

import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

public class CalificacionStepDefinitions {

    @And("{word} obtiene el id de su matrícula del curso {string}")
    public void obtieneElIdDeSuMatricula(String nombre, String curso) {
        Actor actor = theActorCalled(nombre);
        String token = actor.recall("accessToken");

        actor.attemptsTo(
                Get.resource("/api/matriculas")
                        .with(req -> req.queryParam("curso", curso)
                                .header("Authorization", "Bearer " + token))
        );

        Long id = SerenityRest.lastResponse().jsonPath().getLong("[0].id");
        actor.remember("idMatricula", id);
    }

    @When("{word} registra la nota {string} en la matrícula de {word}")
    public void registraLaNota(String docente, String nota, String alumna) {
        Long id = theActorCalled(alumna).recall("idMatricula");
        theActorCalled(docente).attemptsTo(CalificarMatricula.conNota(id, nota));
    }

    @And("la matrícula queda con condición {string}")
    public void laMatriculaQuedaConCondicion(String condicion) {
        String real = SerenityRest.lastResponse().jsonPath().getString("condicion");
        assertThat(real).isEqualTo(condicion);
    }

    @When("{word} consulta su historial académico")
    public void consultaSuHistorial(String nombre) {
        theActorCalled(nombre).attemptsTo(ConsultarHistorial.propio());
    }

    @And("el historial de {word} muestra {int} créditos aprobados y promedio {string}")
    public void elHistorialMuestra(String nombre, int creditos, String promedio) {
        Actor actor = theActorCalled(nombre);
        Number creditosReales = actor.asksFor(ElHistorial.creditosAprobados());
        Number promedioReal = actor.asksFor(ElHistorial.promedioPonderado());
        double esperado = Double.parseDouble(promedio.replace(",", "."));

        assertThat(creditosReales.intValue()).isEqualTo(creditos);
        assertThat(promedioReal.doubleValue()).isCloseTo(esperado, within(0.01));
    }
}