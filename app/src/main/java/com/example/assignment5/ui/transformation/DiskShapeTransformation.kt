package com.example.assignment5.ui.transformation

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import coil.size.Size
import coil.transform.Transformation

class DiskShapeTransformation : Transformation {
    override val cacheKey: String
        get() = "disk_shape_transformation"

    override suspend fun transform(input: Bitmap, size: Size): Bitmap {
        val minEdge = minOf(input.width, input.height)
        val output = Bitmap.createBitmap(minEdge, minEdge, Bitmap.Config.ARGB_8888)

        val canvas = Canvas(output)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = android.graphics.BitmapShader(input, android.graphics.Shader.TileMode.CLAMP, android.graphics.Shader.TileMode.CLAMP)

        val path = Path()
        path.addOval(RectF(0f, 0f, minEdge.toFloat(), minEdge.toFloat()), Path.Direction.CCW)
        canvas.drawPath(path, paint)

        return output
    }
}
