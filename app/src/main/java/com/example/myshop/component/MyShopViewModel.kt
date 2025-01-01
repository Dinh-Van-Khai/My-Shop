package com.example.myshop.component

import androidx.lifecycle.ViewModel
import com.example.myshop.util.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MyShopViewModel @Inject constructor(
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _numberOfNotifications = MutableStateFlow(0)
    val numberOfNotifications = _numberOfNotifications.asStateFlow()

    private val _route = MutableStateFlow(Screen.Home.route)
    val route = _route.asStateFlow()

    private val notificationsCollectionRef = Firebase.firestore.collection("notifications")

    private var numberOfNotificationListener: ListenerRegistration? = null

    init {
        getNumberOfNotification()
    }

    private fun getNumberOfNotification() {
        auth.addAuthStateListener {
            numberOfNotificationListener?.remove()

            if (it.currentUser == null) {
                _numberOfNotifications.update { 0 }
            }

            it.currentUser?.run {
                numberOfNotificationListener = notificationsCollectionRef
                    .where(
                        Filter.and(
                            Filter.equalTo("to", uid),
                            Filter.equalTo("seen", false)
                        )
                    )
                    .addSnapshotListener { querySnapShot, error ->
                        error?.let { e ->
                            e.printStackTrace()
                            return@addSnapshotListener
                        }
                        querySnapShot?.let { snapShot ->
                            _numberOfNotifications.update { snapShot.size() }
                        }
                    }
            }
        }
    }

    fun setRoute(route: String) {
        _route.update { route }
    }
}
