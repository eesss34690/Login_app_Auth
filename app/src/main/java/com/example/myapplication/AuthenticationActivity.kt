package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.amazonaws.mobile.client.*
import com.amplifyframework.AmplifyException
import com.amplifyframework.core.Amplify
import com.amplifyframework.datastore.AWSDataStorePlugin
import com.amplifyframework.datastore.generated.model.Todo


class AuthenticationActivity : AppCompatActivity() {
    private val TAG = AuthenticationActivity::class.java.simpleName
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authentication)
        AWSMobileClient.getInstance()
            .initialize(applicationContext, object : Callback<UserStateDetails> {
                override fun onResult(userStateDetails: UserStateDetails) {
                    Log.i(TAG, userStateDetails.userState.toString())
                    when (userStateDetails.userState) {
                        UserState.SIGNED_IN -> {
                            val i = Intent(this@AuthenticationActivity, MainActivity::class.java)
                            AWSMobileClient.getInstance().signOut()
                            showSignIn()

                        //    startActivity(i)
                        }
                        UserState.SIGNED_OUT -> showSignIn()
                        else -> {
                            AWSMobileClient.getInstance().signOut()
                            showSignIn()
                        }
                    }
                }

                override fun onError(e: Exception) {
                    Log.e(TAG, e.toString())
                }
            })
        try {
            Amplify.addPlugin(AWSDataStorePlugin())
            Amplify.configure(applicationContext)
            Log.i("Tutorial", "Initialized Amplify")
        } catch (failure: AmplifyException) {
            Log.e("Tutorial", "Could not initialize Amplify", failure)
        }
        val item = Todo.builder()
            .name("Finish quarterly taxes")
            .description("Taxes are due for the quarter next week")
            .build()
        Amplify.DataStore.save(
            item,
            { success -> Log.i("Tutorial", "Saved item: " + success.item().name) },
            { error -> Log.e("Tutorial", "Could not save item to DataStore", error) }
        )

    }

    private fun showSignIn() {
        try {
            AWSMobileClient.getInstance().showSignIn(
                this,
                SignInUIOptions.builder().nextActivity(MainActivity::class.java).build()
            )
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }
}