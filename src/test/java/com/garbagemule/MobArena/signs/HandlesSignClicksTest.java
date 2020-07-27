package com.garbagemule.MobArena.signs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class HandlesSignClicksTest {

    SignStore signStore;
    InvokesSignAction invokesSignAction;

    HandlesSignClicks subject;

    @BeforeEach
    void setup() {
        signStore = mock(SignStore.class);
        lenient()
            .when(signStore.findByLocation(any()))
            .thenReturn(Optional.empty());
        invokesSignAction = mock(InvokesSignAction.class);

        subject = new HandlesSignClicks(signStore, invokesSignAction);
    }

    @Test
    void noBlockNoFun() {
        PlayerInteractEvent event = event(null, null);

        subject.on(event);

        verifyNoInteractions(signStore);
    }

    @Test
    void noSignBlockNoFun() {
        Block block = mock(Block.class);
        when(block.getState()).thenReturn(mock(Chest.class));
        PlayerInteractEvent event = event(null, block);

        subject.on(event);

        verifyNoInteractions(signStore);
    }

    @Test
    void nonArenaSignInvokesAction() {
        Block block = mock(Block.class);
        when(block.getState()).thenReturn(mock(Sign.class));
        PlayerInteractEvent event = event(null, block);

        subject.on(event);

        verify(signStore).findByLocation(any());
    }

    @Test
    void arenaSignInvokesAction() {
        Location location = mock(Location.class);
        Block block = mock(Block.class);
        when(block.getLocation()).thenReturn(location);
        when(block.getState()).thenReturn(mock(Sign.class));
        ArenaSign sign = new ArenaSign(location, "", "", "");
        when(signStore.findByLocation(location))
            .thenReturn(Optional.of(sign));
        Player player = mock(Player.class);
        PlayerInteractEvent event = event(player, block);

        subject.on(event);

        verify(invokesSignAction).invoke(sign, player);
    }

    private PlayerInteractEvent event(Player player, Block block) {
        return new PlayerInteractEvent(player, null, null, block, null);
    }

}
