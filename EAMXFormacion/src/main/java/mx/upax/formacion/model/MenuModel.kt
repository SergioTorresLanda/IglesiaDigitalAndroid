package mx.upax.formacion.model

class MenuModel (
    val data : List<ItemMenu>
)

class ItemMenu(
    val code: String,
    val name: String,
    val icon_url: String,
    val icon_pressed_url: String,
    var pressed: Boolean = false
)