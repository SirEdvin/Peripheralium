package site.siredvin.peripheralium.extra.dsl.rml1

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import java.lang.NumberFormatException

interface RenderInstruction {
    fun process(transformation: PoseStack): PoseStack
}

interface RMLLexeme {
    val name: String
    fun build(arguments: String): RenderInstruction
}

val limitingRange = -4f..4f

data class Translate(val x: Float, val y: Float, val z: Float) : RenderInstruction {
    companion object : RMLLexeme {
        override val name: String
            get() = "t"

        override fun build(arguments: String): RenderInstruction {
            val parsed = ArgumentParsingToolkit.asFloats(arguments, 3)
            return Translate(parsed[0], parsed[1], parsed[2])
        }
    }

    override fun process(transformation: PoseStack): PoseStack {
        transformation.translate(
            x.coerceIn(limitingRange).toDouble(),
            y.coerceIn(limitingRange).toDouble(),
            z.coerceIn(limitingRange).toDouble(),
        )
        return transformation
    }
}

data class Rotation(val axis: Vector3f, val angle: Float, val x: Float, val y: Float, val z: Float) : RenderInstruction {
    companion object : RMLLexeme {
        override val name: String
            get() = "r"

        override fun build(arguments: String): RenderInstruction {
            val parsed = ArgumentParsingToolkit.asSubstring(arguments, 5)
            val axis = when (parsed[0]) {
                "x" -> Vector3f.XP
                "y" -> Vector3f.YP
                "z" -> Vector3f.ZP
                else -> throw ArgumentParsingException("First argument of rotation should be axis and it should be: x, y or z")
            }
            val angle: Float
            val x: Float
            val y: Float
            val z: Float
            try {
                angle = parsed[1].toFloat()
            } catch (ignored: NumberFormatException) {
                throw ArgumentParsingException("Second argument of rotation should be angle in degrees as float")
            }
            try {
                x = parsed[2].toFloat()
            } catch (ignored: NumberFormatException) {
                throw ArgumentParsingException("Third argument of rotation should be x shift as float")
            }
            try {
                y = parsed[3].toFloat()
            } catch (ignored: NumberFormatException) {
                throw ArgumentParsingException("Fourth argument of rotation should be y shift as float")
            }
            try {
                z = parsed[4].toFloat()
            } catch (ignored: NumberFormatException) {
                throw ArgumentParsingException("Fifth argument of rotation should be z shift as float")
            }
            return Rotation(axis, angle, x, y, z)
        }
    }

    override fun process(transformation: PoseStack): PoseStack {
        transformation.pushPose()
        transformation.translate(
            x.coerceIn(limitingRange).toDouble(),
            y.coerceIn(limitingRange).toDouble(),
            z.coerceIn(limitingRange).toDouble(),
        )
        transformation.mulPose(axis.rotationDegrees(angle))
        transformation.translate(
            (-x.coerceIn(limitingRange)).toDouble(),
            (-y.coerceIn(limitingRange)).toDouble(),
            (-z.coerceIn(limitingRange)).toDouble(),
        )
        return transformation
    }
}

data class Scale(val x: Float, val y: Float, val z: Float) : RenderInstruction {
    companion object : RMLLexeme {
        override val name: String
            get() = "s"

        override fun build(arguments: String): RenderInstruction {
            val parsed = ArgumentParsingToolkit.asFloats(arguments, 3)
            return Scale(parsed[0], parsed[1], parsed[2])
        }
    }

    override fun process(transformation: PoseStack): PoseStack {
        transformation.scale(x.coerceIn(limitingRange), y.coerceIn(limitingRange), z.coerceIn(limitingRange))
        return transformation
    }
}
