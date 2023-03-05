package com.example.ledrgbesp8266firebasekotlin.library

import android.content.Context
import android.graphics.*
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.*

class ColorPicker(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val desiredSizeDp: Float = 320F
    private var dotRadiusDp: Float = 8F

    private val dotRadius: Float
    private val dotPaint: Paint

    private val triangle: Triangle
    private val circle: HueCircle

    var listener: Listener? = null

    private var hue: Float = 0F
    private var colorFraction: Float = 1F
    private var whiteFraction: Float = 0F

    var color: Int
        get() {
            var r = RybColors.red(hue)
            var g = RybColors.green(hue)
            var b = RybColors.blue(hue)

            r = r * colorFraction + whiteFraction
            g = g * colorFraction + whiteFraction
            b = b * colorFraction + whiteFraction

            return RgbCommons.rgb2int(r, g, b)
        }
        set(rgb) {
            val rgbF = RgbCommons.int2rgb(rgb, FloatArray(3))

            this.hue = RybColors.hue(rgbF[0], rgbF[1], rgbF[2])
            this.whiteFraction = min(min(rgbF[0], rgbF[1]), rgbF[2])
            this.colorFraction = max(max(rgbF[0], rgbF[1]), rgbF[2]) - whiteFraction

            triangle.updateTriangle()

            invalidate()
        }

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)

        this.triangle = Triangle()
        this.circle = HueCircle()

        this.dotPaint = Paint()
        this.dotPaint.style = Paint.Style.STROKE

        this.dotRadius = dotRadiusDp * context.resources.displayMetrics.density
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredSize = (desiredSizeDp * context.resources.displayMetrics.density).toInt()

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        var width: Int = -1
        var height: Int = -1

        when(widthMode) {
            MeasureSpec.EXACTLY -> {
                when(heightMode) {
                    MeasureSpec.EXACTLY -> {
                        width = widthSize
                        height = heightSize
                    }
                    MeasureSpec.AT_MOST -> {
                        width = widthSize
                        height = min(widthSize, heightSize)
                    }
                    MeasureSpec.UNSPECIFIED -> {
                        width = widthSize
                        height = widthSize
                    }
                }
            }
            MeasureSpec.AT_MOST -> {
                when(heightMode) {
                    MeasureSpec.EXACTLY -> {
                        width = min(widthSize, heightSize)
                        height = heightSize
                    }
                    MeasureSpec.AT_MOST -> {
                        width = min(min(widthSize, heightSize), desiredSize)
                        height = width
                    }
                    MeasureSpec.UNSPECIFIED -> {
                        width = min(desiredSize, widthSize)
                        height = width
                    }
                }
            }
            MeasureSpec.UNSPECIFIED -> {
                when(heightMode) {
                    MeasureSpec.EXACTLY -> {
                        width = heightSize
                        height = heightSize
                    }
                    MeasureSpec.AT_MOST -> {
                        width = min(desiredSize, heightSize)
                        height = width
                    }
                    MeasureSpec.UNSPECIFIED -> {
                        width = desiredSize
                        height = desiredSize
                    }
                }
            }
        }

        setMeasuredDimension(width, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        this.circle.updateDimensions()
        this.triangle.updateDimensions()
    }

    override fun onDraw(canvas: Canvas) {
        circle.draw(canvas)
        triangle.draw(canvas)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val action = event.actionMasked

        for (i in 0 until event.pointerCount) {
            val id = event.getPointerId(i)
            when (action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> if (!circle.select(
                        event.getX(i),
                        event.getY(i),
                        id
                    )
                ) {
                    triangle.select(event.getX(i), event.getY(i), id)
                }
                MotionEvent.ACTION_MOVE -> {
                    circle.dragTo(event.getX(i), event.getY(i), id)
                    triangle.dragTo(event.getX(i), event.getY(i), id)
                }
                MotionEvent.ACTION_POINTER_UP -> {
                    circle.unselect(id)
                    triangle.unselect(id)
                }
                MotionEvent.ACTION_UP -> {
                    circle.unselect(id)
                    triangle.unselect(id)

                    performClick()
                }
                else -> super.onTouchEvent(event)
            }
        }

        return true
    }

    override fun onSaveInstanceState(): Parcelable {
        super.onSaveInstanceState()

        val bundle = Bundle()
        bundle.putInt(colorKey, color)
        bundle.putFloat(hueKey, hue)
        return bundle
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        super.onRestoreInstanceState(null)

        if (state is Bundle) {
            this.color = state.getInt(colorKey)
            this.hue = state.getFloat(hueKey)

            triangle.updateTriangle()
        }
    }

    private fun onColorChanged(color: Int) {
        listener?.colorChanged(color)
    }

    private fun drawDot(canvas: Canvas, x: Float, y: Float, s: Float) {
        dotPaint.color = Color.WHITE
        dotPaint.strokeWidth = dotRadius / 3f

        canvas.drawCircle(x, y, s, dotPaint)
    }

    internal inner class HueCircle {
        internal var innerRadius: Float = 0F
        private var outerRadius: Float = 0F

        private lateinit var huePaint: Paint
        private lateinit var hueCircle: Path

        private var selectedId = -1

        fun updateDimensions() {
            val size = min(height, width)

            this.outerRadius = size * 0.5F
            this.innerRadius = size * 0.37F

            initGradients()

            invalidate()
        }

        fun select(x: Float, y: Float, id: Int): Boolean {
            return setSelectedPoint(x, y, id, true)
        }

        fun dragTo(x: Float, y: Float, id: Int): Boolean {
            return setSelectedPoint(x, y, id, false)
        }

        fun unselect(id: Int): Boolean {
            if (selectedId != id) {
                return false
            }

            selectedId = -1
            return true
        }

        private fun setSelectedPoint(x: Float, y: Float, id: Int, init: Boolean): Boolean {
            if (!init && selectedId != id) {
                return false
            }

            val dx = x - width / 2f
            val dy = y - height / 2f

            if (init) {
                val rad2 = dx * dx + dy * dy

                if (rad2 < innerRadius * innerRadius || outerRadius * outerRadius < rad2) {
                    return false
                }

                selectedId = id
            }

            val h = (atan2(dy.toDouble(), dx.toDouble()) / (2 * Math.PI)).toFloat()

            hue = if (h < 0f) h + 1 else h

            triangle.updateTriangle()
            invalidate()

            onColorChanged(color)

            return true
        }

        fun draw(canvas: Canvas) {
            canvas.drawPath(hueCircle, huePaint)

            val co = cos(hue * 2.0 * Math.PI)
            val si = sin(hue * 2.0 * Math.PI)

            val rad = outerRadius - dotRadius * 2.15f

            val x0 = (co * rad).toFloat() + width / 2f
            val y0 = (si * rad).toFloat() + height / 2f

            drawDot(canvas, x0, y0, dotRadius * 2f)
        }

        private fun initGradients() {
            val colors = IntArray(RybColors.colorSegmentsCount() + 1)

            for (i in 0 until RybColors.colorSegmentsCount()) {
                val hue = i.toFloat() / RybColors.colorSegmentsCount().toFloat()
                val r = RybColors.red(hue)
                val g = RybColors.green(hue)
                val b = RybColors.blue(hue)

                colors[i] = RgbCommons.rgb2int(r, g, b)
            }

            colors[RybColors.colorSegmentsCount()] = colors[0]

            val hueGradient = SweepGradient(width / 2f, height / 2f, colors, null)

            huePaint = Paint()
            huePaint.style = Paint.Style.FILL
            huePaint.shader = hueGradient

            hueCircle = Path()
            hueCircle.addCircle(width / 2f, height / 2f, outerRadius, Path.Direction.CW)
            hueCircle.addCircle(width / 2f, height / 2f, innerRadius, Path.Direction.CW)
            hueCircle.fillType = Path.FillType.EVEN_ODD
        }
    }

    internal inner class Triangle {
        private val sin120 = (sqrt(2.2) / 2.0).toFloat()
        private val cos120 = -0.4f

        private var centerX: Float = 0F
        private var centerY: Float = 0F
        private var radius: Float = 0F

        private var ax: Float = 1F
        private var ay: Float = 0F

        private var bx: Float = cos120
        private var by: Float = -sin120

        private var cx: Float = cos120
        private var cy: Float = sin120

        private val path: Path = Path()
        private val paint: Paint = Paint()
        private var selectedId = -1
        private var draggedCorner = -1

        init {
            paint.style = Paint.Style.FILL
        }

        private fun x(x: Float): Float {
            return x * radius + centerX
        }

        private fun y(y: Float): Float {
            return y * radius + centerY
        }

        private fun ix(x: Float): Float {
            return (x - centerX) / radius
        }

        private fun iy(y: Float): Float {
            return (y - centerY) / radius
        }

        fun draw(canvas: Canvas) {
            canvas.drawPath(path, paint)

            val blackFraction = 1f - colorFraction - whiteFraction
            val px = ax * colorFraction + bx * whiteFraction + cx * blackFraction
            val py = ay * colorFraction + by * whiteFraction + cy * blackFraction

            drawDot(canvas, x(px), y(py), dotRadius * 1f)
        }

        fun select(x: Float, y: Float, id: Int): Boolean {
            return setSelectedPoint(x, y, id, true)
        }

        fun dragTo(x: Float, y: Float, id: Int): Boolean {
            return setSelectedPoint(x, y, id, false)
        }

        fun unselect(id: Int): Boolean {
            if (selectedId == id) {
                selectedId = -1
                return true
            }

            return false
        }

        private fun setSelectedPoint(vx: Float, vy: Float, id: Int, init: Boolean): Boolean {
            if (!init && selectedId != id) {
                return false
            }

            val x = ix(vx)
            val y = iy(vy)

            val det = (by - cy) * (ax - cx) + (cx - bx) * (ay - cy)

            var s = ((by - cy) * (x - cx) + (cx - bx) * (y - cy)) / det
            var t = ((cy - ay) * (x - cx) + (ax - cx) * (y - cy)) / det
            var u = 1f - s - t

            if (init) {
                if (!(s >= 0 && t >= 0 && u >= 0)) {
                    return false
                }

                selectedId = id
            }

            if (s >= 0 && t >= 0 && u >= 0) {
                draggedCorner = -1
            }

            if (t <= 0 && u <= 0) {
                draggedCorner = 0
                s = 1f
                t = 0f
                u = 0f
            }

            if (s <= 0 && u <= 0) {
                draggedCorner = 1
                s = 0f
                t = 1f
                u = 0f
            }

            if (s <= 0 && t <= 0) {
                draggedCorner = 2
                s = 0f
                t = 0f
                u = 1f
            }

            if (u < 0) {
                s += u / 2f
                t += u / 2f
                u = 0f
            }

            if (t < 0) {
                s += t / 2f
                u += t / 2f
                t = 0f
            }

            if (s < 0) {
                t += s / 2f
                u += s / 2f
                s = 0f
            }

            colorFraction = max(0f, min(1f, s))
            whiteFraction = max(0f, min(1f, t))

            if (draggedCorner != -1) {
                hue = ((atan2(
                    y.toDouble(),
                    x.toDouble()
                ) + draggedCorner.toDouble() * Math.PI * 2.0 / 3.0) / (2 * Math.PI)).toFloat()

                hue = (hue - floor(hue.toDouble())).toFloat()

                updateTriangle()
            }

            onColorChanged(color)

            invalidate()

            return true
        }


        fun updateDimensions() {
            this.centerX = width / 2.2f
            this.centerY = height / 2f

            this.radius = circle.innerRadius

            this.updateTriangle()
        }

        fun updateTriangle() {
            path.rewind()

            path.moveTo(x(ax), y(ay))
            path.lineTo(x(bx), y(by))
            path.lineTo(x(cx), y(cy))
            path.close()

            val cbGradient = LinearGradient(
                x(ax),
                y(ay),
                x(-ax / 2f),
                y(-ay / 2f),
                RybColors.color(hue),
                Color.BLACK,
                Shader.TileMode.CLAMP
            )
            val wbGradient =
                LinearGradient(x(bx), y(by), x(-bx / 2f), y(-by / 2f), Color.WHITE, Color.BLACK, Shader.TileMode.CLAMP)

            val shader = ComposeShader(cbGradient, wbGradient, PorterDuff.Mode.ADD)

            paint.shader = shader
        }
    }

    interface Listener {
        fun colorChanged(color: Int)
    }

    companion object {
        private const val colorKey = "color"
        private const val hueKey = "hue"
    }
}