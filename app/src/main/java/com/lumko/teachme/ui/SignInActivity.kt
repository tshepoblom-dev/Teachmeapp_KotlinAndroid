package com.lumko.teachme.ui

import android.os.Bundle
import com.lumko.teachme.databinding.ActivitySignInBinding
import com.lumko.teachme.manager.App
import com.lumko.teachme.ui.frag.ChooseAuthFrag

class SignInActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = Bundle()
        intent?.getBooleanExtra(App.REQUEDT_TO_LOGIN_FROM_INSIDE_APP, false)?.let {
            bundle.putBoolean(
                App.REQUEDT_TO_LOGIN_FROM_INSIDE_APP,
                it
            )
        }

        val frag = ChooseAuthFrag()
        frag.arguments = bundle

        supportFragmentManager.beginTransaction().replace(android.R.id.content, frag).commit()
    }
}