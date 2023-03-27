package mx.arquidiocesis.eamxevent.model.enum

enum class Type_donation(var pos: Int, var don: String) {
    Seleccione(0, "Seleccione"),
    Efectivo(1, "Efectivo"),
    Especie(2, "Especie"),
}