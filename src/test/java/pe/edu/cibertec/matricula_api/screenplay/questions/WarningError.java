package pe.edu.cibertec.matricula_api.screenplay.questions;

import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.Question;

public class WarningError implements Question<String> {

    public static WarningError delUltimoIntento() {
        return new WarningError();
    }

    @Override
    public String answeredBy(Actor actor) {
        return SerenityRest.lastResponse().jsonPath().getString("codigo");
    }

    @Override
    public String toString() {
        return "el codigo del error";
    }
}