package site.siredvin.peripheralium.util.representation

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Registry
import net.minecraft.tags.TagKey
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.trading.MerchantOffer
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.peripheralium.ext.toRelative
import java.util.stream.Collectors
import java.util.stream.Stream

object LuaRepresentation {

    fun forBlockState(state: BlockState): MutableMap<String, Any> {
        val data: MutableMap<String, Any> = HashMap()
        data["name"] = state.block.name.string
        data["tags"] = tagsToList(state.tags)
        return data
    }

    fun forEntity(entity: Entity): MutableMap<String, Any> {
        val data: MutableMap<String, Any> = HashMap()
        data["id"] = entity.id
        data["uuid"] = entity.stringUUID
        data["category"] = entity.type.category.name
        data["type"] = entity.type.description.string
        data["name"] = entity.name.string
        data["tags"] = entity.tags
        return data
    }

    fun <T: Entity> withPos(entity: T, facing: Direction, center: BlockPos, converter: (T) -> (MutableMap<String, Any>)):  MutableMap<String, Any> {
        val base = converter(entity)
        base.putAll(forBlockPos(entity.blockPosition(), facing, center))
        return base
    }

    fun <T> withPos(value: T, pos: BlockPos, facing: Direction, center: BlockPos, converter: (T) -> (MutableMap<String, Any>)):  MutableMap<String, Any> {
        val base = converter(value)
        base.putAll(forBlockPos(pos, facing, center))
        return base
    }

    fun forBlockPos(pos: BlockPos, facing: Direction, center: BlockPos): MutableMap<String, Any> {
        val transformedPos = pos.subtract(center).toRelative(facing)
        val map: MutableMap<String, Any> = HashMap()
        map["x"] = transformedPos.x
        map["y"] = transformedPos.y
        map["z"] = transformedPos.z
        return map
    }

    fun forItemStack(stack: ItemStack): MutableMap<String, Any> {
        val map = forItem(stack.item)
        map["tags"] = tagsToList(stack.tags)
        map["count"] = stack.count
        map["maxStackSize"] = stack.maxStackSize
        return map
    }

    fun forItem(item: Item): MutableMap<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map["technicalName"] = Registry.ITEM.getKey(item).toString()
        map["name"] = item.description.string
        return map
    }

    fun forMobEffect(effect: MobEffect): MutableMap<String, Any> {
        return hashMapOf(
            "name" to effect.displayName.string,
            "technicalName" to effect.descriptionId,
        )
    }

    fun forMobEffectInstance(effectInstance: MobEffectInstance): MutableMap<String, Any> {
        val base = forMobEffect(effectInstance.effect)
        base.putAll(mapOf(
            "duration" to effectInstance.duration,
            "amplifier" to effectInstance.amplifier,
            "isAmbient" to effectInstance.isAmbient
        ))
        return base
    }

    fun <T> tagsToList(tags: Stream<TagKey<T>>): List<String> {
        return tags.map { key -> key.location.toString() }.collect(Collectors.toList())
    }

    fun getVillagerOffersAsMap(villager: Villager): MutableMap<Int, Any> {
        val offersMap : MutableMap<Int, Any> = HashMap()
        var index: Int = 1
        for(merchantOffer:MerchantOffer in villager.offers) {
            val offerMap : MutableMap<String, Any> = HashMap()
            offerMap["first_cost"] = getItemStackAsMap(merchantOffer.costA)
            offerMap["second_cost"] = getItemStackAsMap(merchantOffer.costB)
            offerMap["result"] = getItemStackAsMap(merchantOffer.result)
            offersMap[index++] = offerMap
        }
        return offersMap
    }
    private fun getItemStackAsMap(itemStack: ItemStack): MutableMap<String, Any> {
        val itemStackMap : MutableMap<String, Any> = HashMap()
        itemStackMap["count"] = itemStack.count
//        var name = itemStack.displayName.string
//        name = name.substring(0, name.length-1).substring(1, )
        itemStackMap["name"] = formatID(itemStack.descriptionId)
        if(itemStack.isEnchanted || itemStack.`is`(Items.ENCHANTED_BOOK)) itemStackMap["enchantments"] = getEnchantmentsAsMap(itemStack)
        return itemStackMap
    }

    private fun getEnchantmentsAsMap(itemStack : ItemStack): MutableMap<String, Any> {
        val enchantmentsMap : MutableMap<String, Any> = HashMap()
        val enchantments: MutableMap<Enchantment, Int>? = EnchantmentHelper.getEnchantments(itemStack)
        for(enchantment:MutableMap.MutableEntry<Enchantment, Int> in enchantments!!.entries) {
            enchantmentsMap[formatID(enchantment.key.descriptionId)] = enchantment.value
        }
        return enchantmentsMap
    }

    private fun formatID(rawID: String): String {
        return rawID.substring(rawID.indexOf(".") + 1).replace(".", ":")
    }
}