package com.loftydevelopment.helloworldquiz


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

import java.io.IOException

class PanZoomView(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var bitmap: Bitmap? = null
    private var imageWidth: Int = 0
    private var imageHeight: Int = 0

    private var posX: Float = 0.toFloat()
    private var posY: Float = 0.toFloat()
    private var lastPosX: Float = 0.toFloat()
    private var lastPosY: Float = 0.toFloat()
    private var activePointerID = INVALID_POINTER_ID

    private val scaleDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f

    init {
        scaleDetector = ScaleGestureDetector(context, ScaleListener())
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleDetector.onTouchEvent(event)
        val action = event.action

        when (action and MotionEvent.ACTION_MASK) {

            MotionEvent.ACTION_DOWN -> {
                val x = event.x
                val y = event.y
                lastPosX = x
                lastPosY = y
                activePointerID = event.getPointerId(0)
            }

            MotionEvent.ACTION_MOVE -> {
                val pointerIndex = event.findPointerIndex(activePointerID)
                val x = event.getX(pointerIndex)
                val y = event.getY(pointerIndex)

                if (!scaleDetector.isInProgress) {
                    val distanceX = x - lastPosX
                    val distanceY = y - lastPosY
                    posX += distanceX
                    posY += distanceY
                    invalidate()
                }

                lastPosX = x
                lastPosY = y
            }

            MotionEvent.ACTION_UP -> {
                activePointerID = INVALID_POINTER_ID
            }

            MotionEvent.ACTION_CANCEL -> {
                activePointerID = INVALID_POINTER_ID

            }

            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = event.getPointerId(pointerIndex)
                if (pointerId == activePointerID) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    lastPosX = event.getX(newPointerIndex)
                    lastPosY = event.getY(newPointerIndex)
                    activePointerID = event.getPointerId(newPointerIndex)
                }
            }
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (bitmap != null) {
            canvas.save()

            if (posX * -1 < 0) {
                posX = 0f
            } else if (posX * -1 > imageWidth * scaleFactor - width) {
                posX = (imageWidth * scaleFactor - width) * -1
            }
            if (posY * -1 < 0) {
                posY = 0f
            } else if (posY * -1 > imageHeight * scaleFactor - height) {
                posY = (imageHeight * scaleFactor - height) * -1
            }

            if (imageHeight * scaleFactor < height) {
                posY = 0f
            }

            canvas.translate(posX, posY)
            canvas.scale(scaleFactor, scaleFactor)
            canvas.drawBitmap(bitmap!!, 0f, 0f, null)
            canvas.restore()
        }
    }

    fun loadImageOnCanvas(selectedImage: Uri) {
        resetScale()
        var bitmap: Bitmap? = null

        try {
            bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, selectedImage)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val aspectRatio = bitmap!!.height.toFloat() / bitmap.width.toFloat()
        val displayMetrics = resources.displayMetrics
        imageWidth = displayMetrics.widthPixels
        imageHeight = Math.round(imageWidth * aspectRatio)

        this.bitmap = resizeBitmap(bitmap, imageWidth, imageHeight)
        invalidate()
    }

    private fun resetScale() {
         posX = 0.toFloat()
         posY = 0.toFloat()
         lastPosX = 0.toFloat()
         lastPosY = 0.toFloat()
         scaleFactor = 1.0f

    }

    private fun resizeBitmap(bitmap:Bitmap, width:Int, height:Int):Bitmap{

        return Bitmap.createScaledBitmap(
            bitmap,
            width,
            height,
            false
        )
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(scaleGestureDetector: ScaleGestureDetector): Boolean {
            scaleFactor *= scaleGestureDetector.scaleFactor
            scaleFactor = Math.max(minScale, Math.min(scaleFactor, maxScale))
            invalidate()

            return true
        }
    }
    companion object {
        private var INVALID_POINTER_ID = -1
        private var minScale = 1.0f
        private var maxScale = 5.0f
    }
}