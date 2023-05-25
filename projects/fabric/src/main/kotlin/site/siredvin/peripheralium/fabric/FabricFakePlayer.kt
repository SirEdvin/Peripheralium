package site.siredvin.peripheralium.fabric

import com.mojang.authlib.GameProfile
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stat
import net.minecraft.world.MenuProvider
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player
import java.util.*

class FabricFakePlayer(serverLevel: ServerLevel, gameProfile: GameProfile) :
    ServerPlayer(serverLevel.server, serverLevel, gameProfile) {
    init {
        connection = FakeNetHandler(this)
    }

    override fun tick() {}

    override fun awardStat(stat: Stat<*>, increment: Int) {}
    override fun isInvulnerableTo(source: DamageSource): Boolean {
        return true
    }

    override fun canHarmPlayer(other: Player): Boolean {
        return true
    }

    override fun die(damageSource: DamageSource) {}
    override fun openMenu(menu: MenuProvider?): OptionalInt {
        return OptionalInt.empty()
    }

    override fun startRiding(vehicle: Entity, force: Boolean): Boolean {
        return false
    }

    override fun getEyeY(): Double {
        // Override this to make eye position correspond turtle eyes
        return y + 0.2
    }

    override fun getStandingEyeHeight(pose: Pose, dimensions: EntityDimensions): Float {
        return 0f
    }

    companion object {
        fun create(serverLevel: ServerLevel, profile: GameProfile): FabricFakePlayer {
            // Restore the previous player's advancements. See #564.
            val playerList = serverLevel.server.playerList
            val currentPlayer = playerList.getPlayer(profile.id)
            val fakePlayer = FabricFakePlayer(serverLevel, profile)
            if (currentPlayer != null) fakePlayer.advancements.setPlayer(currentPlayer)
            return fakePlayer
        }
    }
}
