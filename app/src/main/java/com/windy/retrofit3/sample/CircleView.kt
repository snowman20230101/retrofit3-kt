package com.windy.retrofit3.sample

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View

class CircleView(context: Context) : View(context) {
    private val paint: Paint = Paint()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        canvas?.drawCircle(height.toFloat(), width.toFloat(), 10.0f, paint)
    }
}