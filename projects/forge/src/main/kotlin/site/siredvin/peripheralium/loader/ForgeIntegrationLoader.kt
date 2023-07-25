package site.siredvin.peripheralium.loader

import net.minecraftforge.fml.ModList
import org.apache.logging.log4j.Logger

class ForgeIntegrationLoader(corePackage: String, logger: Logger) : BaseIntegrationLoader(corePackage, logger) {
    override fun isModPresent(modID: String): Boolean {
        return ModList.get().isLoaded(modID)
    }
}
