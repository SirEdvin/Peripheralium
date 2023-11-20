package site.siredvin.peripheralium.common.configuration

import net.minecraftforge.common.ForgeConfigSpec
import site.siredvin.peripheralium.api.IConfigHandler
import site.siredvin.peripheralium.computercraft.peripheral.operation.UnconditionalOperations

object PeripheraliumConfig {

    val isInitialCooldownEnabled: Boolean
        get() = ConfigHolder.COMMON_CONFIG.IS_INITIAL_COOLDOWN_ENABLED.get()
    val initialCooldownSensetiveLevel: Int
        get() = ConfigHolder.COMMON_CONFIG.INITIAL_COOLDOWN_SENSENTIVE_LEVEL.get()
    val cooldownTrasholdLevel: Int
        get() = ConfigHolder.COMMON_CONFIG.COOLDOWN_TRASHOLD_LEVEL.get()
    val xpToFuelRate: Int
        get() = ConfigHolder.COMMON_CONFIG.XP_TO_FUEL_RATE.get()

    val itemStorageTransferLimit: Long
        get() = ConfigHolder.COMMON_CONFIG.ITEM_STORAGE_TRANSFER_LIMIT.get()
    val fluidStorageTransferLimit: Long
        get() = ConfigHolder.COMMON_CONFIG.FLUID_STORAGE_TRANSFER_LIMIT.get()

    class CommonConfig internal constructor(builder: ForgeConfigSpec.Builder) {

        var IS_INITIAL_COOLDOWN_ENABLED: ForgeConfigSpec.BooleanValue
        var INITIAL_COOLDOWN_SENSENTIVE_LEVEL: ForgeConfigSpec.IntValue
        var COOLDOWN_TRASHOLD_LEVEL: ForgeConfigSpec.IntValue
        var XP_TO_FUEL_RATE: ForgeConfigSpec.IntValue
        val ITEM_STORAGE_TRANSFER_LIMIT: ForgeConfigSpec.LongValue
        val FLUID_STORAGE_TRANSFER_LIMIT: ForgeConfigSpec.LongValue

        init {
            builder.push("limitations")
            ITEM_STORAGE_TRANSFER_LIMIT = builder.comment("Limits max item transfer per one operation")
                .defineInRange("itemStorageTransferLimit", 128L, 1L, Long.MAX_VALUE)
            FLUID_STORAGE_TRANSFER_LIMIT = builder.comment("Limits max fluid transfer per one operation")
                .defineInRange("fluidStorageTransferLimit", 5305500L, 1L, Long.MAX_VALUE)
            builder.pop()
            builder.push("cooldown")
            IS_INITIAL_COOLDOWN_ENABLED = builder.comment("Enables initial cooldown on peripheral initialization")
                .define("isInitialCooldownEnabled", true)
            INITIAL_COOLDOWN_SENSENTIVE_LEVEL = builder.comment("Determinates initial cooldown sensentive level, values lower then this value will not trigger initial cooldown")
                .defineInRange("initialCooldownSensetiveLevel", 6000, 0, Int.MAX_VALUE)
            COOLDOWN_TRASHOLD_LEVEL = builder.comment("Determinates trashold for cooldown to be stored")
                .defineInRange("cooldownTrashholdLevel", 100, 0, Int.MAX_VALUE)
            builder.pop()
            builder.push("experience")
            XP_TO_FUEL_RATE = builder.comment("Determinates amount xp to correspond one fuel point").defineInRange("xpToFuelRate", 10, 1, Int.MAX_VALUE)
            builder.pop()
            builder.push("operations")
            register(UnconditionalOperations.values(), builder)
            builder.pop()
        }

        private fun register(data: Array<out IConfigHandler>, builder: ForgeConfigSpec.Builder) {
            for (handler in data) {
                handler.addToConfig(builder)
            }
        }
    }
}
