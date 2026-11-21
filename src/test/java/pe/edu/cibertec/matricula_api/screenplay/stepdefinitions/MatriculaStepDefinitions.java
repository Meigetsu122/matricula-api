package pe.edu.cibertec.matricula_api.screenplay.stepdefinitions;

import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.response.Response;
import net.serenitybdd.rest.SerenityRest;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class MatriculaStepDefinitions {

    private static final String BASE = "http://localhost:8080";

    private static String token;
    private static Long idMatricula;
    private Response respuesta;

    @Dado("que el alumno {string} inicia sesión con la clave {string}")
    public void login(String usuario, String clave) {
        Response r = SerenityRest.given()
                .baseUri(BASE)
                .contentType(ContentType.JSON)
                .body(Map.of("username", usuario, "password", clave))
                .post("/api/auth/login");
        assertThat(r.statusCode()).isEqualTo(200);
        token = r.jsonPath().getString("accessToken");
    }

    @Cuando("cotizo el curso {string} para el alumno {string}")
    public void cotizar(String curso, String alumno) {
        respuesta = SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("codigoAlumno", alumno, "cursos", List.of(curso)))
                .post("/api/matriculas/cotizacion");
    }

    @Cuando("matriculo al alumno {string} en el curso {string}")
    public void matricular(String alumno, String curso) {
        respuesta = SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("codigoAlumno", alumno, "codigoCurso", curso))
                .post("/api/matriculas");
        if (respuesta.statusCode() == 201) {
            idMatricula = respuesta.jsonPath().getLong("id");
        }
    }

    @Cuando("pago la matrícula con el método {string}")
    public void pagar(String metodo) {
        respuesta = SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(Map.of("metodo", metodo))
                .patch("/api/matriculas/" + idMatricula + "/pagar");
    }

    @Cuando("consulto los pagos")
    public void consultarPagos() {
        respuesta = SerenityRest.given()
                .baseUri(BASE)
                .header("Authorization", "Bearer " + token)
                .get("/api/pagos");
    }

    @Entonces("el código de respuesta es {int}")
    public void codigoRespuesta(int esperado) {
        assertThat(respuesta.statusCode()).isEqualTo(esperado);
    }

    @Y("la cotización tiene {int} créditos, subtotal {string}, descuento {int} y total {string}")
    public void verificarCotizacion(int creditos, String subtotal, int descuento, String total) {
        assertThat(respuesta.jsonPath().getInt("creditos")).isEqualTo(creditos);
        assertThat(respuesta.jsonPath().getDouble("subtotal")).isEqualTo(Double.parseDouble(subtotal));
        assertThat(respuesta.jsonPath().getInt("descuento")).isEqualTo(descuento);
        assertThat(respuesta.jsonPath().getDouble("total")).isEqualTo(Double.parseDouble(total));
    }

    @Y("la matrícula tiene estado {string} y monto {string}")
    public void verificarMatricula(String estado, String monto) {
        assertThat(respuesta.jsonPath().getString("estado")).isEqualTo(estado);
        assertThat(respuesta.jsonPath().getDouble("monto")).isEqualTo(Double.parseDouble(monto));
    }

    @Y("la matrícula tiene estado {string} y fecha de pago con valor")
    public void verificarPagada(String estado) {
        assertThat(respuesta.jsonPath().getString("estado")).isEqualTo(estado);
        assertThat(respuesta.jsonPath().getString("fechaPago")).isNotBlank();
    }

    @Y("la respuesta cumple el schema {string}")
    public void cumpleSchema(String ruta) {
        respuesta.then().assertThat()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath(ruta));
    }

    @Entonces("existe el pago de la matrícula con método {string} y número de operación con formato {string}")
    public void verificarPago(String metodo, String formato) {
        List<Map<String, Object>> pagos = respuesta.jsonPath().getList("$");
        Map<String, Object> pago = pagos.stream()
                .filter(p -> ((Number) p.get("idMatricula")).longValue() == idMatricula)
                .findFirst()
                .orElseThrow(() -> new AssertionError("No hay pago para la matrícula " + idMatricula));
        assertThat(pago.get("metodo")).isEqualTo(metodo);
        assertThat((String) pago.get("numeroOperacion")).matches(formato);
    }

    @Y("el campo codigo del error es {string}")
    public void verificarCodigoError(String codigo) {
        assertThat(respuesta.jsonPath().getString("codigo")).isEqualTo(codigo);
    }
}