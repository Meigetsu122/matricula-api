Feature: Matrícula del alumno

  Background:
    Given que el alumno "i202610006" inicia sesión con la clave "Cibertec2026"

  Scenario: Cotizar el curso
    When cotizo el curso "DM5512" para el alumno "i202610006"
    Then el código de respuesta es 200
    And la cotización tiene 4 créditos, subtotal "480.0", descuento 0 y total "480.0"

  Scenario: Matricularse y pagar el curso
    When matriculo al alumno "i202610006" en el curso "DM5512"
    Then el código de respuesta es 201
    And la matrícula tiene estado "REGISTRADA" y monto "480.0"
    And la respuesta cumple el schema "schemas/matricula.json"
    When pago la matrícula con el método "TRANSFERENCIA"
    Then el código de respuesta es 200
    And la matrícula tiene estado "PAGADA" y fecha de pago con valor
    When consulto los pagos
    Then existe el pago de la matrícula con método "TRANSFERENCIA" y número de operación con formato "OP-\d{6}"

  Scenario: Regla de negocio que debe fallar
    When matriculo al alumno "i202610006" en el curso "QA5517"
    Then el código de respuesta es 422
    And el campo codigo del error es "PRERREQUISITO_PENDIENTE"
    And la respuesta cumple el schema "schemas/error.json"