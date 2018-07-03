package com.llollox.androidtoggleswitch.widgets

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.llollox.androidtoggleswitch.R


class ToggleSwitchButton (context: Context, var position: Int, var positionType: PositionType,
                          listener: Listener, layoutId: Int, var prepareDecorator: ToggleSwitchButtonDecorator,
                          var checkedDecorator: ViewDecorator?, var uncheckedDecorator: ViewDecorator?,
                          var checkedBackgroundColor: Int, var checkedBorderColor: Int,
                          var borderRadius: Float, var borderWidth: Float,
                          var uncheckedBackgroundColor: Int, var uncheckedBorderColor: Int,
                          var separatorColor: Int, var toggleMargin: Int) :
        LinearLayout(context), IRightToLeftProvider {

    interface ToggleSwitchButtonDecorator {
        fun decorate(toggleSwitchButton: ToggleSwitchButton, view: View, position: Int)
    }

    interface ViewDecorator {
        fun decorate(view: View, position: Int)
    }

    interface Listener {
        fun onToggleSwitchClicked(button: ToggleSwitchButton)
    }

    enum class PositionType {
        LEFT, CENTER, RIGHT
    }

    private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)

    var toggleWidth                                 = 0
    var toggleHeight                                = LinearLayout.LayoutParams.MATCH_PARENT
    var isChecked                                   = false
    var rightToLeftProvider: IRightToLeftProvider = this

    var separator: View
    var view: View

    init {

        // Inflate Layout
        val inflater    = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layoutView  = inflater.inflate(R.layout.toggle_switch_button, this, true) as LinearLayout
        val relativeLayoutContainer = layoutView.findViewById(R.id.relative_layout_container) as RelativeLayout

        setAddStatesFromChildren(true)
        
        // View
        val params = RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE)
        params.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE)

        view  = LayoutInflater.from(context).inflate(layoutId, relativeLayoutContainer, false)
        relativeLayoutContainer.addView(view, params)

        // Bind Views
        separator = layoutView.findViewById(R.id.separator)
        val clickableWrapper = layoutView.findViewById(R.id.clickable_wrapper) as LinearLayout

        // Setup View
        val layoutParams = LinearLayout.LayoutParams(toggleWidth, toggleHeight, 1.0f)

        if (toggleMargin > 0 && !isFirst()) {
            layoutParams.setMargins(toggleMargin, 0, 0, 0)
        }

        this.layoutParams = layoutParams

        this.orientation = HORIZONTAL
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.background = getBackgroundDrawable()
        } else {
            setBackgroundDrawable(getBackgroundDrawable())
        }

        separator.setBackgroundColor(separatorColor)

        clickableWrapper.setOnClickListener {
            listener.onToggleSwitchClicked(this)
        }

        // Decorate
        prepareDecorator.decorate(this, view, position)
        uncheckedDecorator?.decorate(view, position)
    }

    // Public instance methods

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    fun check() {
        this.isChecked = true
        checkedDecorator?.decorate(view, position)
        refreshDrawableState()
    }

    fun uncheck() {
        this.isChecked = false
        uncheckedDecorator?.decorate(view, position)
        refreshDrawableState()
    }

    fun setSeparatorVisibility(isSeparatorVisible : Boolean) {
        separator.visibility = if (isSeparatorVisible) View.VISIBLE else View.GONE
    }

    // Private instance methods

    private fun getBackgroundDrawable() : StateListDrawable {
        val checkedBackgroundColorFaded = Color.argb(127, Color.red(checkedBackgroundColor), Color.green(checkedBackgroundColor), Color.blue(checkedBackgroundColor))
        val cornerRadii = getCornerRadii()

        var stateListDrawable = StateListDrawable()

        val uncheckedDrawable = GradientDrawable()
        uncheckedDrawable.setColor(uncheckedBackgroundColor)

        uncheckedDrawable.cornerRadii = cornerRadii
        
        ///

        val checkedDrawable = GradientDrawable()
        checkedDrawable.setColor(checkedBackgroundColor)

        checkedDrawable.cornerRadii = cornerRadii

        ///

        val focusedUncheckedDrawable = GradientDrawable()
        focusedUncheckedDrawable.setColor(checkedBackgroundColorFaded)

        focusedUncheckedDrawable.cornerRadii = cornerRadii

        ///

        val focusedCheckedDrawable = GradientDrawable()
        focusedCheckedDrawable.setColor(checkedBackgroundColor)

        focusedCheckedDrawable.cornerRadii = cornerRadii
        
        ///
        
        if (borderWidth > 0) {
            uncheckedDrawable.setStroke(borderWidth.toInt(), uncheckedBorderColor)
            checkedDrawable.setStroke(borderWidth.toInt(), checkedBorderColor)
            focusedUncheckedDrawable.setStroke(borderWidth.toInt(), checkedBorderColor)
            focusedCheckedDrawable.setStroke(borderWidth.toInt(), checkedBackgroundColorFaded)
        }

        stateListDrawable.addState(intArrayOf(android.R.attr.state_focused, -android.R.attr.state_checked), focusedUncheckedDrawable)
        stateListDrawable.addState(intArrayOf(android.R.attr.state_focused, android.R.attr.state_checked), focusedCheckedDrawable)
        stateListDrawable.addState(intArrayOf(android.R.attr.state_checked), checkedDrawable)
        stateListDrawable.addState(intArrayOf(), uncheckedDrawable)

        return stateListDrawable
    }

    private fun getCornerRadii(): FloatArray {
        if (toggleMargin > 0) {
            return floatArrayOf(borderRadius,borderRadius,
                    borderRadius, borderRadius,
                    borderRadius, borderRadius,
                    borderRadius,borderRadius)
        }
        else {
            val isRightToLeft = rightToLeftProvider.isRightToLeft()
            when(positionType) {
                PositionType.LEFT ->
                    return if (isRightToLeft) getRightCornerRadii() else getLeftCornerRadii()

                PositionType.RIGHT ->
                    return if (isRightToLeft) getLeftCornerRadii() else getRightCornerRadii()

                else -> return floatArrayOf(0f,0f,0f, 0f, 0f, 0f, 0f,0f)
            }
        }
    }

    override fun isRightToLeft(): Boolean {
        return resources.getBoolean(R.bool.is_right_to_left)
    }

    private fun getRightCornerRadii(): FloatArray {
        return floatArrayOf(0f,0f,borderRadius, borderRadius, borderRadius, borderRadius, 0f,0f)
    }

    private fun getLeftCornerRadii(): FloatArray {
        return floatArrayOf(borderRadius, borderRadius, 0f,0f,0f,0f, borderRadius, borderRadius)
    }

    private fun isFirst() : Boolean {
        if (rightToLeftProvider.isRightToLeft()) {
            return positionType == PositionType.RIGHT
        }
        else {
            return positionType == PositionType.LEFT
        }
    }
}