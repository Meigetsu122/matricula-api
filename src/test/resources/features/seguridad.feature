Feature: Seguridad de la sesión

  Scenario: Rotación del refresh token
    Given que Pedro consulta la API de matricula
    And Pedro inicia sesión con el usuario "i202610006" y la clave "Cibertec2026"
    Then el codigo de respuesta es 200
    When Pedro renueva su sesión con el refresh token guardado
    Then el codigo de respuesta es 200
    When Pedro renueva su sesión con el refresh token original
    Then el codigo de respuesta es 401
    And el error que recibe Pedro tiene el codigo "REFRESH_REUTILIZADO"
    And la respuesta cumple el esquema "error.json"
    When Pedro consulta su perfil
    Then el codigo de respuesta es 401
    And el error que recibe Pedro tiene el codigo "TOKEN_INVALIDO"
    And la respuesta cumple el esquema "error.json"

  Scenario: Cierre de sesión
    Given que Pedro consulta la API de matricula
    And Pedro inicia sesión con el usuario "i202610006" y la clave "Cibertec2026"
    When Pedro cierra sesión
    Then el codigo de respuesta es 204
    When Pedro consulta su perfil
    Then el codigo de respuesta es 401
    And el error que recibe Pedro tiene el codigo "TOKEN_INVALIDO"
    And la respuesta cumple el esquema "error.json"

  Scenario Outline: Bloqueo de cuenta por intentos fallidos
    Given que Jorge consulta la API de matricula
    When Jorge inicia sesión con el usuario "i202610004" y la clave "<clave>"
    Then el codigo de respuesta es <status>
    And el error que recibe Jorge tiene el codigo "<codigo>"
    And la respuesta cumple el esquema "error.json"

    Examples:
      | intento            | clave        | status | codigo                |
      | 1 (clave mala)     | mala1        | 401    | CREDENCIALES_INVALIDAS |
      | 2 (clave mala)     | mala2        | 401    | CREDENCIALES_INVALIDAS |
      | 3 (clave mala)     | mala3        | 401    | CREDENCIALES_INVALIDAS |
      | 4 (clave mala)     | mala4        | 401    | CREDENCIALES_INVALIDAS |
      | 5 (clave mala)     | mala5        | 423    | CUENTA_BLOQUEADA      |
      | 6 (clave correcta) | Cibertec2026 | 423    | CUENTA_BLOQUEADA      |

  Scenario: Alumno sin permiso de administrador
    Given que Pedro consulta la API de matricula
    And Pedro inicia sesión con el usuario "i202610006" y la clave "Cibertec2026"
    When Pedro intenta listar a todos los alumnos
    Then el codigo de respuesta es 403
    And el error que recibe Pedro tiene el codigo "ACCESO_DENEGADO"
    And la respuesta cumple el esquema "error.json"