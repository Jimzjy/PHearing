package io.github.phearing.phearing.ui.auth

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.github.phearing.phearing.R
import io.github.phearing.phearing.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    companion object {
        fun newInstance() = LoginFragment()
    }

    private lateinit var mViewModel: LoginViewModel
    private lateinit var mBinding: FragmentLoginBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AuthActivity).supportActionBar?.title = resources.getString(R.string.auth_login)
        mBinding = FragmentLoginBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(LoginViewModel::class.java)
        mBinding.viewModel = mViewModel
        mBinding.setLifecycleOwner(this)
        init()
    }

    private fun init() {
        mBinding.authLoginRegisterBt.setOnClickListener {
            (activity as AuthActivity).navigateTo(RegisterFragment.newInstance())
        }
        mBinding.authLoginLoginBt.setOnClickListener {
            if (mViewModel.username.value?.isEmpty() != false) {
                mBinding.authLoginUsernameTil.error = resources.getString(R.string.auth_username_empty)
                return@setOnClickListener
            }
            if (mViewModel.password.value?.isEmpty() != false) {
                mBinding.authLoginPasswordTil.error = resources.getString(R.string.auth_password_empty)
                return@setOnClickListener
            }
            // do login
        }
        mBinding.authLoginUsernameEt.setOnKeyListener { _, _, _ ->
            if (mViewModel.username.value?.isEmpty() == false) {
                mBinding.authLoginUsernameTil.error = null
            }
            false
        }
        mBinding.authLoginPasswordEt.setOnKeyListener { _, _, _ ->
            if (mViewModel.password.value?.isEmpty() == false) {
                mBinding.authLoginPasswordTil.error = null
            }
            false
        }
    }
}
