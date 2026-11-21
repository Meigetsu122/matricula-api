package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

public class IniciarSesion implements Task {

    private final String usuario;
    private final String clave;

    public IniciarSesion(String usuario, String clave) {
        this.usuario = usuario;
        this.clave = clave;
    }

    public static IniciarSesion conCredenciales(String usuario, String clave) {
        return new IniciarSesion(usuario, clave);
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        actor.attemptsTo(
                Post.to("/api/auth/login")
                        .with(req -> req.contentType(ContentType.JSON)
                                .body(Map.of("username", usuario, "password", clave)))
        );

        Response respuesta = SerenityRest.lastResponse();
        if (respuesta.statusCode() == 200) {
            String refresh = respuesta.jsonPath().getString("refreshToken");
            actor.remember("accessToken", respuesta.jsonPath().getString("accessToken"));
            actor.remember("refreshToken", refresh);
            actor.remember("refreshOriginal", refresh);
            actor.remember("usuario", usuario);
        }
    }

    @Override
    public String toString() {
        return "inicia sesión con el usuario " + usuario;
    }
}