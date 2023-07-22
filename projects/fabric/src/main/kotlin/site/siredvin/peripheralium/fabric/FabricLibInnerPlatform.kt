package site.siredvin.peripheralium.fabric

import site.siredvin.peripheralium.PeripheraliumCore

object FabricLibInnerPlatform : FabricBaseInnerPlatform() {
    override val modID: String
        get() = PeripheraliumCore.MOD_ID
}
