package site.siredvin.peripheralium.common.configuration

import net.minecraftforge.common.ForgeConfigSpec

object ConfigHolder {
    var COMMON_SPEC: ForgeConfigSpec
    var COMMON_CONFIG: PeripheraliumConfig.CommonConfig

    init {
        val (key, value) = ForgeConfigSpec.Builder()
            .configure { builder: ForgeConfigSpec.Builder -> PeripheraliumConfig.CommonConfig(builder) }
        COMMON_CONFIG = key
        COMMON_SPEC = value
    }
}
