package site.siredvin.peripheralium.util.representation

import dan200.computercraft.api.detail.VanillaDetailRegistries
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.tags.TagKey
import net.minecraft.world.effect.MobEffect
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.ExperienceOrb
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.npc.Villager
import net.minecraft.world.entity.npc.VillagerProfession
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.trading.Merchant
import net.minecraft.world.item.trading.MerchantOffer
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import site.siredvin.peripheralium.ext.toRelative
import site.siredvin.peripheralium.storages.fluid.FluidStack
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.XplatRegistries
import java.util.stream.Collectors
import java.util.stream.Stream

@Suppress("MemberVisibilityCanBePrivate")
object LuaRepresentation {

    fun forBlockState(state: BlockState): MutableMap<String, Any> {
        val data: MutableMap<String, Any> = HashMap()
        data["name"] = XplatRegistries.BLOCKS.getKey(state.block).toString()
        data["displayName"] = state.block.name.string
        data["tags"] = tagsToList(state.tags)
        return data
    }

    fun forEntity(entity: Entity): MutableMap<String, Any> {
        val data: MutableMap<String, Any> = HashMap()
        data["name"] = entity.id
        data["uuid"] = entity.stringUUID
        data["category"] = entity.type.category.name
        data["type"] = entity.type.description.string
        data["displayName"] = entity.name.string
        data["tags"] = entity.tags
        return data
    }

    fun forLivingEntity(entity: LivingEntity): MutableMap<String, Any> {
        val base = forEntity(entity)
        base["health"] = entity.health
        return base
    }

    fun <T : Entity> withPos(entity: T, facing: Direction, center: BlockPos, converter: (T) -> (MutableMap<String, Any>)): MutableMap<String, Any> {
        val base = converter(entity)
        base.putAll(forBlockPos(entity.blockPosition(), facing, center))
        return base
    }

    fun <T> withPos(value: T, pos: BlockPos, facing: Direction, center: BlockPos, converter: (T) -> (MutableMap<String, Any>)): MutableMap<String, Any> {
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

    fun forEnchantment(enchantment: Enchantment, level: Int = 1): MutableMap<String, Any> {
        return mutableMapOf(
            "displayName" to enchantment.getFullname(level).string,
            "name" to fromLegacyToNewID(enchantment.descriptionId),
            "level" to level,
        )
    }

    fun forEnchantments(enchantments: MutableMap<Enchantment, Int>): List<Map<String, Any>> {
        val list = mutableListOf<Map<String, Any>>()
        for (enchantment: MutableMap.MutableEntry<Enchantment, Int> in enchantments.entries) {
            list.add(forEnchantment(enchantment.key, enchantment.value))
        }
        return list
    }

    fun forItemStack(stack: ItemStack, mode: RepresentationMode = RepresentationMode.DETAILED): MutableMap<String, Any> {
        return when (mode) {
            RepresentationMode.BASE -> VanillaDetailRegistries.ITEM_STACK.getBasicDetails(stack)
            RepresentationMode.DETAILED -> VanillaDetailRegistries.ITEM_STACK.getDetails(stack)
            RepresentationMode.FULL -> {
                val base = VanillaDetailRegistries.ITEM_STACK.getDetails(stack)
                val tagData = stack.tag?.let { PeripheraliumPlatform.nbtToLua(it) }
                if (tagData != null) {
                    base["rawNBT"] = tagData
                }
                base
            }
        }
    }

    fun forItem(item: Item): MutableMap<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map["name"] = item.descriptionId
        map["displayName"] = item.description.string
        return map
    }

    fun forFluidStack(fluid: FluidStack): MutableMap<String, Any?> {
        val baseInformation = forFluid(fluid.fluid)
        baseInformation["amount"] = fluid.amount
        if (fluid.tag != null) {
            baseInformation["nbt"] = PeripheraliumPlatform.nbtHash(fluid.tag!!)
        }
        return baseInformation
    }

    fun forFluid(fluid: Fluid): MutableMap<String, Any?> {
        return mutableMapOf(
            "name" to XplatRegistries.FLUIDS.getKey(fluid).toString(),
        )
    }

    fun forMobEffect(effect: MobEffect): MutableMap<String, Any> {
        return hashMapOf(
            "displayName" to effect.displayName.string,
            "name" to fromLegacyToNewID(effect.descriptionId),
        )
    }

    fun forMobEffectInstance(effectInstance: MobEffectInstance): MutableMap<String, Any> {
        val base = forMobEffect(effectInstance.effect)
        base.putAll(
            mapOf(
                "duration" to effectInstance.duration,
                "amplifier" to effectInstance.amplifier,
                "isAmbient" to effectInstance.isAmbient,
            ),
        )
        return base
    }

    fun <T> tagsToList(tags: Stream<TagKey<T>>): List<String> {
        return tags.map { key -> key.location.toString() }.collect(Collectors.toList())
    }

    fun forMerchantOffers(merchant: Merchant): Map<Int, Map<String, Any>> {
        val offers = mutableMapOf<Int, Map<String, Any>>()
        var currentIndex = 1
        for (merchantOffer: MerchantOffer in merchant.offers) {
            if (merchantOffer.isOutOfStock) {
                currentIndex++
                continue
            }
            val offerMap: MutableMap<String, Any> = HashMap()
            val inputs: MutableList<Map<String, Any>> = mutableListOf()
            inputs.add(forItemStack(merchantOffer.costA))
            if (!merchantOffer.costB.isEmpty) {
                inputs.add(forItemStack(merchantOffer.costB))
            }
            offerMap["inputs"] = inputs
            offerMap["outputs"] = listOf(forItemStack(merchantOffer.result))
            offers[currentIndex] = offerMap
            currentIndex++
        }
        return offers
    }

    fun forVillager(villager: Villager): Map<String, Any> {
        val data = mutableMapOf<String, Any>()
        val vilData = villager.villagerData
        if (vilData.profession != VillagerProfession.NONE) {
            data["profession"] = vilData.profession.name
            data["xp"] = villager.villagerXp
            data["level"] = vilData.level
            data["type"] = vilData.type.toString()
        }
        return data
    }

    fun forExpirenceOrb(orb: ExperienceOrb): MutableMap<String, Any> {
        val base = forEntity(orb)
        base["xpValue"] = orb.value
        return base
    }

    fun forPlayer(player: Player): MutableMap<String, Any> {
        val base = forLivingEntity(player)
        base["experienceLevel"] = player.experienceLevel
        base["foodLevel"] = player.foodData.foodLevel
        base["saturationLevel"] = player.foodData.saturationLevel
        base["isCreative"] = player.isCreative
        base["yRot"] = player.yRot
        base["xRot"] = player.xRot
        return base
    }

    /**
     * So, this function exists mostly for converting ids like minecraft.looting to more
     * simple for anyone minecraft:looting. Mostly applicable for enchantments and effects
     */
    fun fromLegacyToNewID(legacyID: String): String {
        return legacyID.substring(legacyID.indexOf(".") + 1).replace(".", ":")
    }
}
