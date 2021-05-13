package com.meranged.keeprel

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.picasso.Picasso


class SignInActivity : AppCompatActivity() {

    val RC_SIGN_IN = 100
    var mGoogleSignInClient: GoogleSignInClient? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        val reconnectButton = findViewById<Button>(R.id.reconnectButton)

        logoutButton.setOnClickListener {
            signOut()
        }

        reconnectButton.setOnClickListener {
            reconnect()
        }
        reconnect()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            val task =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }


    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account =
                completedTask.getResult(ApiException::class.java)

            // Signed in successfully, show authenticated UI.
            updateUI(account)
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.i("KRTest", "signInResult:failed code=" + e.statusCode)
            updateUI(null)
        }
    }

    private fun updateUI(acc: GoogleSignInAccount?){
        val accImage = findViewById<ImageView>(R.id.accImageView)
        val accName = findViewById<TextView>(R.id.accNameTV)
        val accMail = findViewById<TextView>(R.id.accMailTV)

        if (acc != null) {
            accName.text = acc.displayName
            accMail.text = acc.email
            val a = acc.photoUrl
            accImage.setImageURI(null)
            Picasso.get().load(a).into(accImage)

        } else {
            accName.text = "Что-то пошло"
            accMail.text = "Не так"
        }
    }



    private fun signOut() {
        mGoogleSignInClient?.signOut()
            ?.addOnCompleteListener(this, OnCompleteListener<Void?> {
                // ...
            })

        updateUI(null)
    }

    private fun reconnect(){
        var account = GoogleSignIn.getLastSignedInAccount(this)

        if (account == null) {

            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()

            // Build a GoogleSignInClient with the options specified by gso.
            var mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        } else {
            updateUI(account)
        }
    }
}