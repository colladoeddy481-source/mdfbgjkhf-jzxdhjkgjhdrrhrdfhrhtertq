package com.rickprime.mod;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod(RickPrimeMod.MODID)
public class RickPrimeMod {
    public static final String MODID = "rickprime";

    public RickPrimeMod() {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        public static final KeyMapping REGEN_KEY = new KeyMapping(
                "key.rickprime.regen",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_R,
                "key.categories.rickprime"
        );

        @SubscribeEvent
        public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(REGEN_KEY);
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
    public static class ForgeClientEvents {
        private static boolean isBrokenDown = false;

        @SubscribeEvent
        public static void onPlayerHurt(LivingHurtEvent event) {
            if (Minecraft.getInstance().player != null && event.getEntity().getId() == Minecraft.getInstance().player.getId()) {
                float currentHealth = Minecraft.getInstance().player.getHealth();
                float incomingDamage = event.getAmount();

                if (currentHealth - incomingDamage <= 1.0f && !isBrokenDown) {
                    event.setCanceled(true);
                    Minecraft.getInstance().player.setHealth(2.0f);
                    isBrokenDown = true;
                    Minecraft.getInstance().player.displayClientMessage(Component.literal("§c[Rick Prime] ¡Has quedado abatido! Presiona 'R' para regenerarte."), true);
                } else if (isBrokenDown) {
                    event.setCanceled(true);
                }
            }
        }

        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            if (ClientModEvents.REGEN_KEY.consumeClick() && isBrokenDown) {
                if (Minecraft.getInstance().player != null) {
                    isBrokenDown = false;
                    Minecraft.getInstance().player.heal(10.0f);
                    Minecraft.getInstance().player.displayClientMessage(Component.literal("§a[Rick Prime] ¡Te has regenerado con éxito!"), true);
                }
            }
        }
    }
}
