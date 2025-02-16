package site.siredvin.peripheralium.forge

import com.mojang.authlib.GameProfile
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.Pose
import net.minecraft.world.entity.player.Player
import net.minecraftforge.common.util.FakePlayer
import java.util.*

class ForgeFakePlayer(level: ServerLevel, profile: GameProfile) : FakePlayer(level, profile) {

    override fun canHarmPlayer(other: Player): Boolean = true

    override fun openMenu(menu: MenuProvider?): OptionalInt = OptionalInt.empty()

    override fun startRiding(vehicle: Entity, force: Boolean): Boolean = false

    override fun getStandingEyeHeight(pose: Pose, dimensions: EntityDimensions): Float = 0f

    override fun getEyeY(): Double = y + 0.2

    override fun getAttackStrengthScale(f: Float): Float = 1f
}
