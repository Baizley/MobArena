package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.waves.WaveManager;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class RendersTemplateTest {

    RendersTemplate subject;

    @BeforeEach
    void setup() {
        subject = new RendersTemplate();
    }


    @Test
    void rendersArenaName() {
        String name = "castle";
        Arena arena = arena(name, false, false);
        Template template = new Template.Builder("template")
            .withBase(new String[]{"<arena-name>", "", "", ""})
            .build();

        String[] result = subject.render(template, arena);

        String[] expected = new String[]{name, "", "", ""};
        assertThat(result, equalTo(expected));
    }

    @Test
    void defaultsToBaseIfArenaIsNotRunning() {
        Arena arena = arena("castle", false, false);
        String[] base = {"this", "is", "the", "base"};
        Template template = new Template.Builder("template")
            .withBase(base)
            .withRunning(new String[]{"here", "is", "running", "yo"})
            .build();

        String[] result = subject.render(template, arena);

        assertThat(result, equalTo(base));
    }

    @Test
    void idleOverridesBaseIfNotRunning() {
        Arena arena = arena("castle", false, false);
        String[] idle = {"relax", "don't", "do", "it"};
        Template template = new Template.Builder("template")
            .withBase(new String[]{"this", "is", "the", "base"})
            .withIdle(idle)
            .build();

        String[] result = subject.render(template, arena);

        assertThat(result, equalTo(idle));
    }

    @Test
    void runningOverridesBaseIfArenaIsRunning() {
        Arena arena = arena("castle", true, false);
        String[] running = {"here", "is", "running", "yo"};
        Template template = new Template.Builder("template")
            .withBase(new String[]{"this", "is", "the", "base"})
            .withRunning(running)
            .build();

        String[] result = subject.render(template, arena);

        assertThat(result, equalTo(running));
    }

    @Test
    void lobbyOverridesBaseIfPlayersInLobby() {
        Arena arena = arena("castle", false, true);
        String[] joining = {"we", "in", "da", "lobby"};
        Template template = new Template.Builder("template")
            .withBase(new String[]{"this", "is", "the", "base"})
            .withJoining(joining)
            .build();

        String[] result = subject.render(template, arena);

        assertThat(result, equalTo(joining));
    }

    @Test
    void readyOverridesLobbyIfPlayersReady() {
        Player ready = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(arena.configName()).thenReturn("castle");
        when(arena.isRunning()).thenReturn(false);
        when(arena.getPlayersInLobby()).thenReturn(Collections.singleton(ready));
        when(arena.getNonreadyPlayers()).thenReturn(Collections.emptyList());
        when(arena.getReadyPlayersInLobby()).thenReturn(Collections.singleton(ready));
        String[] readyTemplate = {"we", "are", "all", "ready"};
        Template template = new Template.Builder("template")
            .withBase(new String[]{"this", "is", "the", "base"})
            .withJoining(new String[]{"joining", "template", "is", "here"})
            .withReady(readyTemplate)
            .build();

        String[] result = subject.render(template, arena);

        assertThat(result, equalTo(readyTemplate));
    }

    @Test
    void readyPlayersReturnsJoiningIfNotDefined() {
        Player ready = mock(Player.class);
        Arena arena = mock(Arena.class);
        when(arena.configName()).thenReturn("castle");
        when(arena.isRunning()).thenReturn(false);
        when(arena.getPlayersInLobby()).thenReturn(Collections.singleton(ready));
        when(arena.getNonreadyPlayers()).thenReturn(Collections.emptyList());
        when(arena.getReadyPlayersInLobby()).thenReturn(Collections.singleton(ready));
        String[] joining = {"we", "in", "da", "lobby"};
        Template template = new Template.Builder("template")
            .withBase(new String[]{"this", "is", "the", "base"})
            .withJoining(joining)
            .build();

        String[] result = subject.render(template, arena);

        assertThat(result, equalTo(joining));
    }

    @Test
    void rendersReadyListEntries() {
        Player ready = mock(Player.class);
        when(ready.getName()).thenReturn("Bobcat00");
        Player notready = mock(Player.class);
        when(notready.getName()).thenReturn("garbagemule");
        Arena arena = mock(Arena.class);
        when(arena.configName()).thenReturn("castle");
        when(arena.isRunning()).thenReturn(false);
        when(arena.getNonreadyPlayers()).thenReturn(Collections.singletonList(notready));
        when(arena.getReadyPlayersInLobby()).thenReturn(Collections.singleton(ready));
        Template template = new Template.Builder("template")
            .withBase(new String[]{"<ready-1>", "<ready-2>", "<notready-1>", "<notready-2>"})
            .build();

        String[] result = subject.render(template, arena);

        String[] expected = {"Bobcat00", "", "garbagemule", ""};
        assertThat(result, equalTo(expected));
    }

    @Test
    void doesNotRenderInvalidListEntries() {
        Arena arena = arena("castle", false, true);
        String[] base = new String[]{"<ready-0>", "<ready--1>", "<ready-n>", "<ready-999999>"};
        Template template = new Template.Builder("template")
            .withBase(base)
            .build();

        String[] result = subject.render(template, arena);

        assertThat(result, equalTo(base));
    }

    private Arena arena(String name, boolean running, boolean lobby) {
        Arena arena = mock(Arena.class);
        when(arena.configName()).thenReturn(name);
        when(arena.isRunning()).thenReturn(running);
        lenient().when(arena.getPlayersInLobby()).thenReturn(lobby ? Collections.singleton(null) : Collections.emptySet());
        lenient().when(arena.getWaveManager()).thenReturn(mock(WaveManager.class));
        return arena;
    }

}
