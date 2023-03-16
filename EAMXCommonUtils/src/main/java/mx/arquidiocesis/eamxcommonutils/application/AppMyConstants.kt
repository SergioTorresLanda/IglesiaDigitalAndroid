package mx.arquidiocesis.eamxcommonutils.application

import mx.arquidiocesis.eamxcommonutils.BuildConfig
import mx.arquidiocesis.eamxcommonutils.common.EAMXEnumsEndPoint
import java.util.*

object AppMyConstants {
    val BASE_URL = "${ConstansApp.hostUser()}user/"
    val SIGN_IN_END_POINT = EAMXEnumsEndPoint.LOGIN.name.toLowerCase(Locale.ROOT)
    val SIGN_UP_END_POINT = EAMXEnumsEndPoint.SIGNUP.name.toLowerCase(Locale.ROOT)
    val CONFIRM_CODE_END_POINT = EAMXEnumsEndPoint.CONFIRM.name.toLowerCase(Locale.ROOT)
    val RESEND_CODE_END_POINT = EAMXEnumsEndPoint.RESEND_CODE.name.toLowerCase(Locale.ROOT)
    val USER_INFO_END_PORINT = EAMXEnumsEndPoint.INFO.name.toLowerCase(Locale.ROOT)
    val LOG_OUT_END_POINT = EAMXEnumsEndPoint.LOG_OUT.name.toLowerCase(Locale.ROOT)
    val UPDATE_END_POINT = EAMXEnumsEndPoint.UPDATE.name.toLowerCase(Locale.ROOT)

    val BASE_URLC = ConstansApp.hostUser()
    val CREATE_DINER = EAMXEnumsEndPoint.CREATEC.name.toLowerCase(Locale.ROOT)

    const val BASE_NEWS_URL = "https://api.arquidiocesis.mx/"

    const val PUBLICATIONS_MAKE_END_POINT = "publications/make"
    const val PUBLICATIONS_ALL_END_POINT = "publications/all"
    const val REACTIONS_ALL_END_POINT = "reactions/all"
    const val REACTIONS_IN_PUBLICATION_END_POINT = "reactions/getinpublication"
    const val PUBLICATIONS_DELETE_END_POINT = "publications/delete"
    const val REACTPUBLICATION_END_POINT = "reactions/reactpublication"
    const val REACTCOMMENT_END_POINT = "reactions/reactcomment"
    const val REACTIONS_EDIT_END_POINT = "publications/edit"
    const val COMMENTS_ALL_END_POINT = "comments/all"
    const val REACTIONSTOP_END_POINT = "reactions/top"
    const val MAKE_IN_PUBLICATION_END_POINT = "comments/makeinpublication"
    const val COMMENTS_DETAIL_END_POINT = "comments/detail"
    const val COMMENTS_FIND_END_POINT = "comments/find"
    const val FEELINGSALL_END_POINT = "feelings/all"
    const val GROUPS_ALL_END_POINT = "groups/all_v2"
    const val MAKE_IN_COMMENT_ALL_END_POINT = "comments/makeincomment"
    const val PROFILE_END_POINT = "employees/profile"
    const val PUBLICATION_WATCH_END_POINT = "publications/watch"

    /*Titulos para los toolbar de cada Fragment*/
    const val evento = "Actividades y voluntariado"
    const val detailEvento = "Alta de comedores"
    const val perfil = "Perfil"
    const val expanded = "Expanded"
    const val servicios = "Servicios"
    const val otros_servicios = "Otros servicios"
    const val bendiciones_title = "Bendiciones"
    const val sacramentos = "Sacramentos"
    const val oraciones = "Oraciones"
    const val miIglesia = "Mi iglesia"
    const val misas = "Misas"
    const val formacion = "Biblioteca virtual"
    const val administrar_modulos = "Administrar modulos"
    const val nombrar_administradores = "Nombrar Administradores"
    const val red_social = "Red social"
    const val noticias = "Noticias"
    const val eventos_vivo = "Eventos en vivo"
    const val sos = "S.O.S"
    const val notificaciones = "Notificaciones"
    const val sosServicios = "S.O.S Servicios"
    const val cadenaOracion = "Cadena de oración"
    const val ofrendaTarjeta = "Ofrenda con tarjeta"
    const val promesas = "Promesas"
    const val create_post = "Crear Publicación"
    const val intentions = "Intenciones"
    const val intentionsCommunity = "Intenciones para comunidad"
    const val home = ""
    const val comentarios = "Comentarios"
    const val editarComunidad = "Editar Comunidad"
    const val comunidad = "Mis comunidades"
    const val comunidadAdd = "Mis comunidades "
    const val ofrenda = "Mi ofrenda"
    const val solicitarIntencion = "Agendar"
}