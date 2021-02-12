package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.core.animation.doOnEnd
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

private const val BAR_START = 0F
private const val DURATION_CLICKED = 4000L
private const val DURATION_LOADING = 3000L
private const val DURATION_FAST = 500L
private const val ARC_START = 0F
private const val TEXT_SIZE = 60F

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0

    private var buttonBackground = 0
    private lateinit var buttonText: String
    private var textColor = 0
    private var barWidth = BAR_START
    private var barBackground = 0
    private var arcAngle = ARC_START
    private var arcBackground = 0

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = TEXT_SIZE
    }

    private var valueAnimator = ValueAnimator()

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { _, _, new ->
        when(new) {
            ButtonState.Clicked -> {
                buttonText = context.getString(R.string.button_loading)
                valueAnimator.duration = DURATION_CLICKED
                valueAnimator.repeatCount = 0
                valueAnimator.interpolator = DecelerateInterpolator()
                valueAnimator.start()
            }
            ButtonState.Loading -> {
                buttonText = context.getString(R.string.button_loading)
                valueAnimator.duration = DURATION_LOADING
                valueAnimator.repeatCount = ValueAnimator.INFINITE
                valueAnimator.interpolator = LinearInterpolator()
                valueAnimator.start()
            }
            ButtonState.Completed -> {
                valueAnimator.pause()
                valueAnimator.repeatCount = 0
                valueAnimator.duration = DURATION_FAST
                valueAnimator.start()
            }
        }
        invalidate()
    }

    init {
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonText = getText(R.styleable.LoadingButton_button_text) as String
            textColor = getColor(R.styleable.LoadingButton_text_color, 0)
            buttonBackground = getColor(R.styleable.LoadingButton_button_background,0)
            barBackground = getColor(R.styleable.LoadingButton_bar_background, 0)
            arcBackground = getColor(R.styleable.LoadingButton_arc_background, 0)
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        valueAnimator = ValueAnimator.ofFloat(BAR_START, widthSize.toFloat()).apply {
            duration = DURATION_LOADING
            repeatCount = ValueAnimator.INFINITE
            repeatMode = ValueAnimator.RESTART
            interpolator = LinearInterpolator()
            addUpdateListener {
                isEnabled = false
                barWidth = animatedValue as Float
                arcAngle = animatedFraction * 360
                invalidate()
            }
            doOnEnd {
                buttonText = context.getString(R.string.download)
                barWidth = BAR_START
                arcAngle = ARC_START
                isEnabled = true
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        paint.color = buttonBackground
        canvas.drawRect(0F, 0F, widthSize.toFloat(), heightSize.toFloat(), paint)
        paint.color = barBackground
        canvas.drawRect(0F, 0F, barWidth, heightSize.toFloat(), paint)
        paint.color = arcBackground
        canvas.drawArc((widthSize*3/4).toFloat(), heightSize/4.toFloat(),
            (widthSize*3/4 + heightSize/2).toFloat(), heightSize*3/4.toFloat(),
            ARC_START, arcAngle, true, paint)
        paint.color = textColor
        canvas.drawText(buttonText, (widthSize/2).toFloat(), (heightSize/2 + 20).toFloat() , paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }
}