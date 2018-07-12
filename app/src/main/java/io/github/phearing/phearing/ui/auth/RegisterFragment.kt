package io.github.phearing.phearing.ui.auth

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import io.github.phearing.phearing.R
import io.github.phearing.phearing.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {

    companion object {
        fun newInstance() = RegisterFragment()
    }

    private lateinit var mViewModel: RegisterViewModel
    private lateinit var mBinding: FragmentRegisterBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AuthActivity).supportActionBar?.title = resources.getString(R.string.auth_register)
        mBinding = FragmentRegisterBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(RegisterViewModel::class.java)
        mBinding.viewModel = mViewModel
        mBinding.setLifecycleOwner(this)
        init()
    }

    private fun init() {
        mBinding.authRegisterRegisterBt.setOnClickListener {
            if (mViewModel.username.value?.isEmpty() != false) {
                mBinding.authRegisterUsernameTil.error = resources.getString(R.string.auth_username_empty)
                return@setOnClickListener
            }
            if (mViewModel.password.value?.isEmpty() != false) {
                mBinding.authRegisterPasswordTil.error = resources.getString(R.string.auth_password_empty)
                return@setOnClickListener
            }
            if (mViewModel.passwordRe.value != mViewModel.password.value) {
                mBinding.authRegisterPasswordReTil.error = resources.getString(R.string.auth_inconsistent_password)
                return@setOnClickListener
            }
            if (mViewModel.birth.value?.isEmpty() != false) {
                mBinding.authRegisterBirthTil.error = resources.getString(R.string.auth_birth_empty)
                return@setOnClickListener
            }
            // do register
        }
        mBinding.authRegisterUsernameEt.setOnKeyListener { _, _, _ ->
            if (mViewModel.username.value?.isEmpty() == false) {
                mBinding.authRegisterUsernameTil.error = null
            } else {
                mBinding.authRegisterUsernameTil.error = resources.getString(R.string.auth_username_empty)
            }
            false
        }
        mBinding.authRegisterPasswordEt.setOnKeyListener { _, _, _ ->
            if (mViewModel.password.value?.isEmpty() == false) {
                mBinding.authRegisterPasswordTil.error = null
            }
            false
        }
        mBinding.authRegisterPasswordReEt.setOnKeyListener { _, _, _ ->
            if (mViewModel.passwordRe.value == mViewModel.password.value) {
                mBinding.authRegisterPasswordReTil.error = null
            }
            false
        }
        mBinding.authRegisterBirthEt.setOnKeyListener { _, _, _ ->
            if (mViewModel.birth.value?.isEmpty() == false) {
                mBinding.authRegisterBirthTil.error = null
            }
            false
        }
    }
}
