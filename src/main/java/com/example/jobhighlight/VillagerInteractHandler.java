package com.example.jobhighlight;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.network.PacketDistributor;

/**
 * Serverseitig: Shift-Rechtsklick auf einen Villager.
 *
 * - Villager hat einen Arbeitsblock -> genau dieser Block wird markiert.
 * - Villager hat keinen Arbeitsblock (berufslos, Nitwit, Block verloren) -> alle Bloecke der Sorte,
 *   die der Spieler in der Hand haelt, werden im Umkreis markiert (z. B. um freie Lecterns zu finden).
 */
@EventBusSubscriber(modid = JobHighlight.MODID)
public final class VillagerInteractHandler {
    private static final int SEARCH_RADIUS = 32;
    private static final int SEARCH_RADIUS_Y = 16;
    private static final int MAX_RESULTS = 256;
    private static final int DURATION_MS = 15_000;

    private static final int COLOR_JOB_SITE = 0xFFAA00;
    private static final int COLOR_HELD_BLOCK = 0x00E5FF;

    private VillagerInteractHandler() {}

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) {
            return;
        }
        if (!(event.getTarget() instanceof Villager villager)) {
            return;
        }
        // Auf dem Client ist das Entity ein LocalPlayer -> hier nur die Serverseite behandeln
        if (!(event.getEntity() instanceof ServerPlayer player) || !player.isShiftKeyDown()) {
            return;
        }
        if (!(villager.level() instanceof ServerLevel level)) {
            return;
        }

        // Normale Villager-Interaktion (Handel o. ae.) unterdruecken
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);

        // 1) Arbeitsblock des Villagers
        Optional<GlobalPos> jobSite = villager.getBrain().getMemory(MemoryModuleType.JOB_SITE);
        if (jobSite.isPresent()
                && jobSite.get().dimension().equals(level.dimension())
                && level.hasChunkAt(jobSite.get().pos())) {
            BlockPos pos = jobSite.get().pos();
            send(player, List.of(pos), COLOR_JOB_SITE);
            player.displayClientMessage(Component.translatable(
                    "message.jobhighlight.job_site",
                    level.getBlockState(pos).getBlock().getName(),
                    pos.getX(), pos.getY(), pos.getZ()), true);
            return;
        }

        // 2) Kein Arbeitsblock -> gehaltenen Block in der Umgebung markieren
        ItemStack held = player.getMainHandItem();
        if (!(held.getItem() instanceof BlockItem blockItem)) {
            player.displayClientMessage(Component.translatable("message.jobhighlight.no_site_no_block"), true);
            return;
        }

        Block target = blockItem.getBlock();
        List<BlockPos> found = findBlocks(level, villager.blockPosition(), target);
        if (found.isEmpty()) {
            player.displayClientMessage(
                    Component.translatable("message.jobhighlight.held_none", target.getName(), SEARCH_RADIUS), true);
            return;
        }

        send(player, found, COLOR_HELD_BLOCK);
        player.displayClientMessage(
                Component.translatable("message.jobhighlight.held_found", found.size(), target.getName()), true);
    }

    private static List<BlockPos> findBlocks(ServerLevel level, BlockPos center, Block target) {
        List<BlockPos> result = new ArrayList<>();
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int dx = -SEARCH_RADIUS; dx <= SEARCH_RADIUS; dx++) {
            for (int dz = -SEARCH_RADIUS; dz <= SEARCH_RADIUS; dz++) {
                for (int dy = -SEARCH_RADIUS_Y; dy <= SEARCH_RADIUS_Y; dy++) {
                    cursor.set(center.getX() + dx, center.getY() + dy, center.getZ() + dz);
                    // Nicht geladene Chunks ueberspringen, damit nichts nachgeladen wird
                    if (level.isOutsideBuildHeight(cursor) || !level.hasChunkAt(cursor)) {
                        continue;
                    }
                    if (level.getBlockState(cursor).is(target)) {
                        result.add(cursor.immutable());
                        if (result.size() >= MAX_RESULTS) {
                            return result;
                        }
                    }
                }
            }
        }
        return result;
    }

    private static void send(ServerPlayer player, List<BlockPos> positions, int color) {
        PacketDistributor.sendToPlayer(player, new HighlightPayload(positions, color, DURATION_MS));
    }
}
