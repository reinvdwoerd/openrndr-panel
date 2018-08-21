package org.openrndr.panel.elements

import org.openrndr.draw.Drawer
import org.openrndr.draw.FontImageMap
import org.openrndr.draw.LineCap
import org.openrndr.panel.style.*
import org.openrndr.shape.Rectangle
import org.openrndr.text.Writer

import io.reactivex.subjects.PublishSubject

class Toggle : Element(ElementType("toggle")) {
    var label = ""
    var value = false

    class ValueChangedEvent(val source: Toggle,
                            val oldValue: Boolean,
                            val newValue: Boolean)

    class Events {
        val valueChanged = PublishSubject.create<ValueChangedEvent>()
    }

    val events = Events()


    override val widthHint: Double?
        get() {
            computedStyle.let { style ->
                val fontUrl = (root() as? Body)?.controlManager?.fontManager?.resolve(style.fontFamily) ?: "broken"
                val fontSize = (style.fontSize as? LinearDimension.PX)?.value ?: 14.0
                val fontMap = FontImageMap.fromUrl(fontUrl, fontSize)

                val writer = Writer(null)

                writer.box = Rectangle(0.0,
                        0.0,
                        Double.POSITIVE_INFINITY,
                        Double.POSITIVE_INFINITY)

                writer.drawStyle.fontMap = fontMap
                writer.newLine()
                writer.text(label, visible = false)

                return writer.cursor.x + (computedStyle.height as LinearDimension.PX).value - 8.0 + 5.0
            }
        }

    init {
        mouse.pressed.subscribe {
            it.cancelPropagation()
        }
        mouse.clicked.subscribe {
            value = !value
            draw.dirty = true
            events.valueChanged.onNext(Toggle.ValueChangedEvent(this, !value, value))
            it.cancelPropagation()
        }
    }

    /**
     * Emits the current value through the valueChanged event
     */
    fun emit() {
        events.valueChanged.onNext(Toggle.ValueChangedEvent(this, value, value))
    }

    override fun draw(drawer: Drawer) {

        drawer.pushModel()
        val checkBoxSize = layout.screenHeight - 8.0
        drawer.translate(0.0, (layout.screenHeight - checkBoxSize) / 2.0)
        drawer.strokeWeight = 1.0
        drawer.stroke = computedStyle.effectiveColor
        drawer.fill = null
        drawer.rectangle(0.0, 0.0, checkBoxSize, checkBoxSize)

        if (value) {
            drawer.strokeWeight = 2.0
            drawer.stroke = computedStyle.effectiveColor
            drawer.fill = null
            drawer.lineCap = LineCap.ROUND
            drawer.lineSegment(5.0, 5.0, checkBoxSize / 2.0 - 2.0, checkBoxSize / 2.0 - 2.0)
            drawer.lineSegment(checkBoxSize / 2.0 + 2.0, checkBoxSize / 2.0 + 2.0, checkBoxSize - 5.0, checkBoxSize - 5.0)
            drawer.lineSegment(checkBoxSize - 5.0, 5.0, checkBoxSize / 2.0 + 2.0, checkBoxSize / 2.0 - 2.0)
            drawer.lineSegment(checkBoxSize/2.0 -2.0, checkBoxSize/2.0 + 2.0, 5.0, checkBoxSize - 5.0)
        }


        drawer.popModel()

        drawer.fontMap = (root() as? Body)?.controlManager?.fontManager?.font(computedStyle)!!
        drawer.translate(5.0 + checkBoxSize, (layout.screenHeight / 2.0) + drawer.fontMap!!.height / 2.0)
        drawer.stroke = null
        drawer.fill = computedStyle.effectiveColor

        drawer.text(label, 0.0, 0.0)

    }

}