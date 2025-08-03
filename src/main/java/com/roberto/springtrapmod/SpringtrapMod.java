package com.roberto.springtrapmod;

import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingChangeTargetEvent;
import net.minecraftforge.event.level.ExplosionEvent.Detonate;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.world.damagesource.BlockDestructionCallback;

@Mod("springtrapmod")
public class SpringtrapMod {

    private static final UUID PROTECTED_UUID = UUID.fromString("de1cadd4-fd7c-44cc-bb3c-600a3086d5fc");

    public SpringtrapMod() {
        MinecraftForge.EVENT_BUS.register(this);

        BlockDestructionCallback.EVENT.register((Level world, BlockPos pos, BlockState state, LivingEntity breaker) -> {
            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(state.getBlock());
            return id == null
                || !id.getNamespace().equals("securitycraft")
                || !id.getPath().startsWith("reinforced_");
        });
    }

    @SubscribeEvent
    public void onChangeTarget(LivingChangeTargetEvent event) {
        if (event.getNewTarget() instanceof Player p && PROTECTED_UUID.equals(p.getUUID())) {
            event.setNewTarget(null);
            event.cancel();
        }
    }

    @SubscribeEvent
    public void onExplosion(Detonate event) {
        event.getAffectedBlocks().removeIf(pos -> {
            Block b = event.getLevel().getBlockState(pos).getBlock();
            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(b);
            return id != null
                && id.getNamespace().equals("securitycraft")
                && id.getPath().startsWith("reinforced_");
        });
    }
}
