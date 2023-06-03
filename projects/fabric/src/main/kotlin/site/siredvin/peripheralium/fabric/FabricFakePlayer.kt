package site.siredvin.peripheralium.fabric

import com.mojang.authlib.GameProfile
import net.fabricmc.fabric.api.entity.FakePlayer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player

class FabricFakePlayer(serverLevel: ServerLevel, gameProfile: GameProfile) :
    FakePlayer(serverLevel, gameProfile) {

    override fun canHarmPlayer(other: Player): Boolean {
        return true
    }

    override fun die(damageSource: DamageSource) {}

    override fun getEyeY(): Double {
        // Override this to make eye position correspond turtle eyes
        return y + 0.2
    }

    override fun getStandingEyeHeight(pose: Pose, dimensions: EntityDimensions): Float {
        return 0f
    }

    override fun getAttackStrengthScale(f: Float): Float {
        return 1f
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
