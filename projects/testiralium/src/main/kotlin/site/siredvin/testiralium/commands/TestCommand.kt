package site.siredvin.testiralium.commands

/*Rewriting CCTestCommand from CC:Tweaked to kotlin and without CC:Tweaked dependency*/

import com.mojang.brigadier.CommandDispatcher
import dan200.computercraft.core.util.Nullability
import dan200.computercraft.gametest.core.CCTestCommand
import dan200.computercraft.mixin.gametest.TestCommandAccessor
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands.literal
import net.minecraft.gametest.framework.GameTestRegistry
import net.minecraft.gametest.framework.StructureUtils
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.level.block.entity.StructureBlockEntity

object TestCommand {

    const val COMMAND = "testeralium"
    fun register(dispatcher: CommandDispatcher<CommandSourceStack>) {
        dispatcher.register(literal(COMMAND).then(literal("import").executes {
            CCTestCommand.importFiles(it.source.server)
            0
        }))
        dispatcher.register(literal(COMMAND).then(literal("export").executes {
            CCTestCommand.exportFiles(it.source.server)

            for (function in GameTestRegistry.getAllTestFunctions()) {
                TestCommandAccessor.callExportTestStructure(it.source, function.structureName)
            }
            0
        }))
        dispatcher.register(literal(COMMAND).then(literal("regen-structures").executes {
            for (function in GameTestRegistry.getAllTestFunctions()) {
                dispatcher.execute("test import " + function.testName, it.source)
                TestCommandAccessor.callExportTestStructure(it.source, function.structureName)
            }
            0
        }))
        dispatcher.register(literal(COMMAND).then(literal("marker").executes {
            val player: ServerPlayer = it.source.playerOrException
            val pos = StructureUtils.findNearestStructureBlock(player.blockPosition(), 15, player.getLevel())
                ?: return@executes CCTestCommand.error(it.source, "No nearby test")

            val structureBlock = player.getLevel().getBlockEntity(pos) as StructureBlockEntity?
                ?: return@executes CCTestCommand.error(it.source, "No nearby structure block")
            val info = GameTestRegistry.getTestFunction(
                structureBlock!!.structurePath
            )

            // Kill the existing armor stand

            // Kill the existing armor stand
            player
                .getLevel().getEntities(
                    EntityType.ARMOR_STAND
                ) { x: ArmorStand ->
                    x.isAlive && x.name.string == info.testName
                }
                .forEach { obj: Entity -> obj.kill() }

            // And create a new one

            // And create a new one
            val nbt = CompoundTag()
            nbt.putBoolean("Marker", true)
            nbt.putBoolean("Invisible", true)
            val armorStand = Nullability.assertNonNull(EntityType.ARMOR_STAND.create(player.getLevel()))
            armorStand.readAdditionalSaveData(nbt)
            armorStand.copyPosition(player)
            armorStand.customName = Component.literal(info.testName)
            player.getLevel().addFreshEntity(armorStand)
            0
        }))
    }
}