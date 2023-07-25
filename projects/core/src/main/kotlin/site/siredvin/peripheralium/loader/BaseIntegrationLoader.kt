package site.siredvin.peripheralium.loader

import org.apache.logging.log4j.Logger
import java.lang.Exception
import java.util.*

abstract class BaseIntegrationLoader(protected val corePackage: String, protected val logger: Logger) {
    abstract fun isModPresent(modID: String): Boolean
    fun maybeLoadIntegration(modID: String, path: String = "Integration"): Optional<Any> {
        val modPresent = isModPresent(modID)
        if (modPresent) {
            logger.info("Loading integration for $modID")
            return maybeLoadIntegration("$modID.$path")
        } else {
            logger.info("Mod $modID is not present, skip loading integration")
        }
        return Optional.empty()
    }

    private fun maybeLoadIntegration(path: String): Optional<Any> {
        return try {
            val clazz = Class.forName("$corePackage.integrations.$path")
            Optional.of(clazz.getDeclaredConstructor().newInstance())
        } catch (ignored: InstantiationException) {
            logger.info("Exception when loading integration $ignored")
            Optional.empty()
        } catch (ignored: IllegalAccessException) {
            logger.info("Exception when loading integration $ignored")
            Optional.empty()
        } catch (ignored: ClassNotFoundException) {
            logger.info("Exception when loading integration $ignored")
            Optional.empty()
        } catch (e: Exception) {
            e.printStackTrace()
            Optional.empty()
        }
    }
}
