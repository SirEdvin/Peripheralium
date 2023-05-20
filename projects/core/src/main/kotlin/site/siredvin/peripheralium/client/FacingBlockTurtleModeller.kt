package site.siredvin.peripheralium.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.mojang.math.Transformation
import dan200.computercraft.api.client.TransformedModel
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleSide

class FacingBlockTurtleModeller<T: ITurtleUpgrade>: TurtleUpgradeModeller<T> {
    override fun getModel(upgrade: T, turtle: ITurtleAccess?, side: TurtleSide): TransformedModel {
        val stack = PoseStack()
        stack.pushPose()
        stack.scale(0.3f, 0.3f, 0.3f)
        stack.mulPose(Axis.XN.rotationDegrees(90f))
        stack.translate(0.0, -2.0, 1.05)
        if (side == TurtleSide.LEFT) {
            stack.translate(-0.6, 0.0, 0.0)
        } else {
            stack.translate(2.9, 0.0, 0.0)
        }
        return TransformedModel.of(upgrade.craftingItem, Transformation(stack.last().pose()))
    }
}