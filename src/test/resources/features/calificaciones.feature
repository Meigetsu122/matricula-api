Feature: Calificación de matrículas

  Scenario Outline: El docente registra la nota y el historial se actualiza
    Given que Rosa consulta la API de matricula
    And Rosa inicia sesión con el usuario "i202610005" y la clave "Cibertec2026"
    And que Marco consulta la API de matricula
    And Marco inicia sesión con el usuario "D0001" y la clave "Cibertec2026"
    And Rosa obtiene el id de su matrícula del curso "PI5620"
    When Marco registra la nota "<nota>" en la matrícula de Rosa
    Then el codigo de respuesta es 200
    And la matrícula queda con condición "<condicion>"
    When Rosa consulta su historial académico
    Then el codigo de respuesta es 200
    And la respuesta cumple el esquema "historial.json"
    And el historial de Rosa muestra <creditos> créditos aprobados y promedio "<promedio>"

    Examples:
      | nota | condicion   | creditos | promedio |
      | 16   | APROBADO    | 13       | 15,69    |
      | 12,5 | DESAPROBADO | 7        | 14,08    |

  Scenario: Un docente que no dicta el curso intenta registrar la nota
    Given que Rosa consulta la API de matricula
    And Rosa inicia sesión con el usuario "i202610005" y la clave "Cibertec2026"
    And que Lucia consulta la API de matricula
    And Lucia inicia sesión con el usuario "D0002" y la clave "Cibertec2026"
    And Rosa obtiene el id de su matrícula del curso "PI5620"
    When Lucia registra la nota "16" en la matrícula de Rosa
    Then el codigo de respuesta es 403
    And el error que recibe Lucia tiene el codigo "ACCESO_DENEGADO"
    And la respuesta cumple el esquema "error.json"

  Scenario: No se puede calificar una matrícula que no está pagada
    Given que Rosa consulta la API de matricula
    And Rosa inicia sesión con el usuario "i202610005" y la clave "Cibertec2026"
    And que Marco consulta la API de matricula
    And Marco inicia sesión con el usuario "D0001" y la clave "Cibertec2026"
    And Rosa obtiene el id de su matrícula del curso "PS5511"
    When Marco registra la nota "16" en la matrícula de Rosa
    Then el codigo de respuesta es 409
    And el error que recibe Marco tiene el codigo "MATRICULA_NO_PAGADA"
    And la respuesta cumple el esquema "error.json"