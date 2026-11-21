package pe.edu.cibertec.matricula_api.screenplay.task;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Task;
import net.serenitybdd.screenplay.rest.interactions.Post;

import java.util.Map;

public class RenovarToken implements Task {

    private final String claveDeMemoria;

    public RenovarToken(String claveDeMemoria) {
        this.claveDeMemoria = claveDeMemoria;
    }

    public static RenovarToken conElRefreshGuardado() {
        return new RenovarToken("refreshToken");
    }

    public static RenovarToken conElRefreshOriginal() {
        return new RenovarToken("refreshOriginal");
    }

    @Override
    public <T extends Actor> void performAs(T actor) {
        String refresh = actor.recall(claveDeMemoria);

        actor.attemptsTo(
                Post.to("/api/auth/refresh")
                        .with(req -> req.contentType(ContentType.JSON)
                                .body(Map.of("refreshToken", refresh)))
        );

        Response respuesta = SerenityRest.lastResponse();
        if (respuesta.statusCode() == 200) {
            actor.remember("accessToken", respuesta.jsonPath().getString("accessToken"));
            actor.remember("refreshToken", respuesta.jsonPath().getString("refreshToken"));
        }
    }

    @Override
    public String toString() {
        return "renueva su sesión con el refresh token (" + claveDeMemoria + ")";
    }
}