package com.example.weknot_android.viewmodel

import android.R
import android.app.Application
import com.example.weknot_android.base.viewmodel.BaseViewModel
import com.example.weknot_android.model.entity.OpenChat.ChatRoom
import com.example.weknot_android.model.entity.user.FbUser
import com.example.weknot_android.model.entity.user.User
import com.example.weknot_android.widget.SingleLiveEvent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.observers.DisposableSingleObserver


class CreateRoomViewModel(application: Application) : BaseViewModel<Any>(application) {

    private val fbUser = FbUser()
    val chatRoom = ChatRoom()
    private var chatRoomUid: String? = null

    var selectedPosition: Int = 0

    val createEvent = SingleLiveEvent<Unit>()
    val closeEvent = SingleLiveEvent<Unit>()

    fun onClickCreate() {
        createEvent.call()
    }

    fun onClickBack() {
        closeEvent.call()
    }

    fun getUser() {
        addDisposable(repository.getUser(userId), object : DisposableSingleObserver<User>() {
            override fun onSuccess(user: User) {
                onRetrieveUserSuccess(user)
            }

            override fun onError(e: Throwable) {
                onErrorEvent.value = e
            }
        })
    }

    private fun setChatRoom(user: User) {
        chatRoom.masterName = user.name
        setRoomCount()
    }

    private fun setFbUser(user: User) {
        fbUser.name = user.name
        fbUser.uid = FirebaseAuth.getInstance().currentUser!!.uid
    }

    private fun insertFirebase() {
        chatRoomUid = FirebaseDatabase.getInstance().reference.child("groupchatrooms").push().key!!

        FirebaseDatabase.getInstance().reference.child("groupchatrooms").child(chatRoomUid!!).setValue(chatRoom)
        FirebaseDatabase.getInstance().reference.child("groupchatrooms").child(chatRoomUid!!).child("users").push().setValue(fbUser)

        closeEvent.call()
    }

    private fun setRoomCount() {
        var count = 1
        FirebaseDatabase.getInstance().reference.child("groupchatrooms")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        for (item in dataSnapshot.children) {
                            count++
                        }
                        chatRoom.roomNumber = count

                        insertFirebase()
                    }
                    override fun onCancelled(databaseError: DatabaseError) {}
                })
    }

    private fun onRetrieveUserSuccess(user: User) {
        setFbUser(user)
        setChatRoom(user)
    }

    override fun onRetrieveDataSuccess(data: Any) {}

    override fun onRetrieveBaseSuccess(message: String) {}
}