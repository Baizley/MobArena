package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.Messenger;
import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class InvokesSignActionTest {

    ArenaMaster arenaMaster;
    Messenger messenger;

    InvokesSignAction subject;

    @BeforeEach
    void setup() {
        arenaMaster = mock(ArenaMaster.class);
        messenger = mock(Messenger.class);

        subject = new InvokesSignAction(arenaMaster, messenger);
    }

    @Test
    void infoSignDoesNothing() {
        String arenaId = "castle";
        ArenaSign sign = new ArenaSign(null, "", arenaId, "info");
        Player player = mock(Player.class);

        subject.invoke(sign, player);

        verifyNoInteractions(arenaMaster);
    }

    @Test
    void joinSignCallsCanJoin() {
        String arenaId = "castle";
        ArenaSign sign = new ArenaSign(null, "", arenaId, "join");
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(arena);

        subject.invoke(sign, player);

        verify(arena).canJoin(player);
    }

    @Test
    void joinSignCallsPlayerJoin() {
        String arenaId = "castle";
        ArenaSign sign = new ArenaSign(null, "", arenaId, "join");
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(arena.canJoin(player)).thenReturn(true);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(arena);

        subject.invoke(sign, player);

        verify(arena).playerJoin(eq(player), any());
    }

    @Test
    void leaveSignCallsInChecks() {
        String arenaId = "castle";
        ArenaSign sign = new ArenaSign(null, "", arenaId, "leave");
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(arena);

        subject.invoke(sign, player);

        verify(arena).inArena(player);
        verify(arena).inLobby(player);
        verify(arena).inSpec(player);
    }

    @Test
    void leaveSignCallsPlayerLeave() {
        String arenaId = "castle";
        ArenaSign sign = new ArenaSign(null, "", arenaId, "leave");
        Player player = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(arena.inArena(player)).thenReturn(true);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(arena);

        subject.invoke(sign, player);

        verify(arena).playerLeave(player);
    }

    @Test
    void nonExistentArenaReportsToPlayer() {
        String arenaId = "castle";
        ArenaSign sign = new ArenaSign(null, "", arenaId, "join");
        Player player = mock(Player.class);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(null);

        subject.invoke(sign, player);

        verify(messenger).tell(eq(player), anyString());
    }

}
