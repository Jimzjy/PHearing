package io.github.phearing.phearing.ui.auth

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.widget.LoadingDialog
import io.github.phearing.phearing.databinding.FragmentDataChangeBinding

class DataChangeFragment : Fragment() {
    companion object {
        fun newInstance() = DataChangeFragment()
    }

    private lateinit var mViewModel: DataChangeViewModel
    private lateinit var mBinding: FragmentDataChangeBinding
    private var mIsReady = false
    private var mLoadingDialog: LoadingDialog? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AuthActivity).supportActionBar?.title = resources.getString(R.string.auth_change_user_data)
        mBinding = FragmentDataChangeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(DataChangeViewModel::class.java)
        mBinding.viewModel = mViewModel
        mBinding.setLifecycleOwner(this)
        init()
    }

    private fun init() {
        mBinding.authChangeChangeBt.setOnClickListener {
            if (mViewModel.passwordRe.value != mViewModel.password.value) {
                mBinding.authChangePasswordReTil.error = resources.getString(R.string.auth_inconsistent_password)
                return@setOnClickListener
            }
            mIsReady = true
            mViewModel.changeData()
            mLoadingDialog = LoadingDialog.newInstance()
            mLoadingDialog?.isCancelable = false
            mLoadingDialog?.show(fragmentManager, "dataChange_loadingDialog")
        }
        mBinding.authChangePasswordReEt.setOnKeyListener { _, _, _ ->
            if (mViewModel.passwordRe.value == mViewModel.password.value) {
                mBinding.authChangePasswordReTil.error = null
            }
            false
        }

        mViewModel.user.observe(this, Observer {
            if (mIsReady) {
                mLoadingDialog?.dismiss()
                if (it == null) {
                    mLoadingDialog = null
                } else {
                    (activity as AuthActivity).saveUserData(it)
                    (activity as AuthActivity).onBackPressed()
                }
                mIsReady = false
            }
        })
    }
}
