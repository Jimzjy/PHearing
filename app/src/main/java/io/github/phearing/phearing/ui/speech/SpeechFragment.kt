package io.github.phearing.phearing.ui.speech

import android.animation.Animator
import android.content.Intent
import android.content.res.ColorStateList
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ScrollView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager

import io.github.phearing.phearing.R
import io.github.phearing.phearing.common.SpeechChoiceRVAdapter
import io.github.phearing.phearing.databinding.FragmentSpeechBinding
import kotlinx.android.synthetic.main.hint_speech0.view.*
import kotlinx.android.synthetic.main.hint_speech1.view.*
import kotlinx.android.synthetic.main.hint_speech2.view.*

const val SPEECH_PREPARE = 0
const val SPEECH_START = 1
const val SPEECH_FINISH = 2
const val SPEECH_WAIT = 3
const val SPEECH_NOT_SUPPORT = 4

class SpeechFragment : Fragment() {
    companion object {
        fun newInstance() = SpeechFragment()
    }

    private lateinit var mViewModel: SpeechViewModel
    private lateinit var mBinding: FragmentSpeechBinding
    private lateinit var mChoiceRVAdapter: SpeechChoiceRVAdapter
    private val mChoiceList = mutableListOf<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        mBinding = FragmentSpeechBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mViewModel = ViewModelProviders.of(this).get(SpeechViewModel::class.java)
        mBinding.viewModel = mViewModel
        mBinding.setLifecycleOwner(this@SpeechFragment)

        context?.let {
            mChoiceRVAdapter = SpeechChoiceRVAdapter(it, mChoiceList)
            mChoiceRVAdapter.setOnClickCallback {
                mViewModel.checkAnswer(it)
                mChoiceList.clear()
                mChoiceRVAdapter.notifyDataSetChanged()
                mBinding.speechHypTv.alpha = 1f
            }
            mBinding.speechChoiceRv.layoutManager = GridLayoutManager(it, 2)
        }
        mBinding.speechChoiceRv.adapter = mChoiceRVAdapter
        mBinding.speechChoiceRv.setHasFixedSize(true)

        mBinding.speechFalseIv.animate().setListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator?) {}
            override fun onAnimationEnd(p0: Animator?) {
                mBinding.speechFalseIv.visibility = View.INVISIBLE
            }
            override fun onAnimationCancel(p0: Animator?) {}
            override fun onAnimationStart(p0: Animator?) {
                mBinding.speechFalseIv.visibility = View.VISIBLE
            }
        })

        init()
    }

    override fun onStop() {
        super.onStop()
        mViewModel.trueHintShow.value = 0
        mViewModel.falseHintShow.value = 0
    }

    override fun onDestroy() {
        super.onDestroy()
        mViewModel.destroy()
    }

    private fun init() {
        mBinding.speechHintBt.setOnClickListener {
            mViewModel.isHintOpen.value = true
        }
        mBinding.speechNextBt.setOnClickListener {
            mViewModel.next()
            mViewModel.isNextButtonEnable.value = false
        }
        mBinding.speechFalseIv.setOnClickListener {
            if (mViewModel.falseHintEnable) {
                mViewModel.setChoiceList()
                mBinding.speechHypTv.animate().setDuration(500).alpha(0f)
            }
        }
        mBinding.speechHintCard.setCloseCallBack {
            mViewModel.isHintOpen.value = false
        }

        mViewModel.trueHintShow.observe(this, Observer {
            if (it > 0) {
                Log.e("","ATrue")
                mBinding.speechTrueIv.alpha = 1f
                mBinding.speechTrueIv.animate().setDuration(1000).alpha(0f)
            }
        })
        mViewModel.falseHintShow.observe(this, Observer {
            if (it > 0) {
                Log.e("", "AFalse")
                mBinding.speechFalseIv.alpha = 1f
                mBinding.speechFalseIv.animate().setDuration(1500).alpha(0f)
            }
        })
        mViewModel.isHintOpen.observe(this, Observer {
            if (it) {
                mBinding.speechHintCard.visibility = View.VISIBLE
                mBinding.speechDisplayCard.visibility = View.INVISIBLE
            } else {
                mBinding.speechHintCard.visibility = View.INVISIBLE
                mBinding.speechDisplayCard.visibility = View.VISIBLE
            }
        })
        mViewModel.state.observe(this, Observer {
            mBinding.speechHintCard.setContainerList(getHintCardContent(it), true)
        })
        mViewModel.choiceList.observe(this, Observer {
            mChoiceList.clear()
            mChoiceList.addAll(it)
            mChoiceRVAdapter.notifyDataSetChanged()
        })
        mViewModel.isNextButtonEnable.observe(this, Observer {
            mBinding.speechNextBt.isEnabled = it
        })
    }

    private fun getHintCardContent(state: Int): List<View> {
        val list = mutableListOf<View>()
        return when(state) {
            SPEECH_PREPARE -> {
                val view0 = getView<ConstraintLayout>(R.layout.hint_speech0)
                view0.hint_speech0_hello_bt.setOnClickListener {
                    mViewModel.speak("你好")
                }
                view0.hint_speech0_goto_tts.setOnClickListener {
                    startActivity(Intent("com.android.settings.TTS_SETTINGS"))
                }
                view0.hint_speech0_start_bt.setOnClickListener {
                    mViewModel.startTest()
                }

                val view1 = getView<ConstraintLayout>(R.layout.hint_speech4)
                val view2 = getView<ScrollView>(R.layout.hint_speech5)

                list.add(view0)
                list.add(view1)
                list.add(view2)
                list
            }
            SPEECH_START -> {
                list
            }
            SPEECH_FINISH -> {
                val view0 = getView<ConstraintLayout>(R.layout.hint_speech2)
                view0.hint_speech2_score_tv.text = mViewModel.scoreText.value
                view0.hint_speech2_start_bt.setOnClickListener {
                    mViewModel.init()
                }

                list.add(view0)
                list
            }
            SPEECH_WAIT -> {
                val view0 = getView<ConstraintLayout>(R.layout.hint_speech3)

                list.add(view0)
                list
            }
            SPEECH_NOT_SUPPORT -> {
                val view0 = getView<ConstraintLayout>(R.layout.hint_speech1)
                view0.hint_speech1_goto_tts.setOnClickListener {
                    startActivity(Intent("com.android.settings.TTS_SETTINGS"))
                }

                list.add(view0)
                list
            }
            else -> {
                list
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T: View> getView(resId: Int): T {
        return layoutInflater.inflate(resId, null, false) as T
    }
}
