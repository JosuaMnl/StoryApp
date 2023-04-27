package com.yosha10.storyapp.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Patterns
import androidx.appcompat.widget.AppCompatEditText
import com.google.android.material.textfield.TextInputLayout
import com.yosha10.storyapp.R

class EmailEditText: AppCompatEditText {
    constructor(context: Context) : super(context){
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr){
        init()
    }

    private fun init(){
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.toString().isNullOrEmpty()) {
                    (parent.parent as? TextInputLayout)?.error = null
                }
                else if (!Patterns.EMAIL_ADDRESS.matcher(s.toString()).matches()){
                    (parent.parent as? TextInputLayout)?.error = context.getString(R.string.email_error)
                } else {
                    (parent.parent as? TextInputLayout)?.error = null
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }
}