package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.Messenger;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class HandlesSignDestructionTest {

    RemovesSignAtLocation removesSignAtLocation;
    Messenger messenger;

    HandlesSignDestruction subject;

    @BeforeEach
    void setup() {
        removesSignAtLocation = mock(RemovesSignAtLocation.class);
        messenger = mock(Messenger.class);

        subject = new HandlesSignDestruction(
            removesSignAtLocation,
            messenger
        );
    }

    @Test
    void doesNothingWithNonArenaSign() {
        Block block = mock(Block.class);
        Player player = mock(Player.class);
        when(removesSignAtLocation.remove(any()))
            .thenReturn(Optional.empty());
        BlockBreakEvent event = new BlockBreakEvent(block, player);

        subject.on(event);

        verifyNoInteractions(messenger);
    }

    @Test
    void reportsBreakageWithArenaSign() {
        Block block = mock(Block.class);
        Player player = mock(Player.class);
        ArenaSign sign = new ArenaSign(null, "", "", "");
        when(removesSignAtLocation.remove(any()))
            .thenReturn(Optional.of(sign));
        BlockBreakEvent event = new BlockBreakEvent(block, player);

        subject.on(event);

        verify(messenger).tell(eq(player), anyString());
    }

}
