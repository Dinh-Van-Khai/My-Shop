package com.example.myshop.component.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.myshop.component.BaseViewModel
import com.example.myshop.model.Product
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class HomeViewModel : BaseViewModel<HomeState, HomeEvent>(HomeState()) {
    init {
        //For all (event not logged in)
        getEventImages()
        getCategories()
        getAllProduct()
        getSearchResult()
        setCurrentCategory("All")

        //For users logged in
        getNumberOfCart()
    }

    private fun getEventImages() {
        _state.update { it.copy(eventImages = listOf(
            "https://scontent.fhan4-4.fna.fbcdn.net/v/t39.30808-6/466110818_1013122277496574_5386451238480269366_n.jpg?_nc_cat=102&ccb=1-7&_nc_sid=127cfc&_nc_eui2=AeH6_n1xRUfvux9oUVEG_JHR30mw87b-KGzfSbDztv4obJyFl7QornFPoomSnWfkS4n5w7T7Enq-lc39uSlFK0kf&_nc_ohc=bH7xsvovFj8Q7kNvgGfM6XX&_nc_zt=23&_nc_ht=scontent.fhan4-4.fna&_nc_gid=ANmADc9H62mhObraX77_eSm&oh=00_AYCdQx6zfJ2MKIOJigg8lywY2NF10I5p6oTbnaJUOTAF0A&oe=67357A69",
            "https://scontent.fhan3-5.fna.fbcdn.net/v/t39.30808-6/465901906_1101448328434968_2034141683515634217_n.jpg?stp=dst-jpg_p526x296&_nc_cat=109&ccb=1-7&_nc_sid=127cfc&_nc_eui2=AeHR-wFo3b-H2MbvwcUeGuB5dIwlz5-PUhV0jCXPn49SFUjxSUybDi0heiS9oEAlmSORNi-9Spd4Ke1V8v2LnmJV&_nc_ohc=hY4eDa-mzccQ7kNvgGns-hv&_nc_zt=23&_nc_ht=scontent.fhan3-5.fna&_nc_gid=AKXZ6nJXJS-9sU3a_pAmaRB&oh=00_AYCWGU8QIqNNWQKl8DU6_HNyA02-GtRboGEly0sFIbP7wQ&oe=67356E3C",
            "https://scontent.fhan4-1.fna.fbcdn.net/v/t39.30808-6/465704362_2562091177513772_7994350718385231314_n.jpg?stp=dst-jpg_s960x960&_nc_cat=104&ccb=1-7&_nc_sid=aa7b47&_nc_eui2=AeFvYwUIuWEc9H3SdD3CSvjrFMJuv7H4w0gUwm6_sfjDSEqKmwPPN6X4yVAOKqHGO150Kig4emV8coDUdJAiKR2y&_nc_ohc=o_TRDfXy9zQQ7kNvgErDfiQ&_nc_zt=23&_nc_ht=scontent.fhan4-1.fna&_nc_gid=A75IhlCVD3U9OaZ7yUWTEz0&oh=00_AYDtbh3HHr-Fr5-43DhOeE2_s4Y_nTzCKGzKGfahSsWAHw&oe=67355325"
        )) }
    }

    private fun getCategories() {
        _state.update { it.copy(categories = listOf(
            "All",
            "123",
            "abc",
            "456",
            "xyz",
            "789",
            "jqk"
        )) }
    }

    private fun getAllProduct() {
        _state.update { it.copy(allProduct = listOf(
            Product(
                name = "abc",
                pid = "001",
                images = listOf(
                    "https://scontent.fhan3-3.fna.fbcdn.net/v/t39.30808-6/466517161_1013887930753342_307490243736270829_n.jpg?stp=dst-jpg_s640x640&_nc_cat=106&ccb=1-7&_nc_sid=127cfc&_nc_eui2=AeFX2YndxzbszK6sZ4aJ4jaqTQdPGyNYGsxNB08bI1gazHQ4AJc75wjsBiwvdsfA9UC4cvHwZvP2HqW95lMBXpHu&_nc_ohc=VQRW74epmDYQ7kNvgFe2pyQ&_nc_zt=23&_nc_ht=scontent.fhan3-3.fna&_nc_gid=A5alKtSb2fvfqGQk-PQw5Sv&oh=00_AYDh-tQiV2pEiiAobRKPow2G8LcLPwY2ZYf93cUY35KELw&oe=6736BFB6",
                    "https://scontent.fhan4-4.fna.fbcdn.net/v/t39.30808-6/466006311_1013079717500830_3643474210991710417_n.jpg?stp=dst-jpg_s600x600&_nc_cat=110&ccb=1-7&_nc_sid=127cfc&_nc_eui2=AeEvuz_vbvImAb89ToKDTEkPkJrNFA5VwLqQms0UDlXAugAVkpJfZH-rb1UtYBTP-QXwmLsUy6gg49m_jEYJA4w5&_nc_ohc=saY5qd2wiiMQ7kNvgE9r57J&_nc_zt=23&_nc_ht=scontent.fhan4-4.fna&_nc_gid=AC-6XMxj-Dk4CiFfwVwl9_J&oh=00_AYD_orR_baDcYx1JpkJalFiUv-7ObTZnAs-lKDy6lMTXHA&oe=6735865B"),
                price = 123456,
                sold = 123456,
                category = listOf("abc", "xyz", "jqk")
            ),
            Product(
                name = "asd assf as  sadf dv s d a sfd asf a sf asf a df ad f adf as f as  asd",
                pid = "002",
                images = listOf("https://scontent.fhan3-2.fna.fbcdn.net/v/t39.30808-6/465928065_512117191812145_1784373943420224638_n.jpg?stp=dst-jpg_p526x296&_nc_cat=107&ccb=1-7&_nc_sid=833d8c&_nc_eui2=AeH6ymXMBAgiMcxIHJxpOLICsWLf_YA87UKxYt_9gDztQgNiBSJiygKaB74VP_f2zbU00AKt9IDrP03rrieRY0uS&_nc_ohc=wg5pzEupiZYQ7kNvgEd6Luz&_nc_zt=23&_nc_ht=scontent.fhan3-2.fna&_nc_gid=AwgSUo8UKEcIQ0bmvi6pm6Z&oh=00_AYD88YV4LrHghM3S8Zi0BIF4BuTEupaBEQ-sey8d4az8GQ&oe=673582A1"),
                price = 123456789,
                sold = 123456202,
                category = listOf("abc", "123", "789")
            ),
            Product(
                name = "123",
                pid = "003",
                images = listOf("https://scontent.fhan3-3.fna.fbcdn.net/v/t39.30808-6/465892016_1258404342089350_9137298027837438844_n.jpg?stp=dst-jpg_p526x296&_nc_cat=1&ccb=1-7&_nc_sid=127cfc&_nc_eui2=AeECESUL_OAseASX2Uar4lN2yRbeqybUSejJFt6rJtRJ6EcyOPqv729lxYK2TUEhgY1YucYhdqLTp1pxGIarsYOX&_nc_ohc=K5NHI1ipnhYQ7kNvgFeTQry&_nc_zt=23&_nc_ht=scontent.fhan3-3.fna&_nc_gid=AS9f_w-ZdWff3QfMzEepXVN&oh=00_AYD-2qGMfS6XQUmIZjE9TQ51It3DwZSjS3cUgrO77hSYgw&oe=67357203"),
                price = 12,
                sold = 456,
            ),
            Product(
                name = "asd assf as  sadf dv s d a sfd asf a sf asf a df ad f adf as f as  asd",
                pid = "004",
                images = listOf("https://scontent.fhan3-2.fna.fbcdn.net/v/t39.30808-6/465928065_512117191812145_1784373943420224638_n.jpg?stp=dst-jpg_p526x296&_nc_cat=107&ccb=1-7&_nc_sid=833d8c&_nc_eui2=AeH6ymXMBAgiMcxIHJxpOLICsWLf_YA87UKxYt_9gDztQgNiBSJiygKaB74VP_f2zbU00AKt9IDrP03rrieRY0uS&_nc_ohc=wg5pzEupiZYQ7kNvgEd6Luz&_nc_zt=23&_nc_ht=scontent.fhan3-2.fna&_nc_gid=AwgSUo8UKEcIQ0bmvi6pm6Z&oh=00_AYD88YV4LrHghM3S8Zi0BIF4BuTEupaBEQ-sey8d4az8GQ&oe=673582A1"),
                price = 123456789,
                sold = 123456202,
                category = listOf("abc", "123", "789")
            ),Product(
                name = "abc",
                pid = "005",
                images = listOf(
                    "https://scontent.fhan4-4.fna.fbcdn.net/v/t39.30808-6/466006311_1013079717500830_3643474210991710417_n.jpg?stp=dst-jpg_s600x600&_nc_cat=110&ccb=1-7&_nc_sid=127cfc&_nc_eui2=AeEvuz_vbvImAb89ToKDTEkPkJrNFA5VwLqQms0UDlXAugAVkpJfZH-rb1UtYBTP-QXwmLsUy6gg49m_jEYJA4w5&_nc_ohc=saY5qd2wiiMQ7kNvgE9r57J&_nc_zt=23&_nc_ht=scontent.fhan4-4.fna&_nc_gid=AC-6XMxj-Dk4CiFfwVwl9_J&oh=00_AYD_orR_baDcYx1JpkJalFiUv-7ObTZnAs-lKDy6lMTXHA&oe=6735865B"),
                price = 123456,
                sold = 123456,
                category = listOf("abc", "xyz", "jqk")
            )
        )) }
    }

    private fun getSearchResult() {
        state.map { it.searchText }
            .distinctUntilChanged()
            .debounce(300L)
            .filter { it.isNotEmpty() }
            .onEach { searchText ->
                _state.update {
                    it.copy(
                        searchResult = it.allProduct.filter { product ->
                            product.toString().contains(searchText, ignoreCase = true)
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun getNumberOfCart() {
        _state.update { it.copy(numberOfCart = 5) }
    }

    fun onSearchTextChange(newValue: String) {
        _state.update { it.copy(searchText = newValue) }
    }

    fun onClickCart() {
        onEvent(HomeEvent.NavigateToCart)
    }

    fun onClickChats() {
        onEvent(HomeEvent.NavigateToChats)
    }

    fun setCurrentCategory(value: String) {
        _state.update {
            it.copy(
                currentCategory = value,
                showingProduct = state.value.allProduct.filter { product ->
                    value == "All" || product.category.contains(value)
                }
            )
        }
    }
}

data class HomeState(
    var eventImages: List<String> = emptyList(),
    var categories: List<String> = listOf("All"),
    var currentCategory: String = "All",
    var allProduct: List<Product> = emptyList(),
    var showingProduct: List<Product> = emptyList(),
    var searchText: String = "",
    var searchResult: List<Product> = emptyList(),
    var numberOfCart: Int = 0,
    var numberOfChats: Int = 0,

    )

sealed class HomeEvent {
    data object NavigateToLogIn : HomeEvent()
    data object NavigateToCart : HomeEvent()
    data object NavigateToChats : HomeEvent()
    data class NavigateToProduct(val product: Product) : HomeEvent()
}