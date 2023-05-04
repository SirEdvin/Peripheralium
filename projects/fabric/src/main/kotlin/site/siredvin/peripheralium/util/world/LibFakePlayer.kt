package site.siredvin.peripheralium.util.world

import com.mojang.authlib.GameProfile
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.stats.Stat
import net.minecraft.world.MenuProvider
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.block.entity.SignBlockEntity
import site.siredvin.peripheralium.PeripheraliumCore
import java.lang.ref.WeakReference
import java.util.*


class LibFakePlayer(
    level: ServerLevel, owner: Entity?, profile: GameProfile?,
): ServerPlayer(
    level.server,
    level,
    if (profile != null && profile.isComplete) profile else PROFILE
) {
    companion object {
        val PROFILE = GameProfile(UUID.fromString("6e483f02-30db-4454-b612-3a167614b276"), "[" + PeripheraliumCore.MOD_ID + "]")
    }

    private val owner: WeakReference<Entity>?

    init {
        if (owner != null) {
            customName = owner.name
            this.owner = WeakReference(owner)
        } else {
            this.owner = null
        }
        val playerList = level.server.playerList;
        val currentPlayer = playerList.getPlayer(profile!!.id);
        if (currentPlayer != null)
            advancements.setPlayer(currentPlayer)
    }

    override fun openTextEdit(p_175141_1_: SignBlockEntity) {}


    override fun isSilent(): Boolean {
        return true
    }

    override fun getEyeY(): Double {
        // Override this to make eye position correspond turtle eyes
        return y + 0.2
    }

    override fun playSound(soundIn: SoundEvent, volume: Float, pitch: Float) {}

    override fun getEyeHeight(pose: Pose): Float {
        return 0f;
    }

    override fun tick() {}

    override fun doTick() {
        super.doTick()
    }

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

    override fun getStandingEyeHeight(pose: Pose, dimensions: EntityDimensions): Float {
        return 0f
    }
}