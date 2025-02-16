package site.siredvin.peripheralium.util.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Transformation
import com.mojang.math.Vector3f
import dan200.computercraft.api.client.TransformedModel
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleSide
import net.minecraft.client.resources.model.ModelResourceLocation

object TurtleModelRender {
    fun <T : ITurtleUpgrade> facingBlockRender(upgrade: T, turtle: ITurtleAccess?, side: TurtleSide): TransformedModel {
        val stack = PoseStack()
        stack.pushPose()
        stack.scale(0.3f, 0.3f, 0.3f)
        stack.mulPose(Vector3f.XN.rotationDegrees(90f))
        stack.translate(0.0, -2.0, 1.05)
        if (side == TurtleSide.LEFT) {
            stack.translate(-0.6, 0.0, 0.0)
        } else {
            stack.translate(2.9, 0.0, 0.0)
        }
        return TransformedModel.of(upgrade.craftingItem, Transformation(stack.last().pose()))
    }

    fun <T : ITurtleUpgrade> baseRender(upgrade: T, turtleAccess: ITurtleAccess?, turtleSide: TurtleSide, leftModel: ModelResourceLocation? = null, rightModel: ModelResourceLocation? = null): TransformedModel {
        if (leftModel == null) {
            val stack = PoseStack()
            stack.pushPose()
            stack.mulPose(Vector3f.YN.rotationDegrees(90f))
            if (turtleSide == TurtleSide.LEFT) {
                stack.translate(0.0, 0.0, -0.6)
            } else {
                stack.translate(0.0, 0.0, -1.4)
            }
            return TransformedModel.of(upgrade.craftingItem, Transformation(stack.last().pose()))
        }
        return TransformedModel.of(if (turtleSide == TurtleSide.LEFT) leftModel else rightModel!!)
    }
}
