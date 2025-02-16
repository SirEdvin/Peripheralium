package site.siredvin.peripheralium.computercraft.turtle

import dan200.computercraft.api.client.TransformedModel
import dan200.computercraft.api.peripheral.IPeripheral
import dan200.computercraft.api.turtle.AbstractTurtleUpgrade
import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeType
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import site.siredvin.peripheralium.api.peripheral.IOwnedPeripheral
import site.siredvin.peripheralium.computercraft.peripheral.DisabledPeripheral
import site.siredvin.peripheralium.util.render.TurtleModelRender
import site.siredvin.peripheralium.util.turtleAdjective

abstract class BaseTurtleUpgrade<T : IOwnedPeripheral<*>>(
    id: ResourceLocation,
    type: TurtleUpgradeType,
    adjective: String,
    stack: ItemStack,
) : AbstractTurtleUpgrade(id, type, adjective, stack) {

    protected open val leftModel: ModelResourceLocation?
        get() = null

    protected open val rightModel: ModelResourceLocation?
        get() = null

    protected abstract fun buildPeripheral(turtle: ITurtleAccess, side: TurtleSide): T

    constructor(id: ResourceLocation, type: TurtleUpgradeType, stack: ItemStack) : this(
        id,
        type,
        turtleAdjective(id),
        stack,
    )

    override fun getModel(p0: ITurtleAccess?, p1: TurtleSide): TransformedModel = TurtleModelRender.baseRender(this, p0, p1, leftModel, rightModel)

    override fun createPeripheral(turtle: ITurtleAccess, side: TurtleSide): IPeripheral? {
        val peripheral = buildPeripheral(turtle, side)
        return if (!peripheral.isEnabled) {
            DisabledPeripheral
        } else {
            peripheral
        }
    }
}
