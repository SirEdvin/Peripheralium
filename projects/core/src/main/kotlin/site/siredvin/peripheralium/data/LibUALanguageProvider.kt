package site.siredvin.peripheralium.data

import net.minecraft.data.PackOutput
import site.siredvin.peripheralium.PeripheraliumCore
import site.siredvin.peripheralium.common.setup.Blocks
import site.siredvin.peripheralium.common.setup.Items
import site.siredvin.peripheralium.data.language.LanguageProvider
import site.siredvin.peripheralium.xplat.LibPlatform

class LibUALanguageProvider(
    output: PackOutput,
) : LanguageProvider(output, PeripheraliumCore.MOD_ID, "uk_ua", LibPlatform.holder, *LibText.values()) {
    override fun addTranslations() {
        add(Items.PERIPHERALIUM_DUST.get(), "Перифераліумний пил")
        add(Items.PERIPHERALIUM_BLEND.get(), "Сирий перифераліум")
        add(Items.PERIPHERALIUM_UPGRADE_TEMPLATE.get(), "Ковальский щаблон з перифераліуму")
        add(Blocks.PERIPHERALIUM_BLOCK.get(), "Блок перифераліуму")
        add(LibText.CREATIVE_TAB, "Перифераліум")
        add(LibText.PRESS_FOR_DESCRIPTION, "[§3Left shift§r] показити опис")
        add(LibText.EMPTY_ENERGY, "Порожня енергія (якого біса?)")
        add(LibText.TURTLE_FUEL_ENERGY, "Паливо для черепах")
        add(LibText.FORGE_ENERGY, "Forge-енергія")
    }
}
