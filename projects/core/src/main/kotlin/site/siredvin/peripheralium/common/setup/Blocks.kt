package site.siredvin.peripheralium.common.setup

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import site.siredvin.peripheralium.xplat.LibPlatform

object Blocks {
    val PERIPHERALIUM_BLOCK = LibPlatform.registerBlock("peripheralium_block", { Block(BlockBehaviour.Properties.of(Material.STONE).destroyTime(0.5f)) })

    fun doSomething() {
    }
}
