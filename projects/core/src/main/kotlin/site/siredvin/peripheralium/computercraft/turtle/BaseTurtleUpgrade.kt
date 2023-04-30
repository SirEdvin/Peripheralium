package site.siredvin.peripheralium.computercraft.turtle

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.client.TransformedModel
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.client.resources.model.ModelResourceLocation
import com.mojang.math.Transformation
import dan200.computercraft.api.peripheral.IPeripheral
import site.siredvin.peripheralium.computercraft.peripheral.DisabledPeripheral

abstract class BaseTurtleUpgrade<T : IOwnedPeripheral<*>>(
    id: ResourceLocation,
    type: TurtleUpgradeType,
    adjective: String,
    stack: ItemStack
) : AbstractTurtleUpgrade(id, type, adjective, stack) {

    protected open val leftModel: ModelResourceLocation?
        get() = null

    protected open val rightModel: ModelResourceLocation?
        get() = null

    protected abstract fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): T

    override fun createPeripheral(turtle: ITurtleAccess, side: TurtleSide): IPeripheral? {
        val peripheral = buildPeripheral(turtle, side)
        return if (!peripheral.isEnabled) { DisabledPeripheral } else peripheral
    }
}