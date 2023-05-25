package site.siredvin.peripheralium.api.blockentities

import net.minecraft.world.entity.player.Player

interface IOwnedBlockEntity {
    var player: Player?
}
