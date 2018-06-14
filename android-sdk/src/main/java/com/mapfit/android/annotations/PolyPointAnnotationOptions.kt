package com.mapfit.android.annotations

/**
 * Base abstract class for composing styling of poly point shapes e.g. [Polyline], [Polygon].
 */
@Suppress("UNCHECKED_CAST")
abstract class PolyPointAnnotationOptions<out T> {

    internal var strokeWidth: Int = Integer.MIN_VALUE
    internal var strokeColor: String = ""
    internal var strokeOutlineColor: String = ""
    internal var strokeOutlineWidth: Int = Integer.MIN_VALUE
    internal var lineJoinType: JoinType = JoinType.MITER
    internal var drawOrder: Int = Integer.MIN_VALUE
    internal var data: Any? = null
    var layerName: String = ""

    /**
     * Sets stroke width of the line.
     *
     * @param width
     */
    fun strokeWidth(width: Int): T {
        this.strokeWidth = width
        return this as T
    }

    /**
     * Sets stroke color of the line.
     *
     * @param color
     */
    fun strokeColor(color: String): T {
        this.strokeColor = color
        return this as T
    }

    /**
     * Sets stroke outline color of the line.
     *
     * @param color
     */
    fun strokeOutlineColor(color: String): T {
        this.strokeOutlineColor = color
        return this as T
    }

    /**
     * Sets stroke outline width of the line.
     *
     * @param
     */
    fun strokeOutlineWidth(width: Int): T {
        this.strokeOutlineWidth = width
        return this as T
    }

    /**
     * Sets the shape type of the joints in multi-segment lines.
     *
     * @param joinType
     */
    fun lineJoinType(joinType: JoinType): T {
        this.lineJoinType = joinType
        return this as T
    }

    /**
     * Sets the poly shape's drawing order. The poly shape with higher draw order will be drawn above
     * the ones have lesser draw order.
     *
     * @param drawOrder of the poly shape
     */
    fun drawOrder(drawOrder: Int): T {
        this.drawOrder = drawOrder
        return this as T
    }

    /**
     * Sets the given object related with the poly shapes. Setting related object of poly shape as a tag makes it
     * easier to reach the object rather than storing a Map data structure.
     *
     * @param tag any object related to the poly shape
     */
    fun data(data: Any): T {
        this.data = data
        return this as T
    }

    /**
     * Sets the poly shape style with the style name that should existing in the YAML file.
     *
     * @param layerName
     */
    fun layerName(layerName: String): T {
        this.layerName = layerName
        return this as T
    }

}