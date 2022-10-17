package site.siredvin.peripheralium.util

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.ResourceLocation

fun itemTooltip(descriptionId: String): MutableComponent {
    val lastIndex = descriptionId.lastIndexOf('.')
    return Component.translatable(
        String.format(
            "%s.tooltip.%s",
            descriptionId.substring(0, lastIndex).replaceFirst("^block".toRegex(), "item"),
            descriptionId.substring(lastIndex + 1)
        )
    )
}

fun itemExtra(descriptionId: String, extra: String): MutableComponent {
    val lastIndex = descriptionId.lastIndexOf('.')
    return Component.translatable(
        String.format(
            "%s.extra.%s.%s",
            descriptionId.substring(0, lastIndex).replaceFirst("^block".toRegex(), "item"),
            descriptionId.substring(lastIndex + 1), extra
        )
    )
}

fun itemExtra(descriptionId: String, extra: String, vararg args: Any): MutableComponent {
    val lastIndex = descriptionId.lastIndexOf('.')
    return Component.translatable(
        String.format(
            "%s.extra.%s.%s",
            descriptionId.substring(0, lastIndex).replaceFirst("^block".toRegex(), "item"),
            descriptionId.substring(lastIndex + 1), extra
        ),
        *args
    )
}

fun turtleAdjective(turtleID: ResourceLocation): String {
    return java.lang.String.format("turtle.%s.%s", turtleID.namespace, turtleID.path)
}

fun pocketAdjective(pocketID: ResourceLocation): String {
    return java.lang.String.format("pocket.%s.%s", pocketID.namespace, pocketID.path)
}

fun text(modID: String, name: String): MutableComponent {
    return Component.translatable(String.format("text.%s.%s", modID, name))
}

fun text(modID: String, name: String, vararg args: Any): MutableComponent {
    return Component.translatable(String.format("text.%s.%s", modID, name), *args)
}
