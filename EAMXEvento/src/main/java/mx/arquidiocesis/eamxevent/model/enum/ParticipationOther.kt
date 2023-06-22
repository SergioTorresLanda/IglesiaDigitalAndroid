package mx.arquidiocesis.eamxevent.model.enum

enum class ParticipationOther(var pos: Int, var del: String) {
    Select(0, "-Seleccionar-"),
    Donante(1, "Donante"),
    Voluntario(2, "Voluntario"),
    Asistente(3, "Asistente")
}