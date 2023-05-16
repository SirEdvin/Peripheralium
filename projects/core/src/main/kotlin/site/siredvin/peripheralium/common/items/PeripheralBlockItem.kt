package site.siredvin.peripheralium.common.items

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.util.text
import java.util.function.Function
import java.util.function.Supplier

open class PeripheralBlockItem(block: Block, properties: Properties, private var enableSup: Supplier<Boolean>, private var alwaysShow: Boolean = false, private vararg val tooltipHook: Function<PeripheralBlockItem, Component?>) : DescriptiveBlockItem(block, properties) {
    private var _tooltips: List<Component>? = null

    private val tooltips: List<Component>
        get() {
            if (_tooltips == null)
                _tooltips = tooltipHook.mapNotNull { it.apply(this) }
            return _tooltips!!
        }

    override fun appendHoverText(
        itemStack: ItemStack,
        level: Level?,
        list: MutableList<Component>,
        tooltipFlag: TooltipFlag
    ) {
        super.appendHoverText(itemStack, level, list, tooltipFlag)
        if (alwaysShow || InputConstants.isKeyDown(Minecraft.getInstance().window.window, InputConstants.KEY_LSHIFT)) {
            list.addAll(tooltips)
        } else {
            list.add(text(PeripheraliumCore.MOD_ID, "press_for_description"))
        }
    }

    fun isEnabled(): Boolean {
        return enableSup.get()
    }
}