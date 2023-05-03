package site.siredvin.peripheralium.tests

import net.minecraft.SharedConstants
import net.minecraft.server.Bootstrap
import org.junit.jupiter.api.extension.BeforeAllCallback
import org.junit.jupiter.api.extension.Extension
import org.junit.jupiter.api.extension.ExtensionContext

class FabricMinecraftBootstrap: Extension, BeforeAllCallback {
    override fun beforeAll(context: ExtensionContext) {
        bootstrap()
    }

    companion object {
        fun bootstrap() {
            SharedConstants.tryDetectVersion()
            Bootstrap.bootStrap()
        }
    }

}