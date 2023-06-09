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
        add(Blocks.PERIPHERALIUM_BLOCK.get(), "Блок перифераліуму")
        add(LibText.CREATIVE_TAB, "Перифераліум")
        add(LibText.PRESS_FOR_DESCRIPTION, "[§3Left shift§r] показити опис")
    }
}
