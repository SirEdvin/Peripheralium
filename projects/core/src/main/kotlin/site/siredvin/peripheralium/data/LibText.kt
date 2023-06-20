package site.siredvin.peripheralium.data

import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.data.language.TextRecord

enum class LibText : TextRecord {
    PRESS_FOR_DESCRIPTION,
    CREATIVE_TAB,
    EMPTY_ENERGY,
    TURTLE_FUEL_ENERGY,
    FORGE_ENERGY,
    ;

    override val textID: String by lazy {
        String.format("text.%s.%s", PeripheraliumCore.MOD_ID, name.lowercase())
    }
}
