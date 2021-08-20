package com.citrus.sandwitchdemo.util

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.citrus.sandwitchdemo.R

class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val cusView: View = View.inflate(context, R.layout.custom_cons, this)
    private val textStr: TextView = cusView.findViewById(R.id.numKey)

    init {
        if (attrs != null) {
            val attributes = context.theme.obtainStyledAttributes(
                attrs,
                R.styleable.CustomView,
                0, 0
            )

            textStr.text = attributes.getString(R.styleable.CustomView_numStr) ?: ""

        }
    }

    open fun setText(text: String) {
        textStr.text = text
    }



}