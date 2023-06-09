package site.siredvin.peripheralium.util

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

fun itemTooltip(descriptionId: String): MutableComponent {
    return Component.translatable("$descriptionId.tooltip")
}

fun itemExtra(descriptionId: String, extra: String): MutableComponent {
    return Component.translatable("$descriptionId.extra.$extra")
}

fun itemExtra(descriptionId: String, extra: String, vararg args: Any): MutableComponent {
    return Component.translatable("$descriptionId.extra.$extra", *args)
}

fun turtleAdjective(turtleID: ResourceLocation): String {
    return java.lang.String.format("turtle.%s.%s", turtleID.namespace, turtleID.path)
}

fun pocketAdjective(pocketID: ResourceLocation): String {
    return java.lang.String.format("pocket.%s.%s", pocketID.namespace, pocketID.path)
}
