package mx.arquidiocesis.eamxevent.model.enum

enum class Participation(var pos: Int, var del: String) {
    Donante(0, "Donante"),
    Voluntario(1, "Voluntario"),
    Asistente(2, "Asistente")
}