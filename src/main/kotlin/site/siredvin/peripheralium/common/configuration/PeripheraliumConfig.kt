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

    class CommonConfig internal constructor(builder: ForgeConfigSpec.Builder) {

        var IS_INITIAL_COOLDOWN_ENABLED: ForgeConfigSpec.BooleanValue
        var INITIAL_COOLDOWN_SENSENTIVE_LEVEL: ForgeConfigSpec.IntValue
        var COOLDOWN_TRASHOLD_LEVEL: ForgeConfigSpec.IntValue
        var XP_TO_FUEL_RATE: ForgeConfigSpec.IntValue

        init {
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