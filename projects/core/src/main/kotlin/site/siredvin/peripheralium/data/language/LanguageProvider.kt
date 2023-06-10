package site.siredvin.peripheralium.data.language

import com.google.gson.JsonObject
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import java.util.concurrent.CompletableFuture
import java.util.stream.Stream

abstract class LanguageProvider(
    private val output: PackOutput,
    private val modID: String,
    private val locale: String,
    private val informationHolder: ModInformationHolder,
    private vararg val textRecords: TextRecord,
) : DataProvider {
    private val translations: MutableMap<String, String> = mutableMapOf()
    override fun run(cachedOutput: CachedOutput): CompletableFuture<*> {
        addTranslations()
        getExpectedKeys().forEach { x -> check(translations.containsKey(x)) { "No translation for $x" } }

        val json = JsonObject()
        for ((key, value) in translations) json.addProperty(
            key,
            value,
        )
        return DataProvider.saveStable(
            cachedOutput,
            json,
            output.outputFolder.resolve("assets/$modID/lang/$locale.json"),
        )
    }

    open fun getExpectedKeys(): Stream<String> {
        return Stream.of(
            informationHolder.getBlocks().stream().map { it.get().descriptionId },
            informationHolder.getItems().stream().map { it.get().descriptionId },
            textRecords.map { it.textID }.stream(),
        ).flatMap { it }
    }

    abstract fun addTranslations()
    fun add(id: String, text: String) {
        require(!translations.containsKey(id)) { "Duplicate translation $id" }
        translations[id] = text
    }

    fun add(item: Item, text: String, tooltip: String? = null) {
        add(item.descriptionId, text)
        if (tooltip != null) {
            add(item.descriptionId + ".tooltip", tooltip)
        }
    }

    fun add(block: Block, text: String, tooltip: String? = null) {
        add(block.descriptionId, text)
        if (tooltip != null) {
            add(block.descriptionId.replace("block", "item") + ".tooltip", tooltip)
        }
    }

    fun add(record: TextRecord, text: String) {
        add(record.textID, text)
    }

    fun addPocket(id: ResourceLocation, text: String) {
        add("pocket.$id", text)
    }

    fun addTurtle(id: ResourceLocation, text: String) {
        add("turtle.$id", text)
    }

    fun addUpgrades(id: ResourceLocation, text: String) {
        addPocket(id, text)
        addTurtle(id, text)
    }

    override fun getName(): String {
        return "Language$locale"
    }
}
