package site.siredvin.peripheralium.data

import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.data.language.TextRecord

enum class LibText(private val override: String? = null): TextRecord {
    PRESS_FOR_DESCRIPTION,
    CREATIVE_TAB(override = String.format("itemGroup.%s.tab", PeripheraliumCore.MOD_ID)),
    EMPTY_ENERGY,
    TURTLE_FUEL_ENERGY,
    FORGE_ENERGY,
    ;

    override val textID: String by lazy {
        if (override != null) { override}
        else {String.format("text.%s.%s", PeripheraliumCore.MOD_ID, name.lowercase())}
    }
}
