package mx.upax.formacion.model

class NewsModel(
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

fun NewsModel.converterToFeaturedModel() : FeaturedModel{
    return FeaturedModel(
        id = this.id,
        image = this.image,
        title = this.title,
        subtitle = this.subtitle,
        tags = this.tags,
        views = this.views,
        type = this.type,
        url = this.url,
    )
}