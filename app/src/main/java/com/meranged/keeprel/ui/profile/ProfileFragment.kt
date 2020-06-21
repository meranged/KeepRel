package com.meranged.keeprel.ui.profile

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.meranged.keeprel.R
import com.meranged.keeprel.ui.slideshow.SlideshowViewModel
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel

    val RC_SIGN_IN = 100
    lateinit var uIV: ImageView
    lateinit var unTV: TextView
    lateinit var ueTV: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileViewModel =
            ViewModelProviders.of(this).get(ProfileViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_profile, container, false)

        uIV = root.findViewById(R.id.profileImageView)
        unTV = root.findViewById(R.id.profileNameTextView)
        ueTV = root.findViewById(R.id.profileMailTextView)

        val changeButton = root.findViewById<Button>(R.id.profileChangeUserButton)

        changeButton.setOnClickListener {
            reconnect(true)
        }

        reconnect(false)

        return root
    }

    private fun reconnect(isChange: Boolean){

        var account: GoogleSignInAccount? = null

        if (!isChange) {

            account = GoogleSignIn.getLastSignedInAccount(activity)
        }

        if (account == null) {


            // Configure sign-in to request the user's ID, email address, and basic
            // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()

            // Build a GoogleSignInClient with the options specified by gso.
            val mGoogleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

            mGoogleSignInClient?.let {
                it.signOut()
                    ?.addOnCompleteListener(requireActivity(), OnCompleteListener<Void?> {
                        // ...
                    })
            }

            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)

        } else {
            updateUI(account)
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

    fun updateUI(acc: GoogleSignInAccount?){

        if (acc != null) {
            unTV.text = acc.displayName
            ueTV.text = acc.email
            var a = acc.photoUrl
            uIV.setImageURI(null)
            Picasso.get().load(a).into(uIV)
        } else {
            unTV.text = "Something went"
            ueTV.text = "wrong"
        }
    }

}