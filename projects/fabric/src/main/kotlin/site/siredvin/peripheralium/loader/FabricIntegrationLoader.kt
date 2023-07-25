package site.siredvin.peripheralium.loader

import net.fabricmc.loader.api.FabricLoader
import org.apache.logging.log4j.Logger

class FabricIntegrationLoader(corePackage: String, logger: Logger) : BaseIntegrationLoader(corePackage, logger) {
    override fun isModPresent(modID: String): Boolean {
        return FabricLoader.getInstance().allMods.stream().anyMatch { it.metadata.id == modID }
    }
}
