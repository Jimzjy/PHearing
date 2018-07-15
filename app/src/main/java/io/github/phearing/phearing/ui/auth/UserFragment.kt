package io.github.phearing.phearing.ui.auth

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import io.github.phearing.phearing.R
import io.github.phearing.phearing.databinding.FragmentUserBinding

const val PICK_IMAGE_REQUEST = 1

class UserFragment : Fragment() {
    companion object {
        fun newInstance() = UserFragment()
    }

    private lateinit var mViewModel: UserViewModel
    private lateinit var mBinding: FragmentUserBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        (activity as AuthActivity).supportActionBar?.title = resources.getString(R.string.data)
        mBinding = FragmentUserBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(UserViewModel::class.java)
        mBinding.viewModel = mViewModel
        mBinding.setLifecycleOwner(this)
        init()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                PICK_IMAGE_REQUEST -> {
                    data?.let {
                        mViewModel.updateAvatar(it.data)
                    }
                }
            }
        }
    }

    private fun init() {
        mBinding.authUserDataChangeBt.setOnClickListener {
            (activity as AuthActivity).navigateTo(DataChangeFragment.newInstance())
        }
        mBinding.authUserSignOutBt.setOnClickListener {
            mViewModel.signOut()
            (activity as AuthActivity).navigateTo(LoginFragment.newInstance(), false)
        }
        mBinding.authUserAvatarIv.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }
        context?.let {
            mBinding.authUserSrl.setColorSchemeColors(ContextCompat.getColor(it, R.color.colorPrimary))
            mBinding.authUserSrl.setOnRefreshListener {
                mViewModel.refreshUserDataFromNetwork()
            }
        }

        mViewModel.user.observe(this, Observer {
            it?.let {
                mViewModel.refreshUserData()
                if (it.avatar != null) {
                    mViewModel.userRepo.getAvatar(it.avatar)
                } else {
                    if(mBinding.authUserSrl.isRefreshing) {
                        mBinding.authUserSrl.isRefreshing = false
                    }
                }
            }
        })
        mViewModel.avatar.observe(this, Observer {
            it?.let {
                mBinding.authUserAvatarIv.setImageDrawable(it)

                if(mBinding.authUserSrl.isRefreshing) {
                    mBinding.authUserSrl.isRefreshing = false
                }
            }
        })
    }
}
