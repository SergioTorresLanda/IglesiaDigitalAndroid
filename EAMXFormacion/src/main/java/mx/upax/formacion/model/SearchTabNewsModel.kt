package mx.upax.formacion.model

data class SearchTabNewsModel(
    val news : List<NewsModel>,
    val featured: List<FeaturedModel>
)