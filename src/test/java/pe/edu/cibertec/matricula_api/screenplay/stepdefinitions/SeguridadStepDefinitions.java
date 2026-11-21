package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.en.And;
import io.cucumber.java.en.When;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.interactions.Get;
import pe.edu.cibertec.matricula_api.screenplay.questions.WarningError;
import pe.edu.cibertec.matricula_api.screenplay.task.CerrarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.IniciarSesion;
import pe.edu.cibertec.matricula_api.screenplay.task.RenovarToken;

import static net.serenitybdd.screenplay.GivenWhenThen.seeThat;
import static net.serenitybdd.screenplay.actors.OnStage.theActorCalled;
import static org.hamcrest.Matchers.equalTo;

public class SeguridadStepDefinitions {

    @When("{word} inicia sesión con el usuario {string} y la clave {string}")
    public void iniciaSesion(String nombre, String usuario, String clave) {
        theActorCalled(nombre).attemptsTo(IniciarSesion.conCredenciales(usuario, clave));
    }

    @When("{word} renueva su sesión con el refresh token guardado")
    public void renuevaConElGuardado(String nombre) {
        theActorCalled(nombre).attemptsTo(RenovarToken.conElRefreshGuardado());
    }

    @When("{word} renueva su sesión con el refresh token original")
    public void renuevaConElOriginal(String nombre) {
        theActorCalled(nombre).attemptsTo(RenovarToken.conElRefreshOriginal());
    }

    @When("{word} cierra sesión")
    public void cierraSesion(String nombre) {
        theActorCalled(nombre).attemptsTo(CerrarSesion.ahora());
    }

    @When("{word} consulta su perfil")
    public void consultaSuPerfil(String nombre) {
        Actor actor = theActorCalled(nombre);
        String token = actor.recall("accessToken");
        actor.attemptsTo(
                Get.resource("/api/auth/perfil")
                        .with(req -> req.header("Authorization", "Bearer " + token))
        );
    }

    @When("{word} intenta listar a todos los alumnos")
    public void intentaListarAlumnos(String nombre) {
        Actor actor = theActorCalled(nombre);
        String token = actor.recall("accessToken");
        actor.attemptsTo(
                Get.resource("/api/alumnos")
                        .with(req -> req.header("Authorization", "Bearer " + token))
        );
    }

    @And("el error que recibe {word} tiene el codigo {string}")
    public void elErrorTieneElCodigo(String nombre, String codigo) {
        theActorCalled(nombre).should(seeThat(WarningError.delUltimoIntento(), equalTo(codigo)));
    }
}