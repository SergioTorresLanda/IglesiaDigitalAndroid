package mx.upax.formacion.model

class FeaturedModel(
    id: Int,
    image: String,
    title: String,
    subtitle: String,
    tags: String,
    views: Int,
    type: String,
    url: String
): BaseModel(
    id, image, title, subtitle, tags, views, type, url
)