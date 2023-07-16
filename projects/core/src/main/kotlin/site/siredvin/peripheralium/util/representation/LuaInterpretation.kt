package site.siredvin.peripheralium.util.representation

import dan200.computercraft.api.lua.LuaException
import net.minecraft.ResourceLocationException
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.level.block.state.properties.EnumProperty
import net.minecraft.world.level.block.state.properties.IntegerProperty
import net.minecraft.world.level.block.state.properties.Property
import site.siredvin.peripheralium.ext.toRelative
import site.siredvin.peripheralium.xplat.XplatRegistries

object LuaInterpretation {
    // BlockPos tricks
    @Throws(LuaException::class)
    fun asBlockPos(table: Map<*, *>): BlockPos {
        if (!table.containsKey("x") || !table.containsKey("y") || !table.containsKey("z")) throw LuaException("Table should be block position table")
        val x = table["x"]
        val y = table["y"]
        val z = table["z"]
        if (x !is Number || y !is Number || z !is Number) throw LuaException("Table should be block position table")
        return BlockPos(x.toInt(), y.toInt(), z.toInt())
    }

    @Throws(LuaException::class)
    fun asBlockPos(center: BlockPos, table: Map<*, *>): BlockPos {
        val relative = asBlockPos(table)
        return BlockPos(center.x + relative.x, center.y + relative.y, center.z + relative.z)
    }

    @Throws(LuaException::class)
    fun asBlockPos(center: BlockPos, table: Map<*, *>, facing: Direction): BlockPos {
        val relative = asBlockPos(table).toRelative(facing.opposite)
        return BlockPos(center.x + relative.x, center.y + relative.y, center.z + relative.z)
    }

    @Throws(LuaException::class)
    fun asRotation(rotation: String): Rotation {
        try {
            return Rotation.valueOf(rotation.uppercase())
        } catch (exc: IllegalArgumentException) {
            val allValues = Rotation.values().joinToString(", ") { it.name.lowercase() }
            throw LuaException("Rotation should be one of: $allValues")
        }
    }

    @Throws(LuaException::class)
    fun asID(id: String): ResourceLocation {
        return try {
            ResourceLocation(id)
        } catch (e: ResourceLocationException) {
            throw LuaException(e.message)
        }
    }

    @Throws(LuaException::class)
    fun asItemStack(obj: Any?): ItemStack {
        if (obj is String) {
            val candidate = XplatRegistries.ITEMS.get(asID(obj)).defaultInstance
            if (candidate.isEmpty) throw LuaException("Cannot find item with id $obj")
            return candidate
        }
        if (obj is Map<*, *>) {
            val id = obj["item"] as? String ?: throw LuaException("Item stack table should contains item field with item id")
            val count = obj.getOrDefault("count", 1) as? Number ?: throw LuaException("Count field should be a number")
            val candidate = XplatRegistries.ITEMS.get(asID(id)).defaultInstance
            if (candidate.isEmpty) throw LuaException("Cannot find item with id $obj")
            return candidate.copyWithCount(count.toInt())
        }
        throw LuaException("Item stack should be item id or table with item id and count")
    }

    @Throws(LuaException::class)
    fun asBlockStateAttrs(state: BlockState, blockAttrs: Map<*, *>): BlockState {
        var changeableState: BlockState = state
        blockAttrs.forEach { attr ->
            val property = state.properties.find { it.name.equals(attr.key) }
                ?: throw LuaException(String.format("Unknown property name %s", attr.key))
            when (property) {
                is EnumProperty -> {
                    val value = attr.value.toString().lowercase()
                    val targetValue = property.getPossibleValues().find { it.toString().lowercase() == value }
                        ?: throw LuaException(
                            java.lang.String.format(
                                "Incorrect value %s, only %s is allowed",
                                attr.key,
                                property.getPossibleValues().joinToString(", "),
                            ),
                        )
                    val trickedProperty = property as Property<Comparable<Any>>
                    changeableState = changeableState.setValue(trickedProperty, targetValue as Comparable<Any>)
                }

                is BooleanProperty -> {
                    if (attr.value !is Boolean) {
                        throw LuaException(String.format("Incorrect value %s, should be boolean", attr.key))
                    }
                    changeableState = changeableState.setValue(property, attr.value as Boolean)
                }

                is IntegerProperty -> {
                    if (attr.value !is Number) {
                        throw LuaException(String.format("Incorrect value %s, should be boolean", attr.key))
                    }
                    changeableState = changeableState.setValue(property, (attr.value as Number).toInt())
                }
            }
        }
        return changeableState
    }

    @Throws(LuaException::class)
    fun asBlockState(table: Map<*, *>): BlockState? {
        if (table.containsKey("block")) {
            val blockID = table["block"].toString()
            val block = XplatRegistries.BLOCKS.get(ResourceLocation(blockID))
            if (block == net.minecraft.world.level.block.Blocks.AIR) {
                throw LuaException(String.format("Cannot find block %s", table["block"]))
            }
            var targetState = block.defaultBlockState()
            if (table.containsKey("attrs")) {
                val blockAttrs = table["attrs"] as? Map<*, *> ?: throw LuaException("attrs should be a table")
                targetState = asBlockStateAttrs(targetState, blockAttrs)
            }
            return targetState
        }
        return null
    }
}
