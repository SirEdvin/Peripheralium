package site.siredvin.peripheralium.util.representation

import com.google.gson.JsonParseException
import dan200.computercraft.shared.util.NBTUtil
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.Component
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
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.trading.Merchant
import net.minecraft.world.item.trading.MerchantOffer
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Fluid
import site.siredvin.peripheralium.ext.toRelative
import site.siredvin.peripheralium.storages.fluid.FluidStack
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.XplatRegistries
import java.util.Objects
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

    fun forEnchantment(enchantment: Enchantment, level: Int = 1): MutableMap<String, Any> = mutableMapOf(
        "displayName" to enchantment.getFullname(level).string,
        "name" to fromLegacyToNewID(enchantment.descriptionId),
        "level" to level,
    )

    fun forEnchantments(enchantments: MutableMap<Enchantment, Int>): List<Map<String, Any>> {
        val list = mutableListOf<Map<String, Any>>()
        for (enchantment: MutableMap.MutableEntry<Enchantment, Int> in enchantments.entries) {
            list.add(forEnchantment(enchantment.key, enchantment.value))
        }
        return list
    }

    /**
     * Both these functions are basically a copy of https://github.com/cc-tweaked/CC-Tweaked/blob/mc-1.20.x/projects/common/src/main/java/dan200/computercraft/shared/details/ItemDetails.java
     * to backport this changes and make 1.19 item_storage and inventory peripheral behave correctly
     */
    private fun forBaseItemStack(stack: ItemStack): MutableMap<String, Any> {
        val map: MutableMap<String, Any> = HashMap()
        map["name"] = stack.descriptionId
        map["count"] = stack.count
        val hash = NBTUtil.getNBTHash(stack.tag)
        if (hash != null) {
            map["nbt"] = hash
        }
        return map
    }

    private fun forDetailedItemStack(stack: ItemStack): MutableMap<String, Any> {
        val map = forBaseItemStack(stack)
        map["displayName"] = stack.hoverName.string
        map["maxCount"] = stack.maxStackSize

        if (stack.isDamageableItem) {
            map["damage"] = stack.damageValue
            map["maxDamage"] = stack.maxDamage
        }

        if (stack.item.isBarVisible(stack)) {
            map["durability"] = stack.item.getBarWidth(stack) / 13.0
        }

        map["tags"] = tagsToList(stack.tags)

        var tag = stack.tag
        if (tag != null && tag.contains("display", Tag.TAG_COMPOUND.toInt())) {
            var displayTag = tag.getCompound("display")
            if (displayTag.contains("Lore", Tag.TAG_LIST.toInt())) {
                var lore = displayTag.getList("Lore", Tag.TAG_STRING.toInt()).stream()
                    .map {
                        try {
                            return@map Component.Serializer.fromJson(it.asString)
                        } catch (e: JsonParseException) {
                            return@map null
                        }
                    }
                    .filter(Objects::nonNull)
                    .map { it.toString() }
                    .toList()
                if (!lore.isEmpty()) map["lore"] = lore
            }
        }

        /*
         * Used to hide some data from ItemStack tooltip.
         * @see https://minecraft.wiki/w/Tutorials/Command_NBT_tags
         * @see ItemStack#getTooltip
         */
        val hideFlags = if (tag != null) {
            tag.getInt("HideFlags")
        } else {
            0
        }

        val showEnchantments = ((stack.`is`(Items.ENCHANTED_BOOK) && (hideFlags and 32) == 0) || (stack.isEnchanted && (hideFlags and 1 == 0)))

        if (showEnchantments) {
            val enchantments = EnchantmentHelper.getEnchantments(stack)
            if (enchantments != null) {
                map["enchantments"] = forEnchantments(enchantments)
            }
        }

        if (tag != null && tag.getBoolean("Unbreakable") && (hideFlags and 4) == 0) {
            map["unbreakable"] = true
        }
        return map
    }

    fun forItemStack(stack: ItemStack, mode: RepresentationMode = RepresentationMode.DETAILED): MutableMap<String, Any> = when (mode) {
        RepresentationMode.BASE -> forBaseItemStack(stack)
        RepresentationMode.DETAILED -> forDetailedItemStack(stack)
        RepresentationMode.FULL -> {
            val base = forDetailedItemStack(stack)
            val tagData = stack.tag?.let { PeripheraliumPlatform.nbtToLua(it) }
            if (tagData != null) {
                base["rawNBT"] = tagData
            }
            base
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

    fun forFluid(fluid: Fluid): MutableMap<String, Any?> = mutableMapOf(
        "name" to XplatRegistries.FLUIDS.getKey(fluid).toString(),
    )

    fun forMobEffect(effect: MobEffect): MutableMap<String, Any> = hashMapOf(
        "displayName" to effect.displayName.string,
        "name" to fromLegacyToNewID(effect.descriptionId),
    )

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

    fun <T> tagsToList(tags: Stream<TagKey<T>>): List<String> = tags.map { key -> key.location.toString() }.collect(Collectors.toList())

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
    fun fromLegacyToNewID(legacyID: String): String = legacyID.substring(legacyID.indexOf(".") + 1).replace(".", ":")
}
