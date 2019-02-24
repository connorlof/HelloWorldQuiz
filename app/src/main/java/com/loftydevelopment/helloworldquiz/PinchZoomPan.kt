package com.loftydevelopment.helloworldquiz


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast

import java.io.IOException

class PinchZoomPan(context: Context, attrs: AttributeSet?) : View(context, attrs) {

    private var mBitmap: Bitmap? = null
    private var mImageWidth: Int = 0
    private var mImageHeight: Int = 0

    private var mPositionX: Float = 0.toFloat()
    private var mPositionY: Float = 0.toFloat()
    private var mLastTouchX: Float = 0.toFloat()
    private var mLastTouchY: Float = 0.toFloat()
    private var mActivePointerID = INVALID_POINTER_ID

    private val mScaleDetector: ScaleGestureDetector
    private var mScaleFactor = 1.0f

    init {

        mScaleDetector = ScaleGestureDetector(context, ScaleListener())

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        //the scale gesture detector should inspect all the touch events
        mScaleDetector.onTouchEvent(event)

        val action = event.action

        when (action and MotionEvent.ACTION_MASK) {

            MotionEvent.ACTION_DOWN -> {

                //get x and y cords of where we touch the screen
                val x = event.x
                val y = event.y

                //remember where touch event started
                mLastTouchX = x
                mLastTouchY = y

                //save the ID of this pointer
                mActivePointerID = event.getPointerId(0)
            }
            MotionEvent.ACTION_MOVE -> {

                //find the index of the active pointer and fetch its position
                val pointerIndex = event.findPointerIndex(mActivePointerID)
                val x = event.getX(pointerIndex)
                val y = event.getY(pointerIndex)

                if (!mScaleDetector.isInProgress) {

                    //calculate the distance in x and y directions
                    val distanceX = x - mLastTouchX
                    val distanceY = y - mLastTouchY

                    mPositionX += distanceX
                    mPositionY += distanceY

                    //redraw canvas call onDraw method
                    invalidate()

                }
                //remember this touch position for next move event
                mLastTouchX = x
                mLastTouchY = y
            }

            MotionEvent.ACTION_UP -> {
                mActivePointerID = INVALID_POINTER_ID
            }

            MotionEvent.ACTION_CANCEL -> {
                mActivePointerID = INVALID_POINTER_ID

            }

            MotionEvent.ACTION_POINTER_UP -> {
                //Extract the index of the pointer that left the screen
                val pointerIndex =
                    action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = event.getPointerId(pointerIndex)
                if (pointerId == mActivePointerID) {
                    //Our active pointer is going up Choose another active pointer and adjust
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mLastTouchX = event.getX(newPointerIndex)
                    mLastTouchY = event.getY(newPointerIndex)
                    mActivePointerID = event.getPointerId(newPointerIndex)
                }
            }
        }

        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (mBitmap != null) {
            canvas.save()

            if (mPositionX * -1 < 0) {
                mPositionX = 0f
            } else if (mPositionX * -1 > mImageWidth * mScaleFactor - width) {
                mPositionX = (mImageWidth * mScaleFactor - width) * -1
            }
            if (mPositionY * -1 < 0) {
                mPositionY = 0f
            } else if (mPositionY * -1 > mImageHeight * mScaleFactor - height) {
                mPositionY = (mImageHeight * mScaleFactor - height) * -1
            }

            if (mImageHeight * mScaleFactor < height) {
                mPositionY = 0f
            }

            canvas.translate(mPositionX, mPositionY)
            canvas.scale(mScaleFactor, mScaleFactor)
            canvas.drawBitmap(mBitmap!!, 0f, 0f, null)
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
        mImageWidth = displayMetrics.widthPixels
        mImageHeight = Math.round(mImageWidth * aspectRatio)

        mBitmap = resizeBitmap(bitmap, mImageWidth, mImageHeight)
        invalidate()
        //requestLayout();

    }

    private fun resetScale() {
         mPositionX = 0.toFloat()
         mPositionY = 0.toFloat()
         mLastTouchX = 0.toFloat()
         mLastTouchY = 0.toFloat()
         mScaleFactor = 1.0f

    }

    // Method to resize a bitmap programmatically
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

            mScaleFactor *= scaleGestureDetector.scaleFactor
            //don't to let the image get too large or small
            mScaleFactor = Math.max(mMinZoom, Math.min(mScaleFactor, mMaxZoom))

            invalidate()

            return true
        }
    }

    companion object {

        private val INVALID_POINTER_ID = -1
        private val mMinZoom = 1.0f
        private val mMaxZoom = 5.0f
    }
}