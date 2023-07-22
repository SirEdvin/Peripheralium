package site.siredvin.peripheralium.tests;

import com.google.auto.service.AutoService;
import org.junit.jupiter.api.extension.Extension;
import site.siredvin.peripheralium.PeripheraliumCore;
import site.siredvin.peripheralium.fabric.FabricIngredients;
import site.siredvin.peripheralium.fabric.FabricLibPlatform;
import site.siredvin.peripheralium.fabric.FabricPeripheraliumPlatform;
import site.siredvin.peripheralium.fabric.FabricXplatTags;

@AutoService(Extension.class)
public class PeripheraliumInitialization implements Extension{

    public PeripheraliumInitialization() {
        PeripheraliumCore.INSTANCE.configure(FabricLibPlatform.INSTANCE, FabricPeripheraliumPlatform.INSTANCE, FabricIngredients.INSTANCE, FabricXplatTags.INSTANCE);
    }
}
