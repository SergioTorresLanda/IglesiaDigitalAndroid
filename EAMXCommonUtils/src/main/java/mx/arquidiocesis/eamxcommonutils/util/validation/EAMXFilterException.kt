package mx.arquidiocesis.eamxcommonutils.util.validation

enum class EAMXFilterException(val exceptionsCharacters: ArrayList<Char>) {
    EMAIL(arrayListOf('@','.','-','_'))
}