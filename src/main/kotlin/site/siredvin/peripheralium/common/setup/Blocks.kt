package site.siredvin.peripheralium.common.setup

import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.Material
import site.siredvin.peripheralium.ext.register

object Blocks {
    val PERIPHERALIUM_BLOCK = Block(BlockBehaviour.Properties.of(Material.STONE).destroyTime(0.5f)).register("peripheralium_block")

    fun doSomething() {
    }
}
