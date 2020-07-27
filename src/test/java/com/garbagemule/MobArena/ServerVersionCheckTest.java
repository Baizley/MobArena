package com.garbagemule.MobArena;

import org.bukkit.Server;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class ServerVersionCheckTest {

    public Server server;

    @BeforeEach
    void setup() {
        server = mock(Server.class);
    }

    @Test
    void oneFourteen() {
        win("git-Spigot-cafebae-dedbeef (MC: 1.14)");
    }

    @Test
    void oneThirteen() {
        win("git-Spigot-f09662d-be557e6 (MC: 1.13.2)");
    }

    private void win(String version) {
        when(server.getVersion()).thenReturn(version);
        ServerVersionCheck.check(server);
    }

    @Test
    void oneTwelve() {
        fail("git-Spigot-79a30d7-acbc348 (MC: 1.12.2)");
    }

    @Test
    void oneEleven() {
        fail("git-Spigot-3fb9445-6e3cec8 (MC: 1.11.2)");
    }

    @Test
    void oneTen() {
        fail("git-Spigot-de459a2-51263e9 (MC: 1.10.2)");
    }

    @Test
    void oneNine() {
        fail("git-Spigot-c6871e2-0cd0397 (MC: 1.9.4)");
    }

    @Test
    void oneEight() {
        fail("git-Spigot-21fe707-e1ebe52 (MC: 1.8.8)");
    }

    private void fail(String version) {
        when(server.getVersion()).thenReturn(version);
        Assertions.assertThrows(IllegalStateException.class, () -> ServerVersionCheck.check(server));
    }

}
