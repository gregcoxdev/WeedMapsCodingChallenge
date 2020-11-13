package com.ngmatt.weedmapsandroidcodechallenge.ui.custom

import android.animation.Animator
import android.content.Context
import android.util.AttributeSet
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable

private const val LOAD_START_FRAME = 0
private const val LOAD_END_FRAME = 118
private const val SUCCESS_START_FRAME = 239
private const val SUCCESS_END_FRAME = 418
private const val FAILURE_START_FRAME = 673
private const val FAILURE_END_FRAME = 841

/**
 * LottieProgressBar is a custom view class that will change it's active frames in order to display
 * a success, failure, or loading loop. It also invoke a custom lambda on completion of the success
 * and failure animations to allow for a smoother UX.
 */
class LottieProgressBar : LottieAnimationView, Animator.AnimatorListener {

    constructor(context: Context): super(context)

    constructor(context: Context, attrs: AttributeSet): super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr : Int) : super(context, attrs, defStyleAttr)

    var onAnimationSuccessEndAction: (() -> Unit) = {}
    var onAnimationFailureEndAction: (() -> Unit) = {}
    private var isSuccessful = false
    private var isFailed = false

    init {
        addAnimatorListener(this)
    }

    override fun onAnimationStart(animation: Animator) {
        // Not implemented.
    }

    override fun onAnimationEnd(animation: Animator) {
        // Notify via lambda.
        when {
            isSuccessful -> { onAnimationSuccessEndAction() }
            isFailed -> { onAnimationFailureEndAction() }
        }
    }

    override fun onAnimationCancel(animation: Animator) {
        // Not implemented.
    }

    override fun onAnimationRepeat(animation: Animator) {
        // When we've passed or failed, let the animation finish, then start the correct animation so it loops seamlessly.
        when {
            isSuccessful -> setSuccessfulFrameData()
            isFailed -> setFailureFrameData()
        }
    }

    fun startLoadingAnimation() {
        isSuccessful = false
        isFailed = false
        setMinFrame(LOAD_START_FRAME)
        setMaxFrame(LOAD_END_FRAME)
        repeatCount = LottieDrawable.INFINITE
        playAnimation()
    }

    fun startSuccessfulAnimation(isImmediate: Boolean) {
        if (isImmediate) setSuccessfulFrameData()
        isSuccessful = true
    }

    fun startFailureAnimation(isImmediate: Boolean) {
        isFailed = when {
            isImmediate -> {
                setFailureFrameData()
                true
            }
            else -> true
        }
    }

    private fun setSuccessfulFrameData() {
        setMaxFrame(SUCCESS_END_FRAME)
        setMinFrame(SUCCESS_START_FRAME)
        repeatCount = 0
    }

    private fun setFailureFrameData() {
        setMaxFrame(FAILURE_END_FRAME)
        setMinFrame(FAILURE_START_FRAME)
        repeatCount = 0
    }

}