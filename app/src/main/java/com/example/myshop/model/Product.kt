package com.example.myshop.model

data class Product(
    var pid: String = "",
    var user: User = User(),
    var images: List<String> = emptyList(),
    var name: String = "",
    var description: String = "",
    var category: List<String> = emptyList(),
    var variations: List<Variation> = emptyList(),
    var stocks: List<Stock> = emptyList(),
    var price: Long = 0,
    var reviews: List<Review> = emptyList(),
    var sold: Long = 0
) {
    fun getCategory() : String {
        var result = ""
        category.forEachIndexed { index, value ->
            result += value
            if (index != category.size - 1) {
                result += " > "
            }
        }
        return result
    }
    fun getAvgRate() : Float {
        var sum = 0
        var avgRate = 0f
        if (this.reviews.isNotEmpty()) {
            this.reviews.forEach {
                sum += it.rate
            }
            avgRate = sum.toFloat() / this.reviews.size
        }
        return avgRate
    }

    fun getSold() : String {
        return if (this.sold >= 1000000) {
            String.format("%.1fm", this.sold / 1000000f)
        } else if (this.sold >= 1000) {
            String.format("%.1fk", this.sold / 1000f)
        } else {
            this.sold.toString()
        }
    }
}


