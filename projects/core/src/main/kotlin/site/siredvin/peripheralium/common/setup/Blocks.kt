package site.siredvin.peripheralium.common.setup

import net.minecraft.world.level.block.Block
import site.siredvin.peripheralium.util.BlockUtil
import site.siredvin.peripheralium.xplat.LibPlatform

object Blocks {
    val PERIPHERALIUM_BLOCK = LibPlatform.registerBlock("peripheralium_block", { Block(BlockUtil.defaultProperties(destroyTime = 0.5f)) })

    fun doSomething() {
    }
}
