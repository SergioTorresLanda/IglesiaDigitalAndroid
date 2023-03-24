package mx.arquidiocesis.eamxcommonutils.common

enum class EAMXProfile(val rol: String) {
    Devoted("DEVOTED"), //Fiel
    DevotedAdmin("DEVOTED_ADMIN"), //Fiel_Administrador
    Priest("PRIEST"), //Sacerdote
    PriestAdmin("PRIEST_ADMIN"), //Sacerdote_Administrador
    DeanPriest("DEAN_PRIEST"), //Sacerdote_Decano
    CommunityResponsible("COMMUNITY_RESPONSIBLE"), //Responsable_comunidad
    CommunityAdmin("COMMUNITY_ADMIN"), //Administrador_comunidad
    CommunityMember("COMMUNITY_MEMBER"), //Miembro_comunidad_religioso
    VicariaClero("CLERGY_VICARAGE"), //Vicaría del Clero
    VicariaPastoral("PASTORAL_VICARAGE"), //Vicaría Pastoral
    VicariaVidaPastoral("CONSECRATED_LIFE_VICARAGE"), //Vicaría de Vida Pastoral
    GestorContenidos("CONTENT_MANAGER"), //Gestor de Contenidos
}