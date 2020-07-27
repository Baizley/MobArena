package com.garbagemule.MobArena.signs;

import org.bukkit.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class RemovesSignAtLocationTest {

    SignStore signStore;
    SavesSignStore savesSignStore;

    RemovesSignAtLocation subject;

    @BeforeEach
    void setup() {
        signStore = mock(SignStore.class);
        savesSignStore = mock(SavesSignStore.class);

        subject = new RemovesSignAtLocation(
            signStore,
            savesSignStore
        );
    }

    @Test
    void noSignMeansNoWrite() {
        Location location = mock(Location.class);
        when(signStore.remove(location))
            .thenReturn(Optional.empty());

        subject.remove(location);

        verifyNoInteractions(savesSignStore);
    }

    @Test
    void signRemovedWritesStore() {
        Location location = mock(Location.class);
        ArenaSign sign = new ArenaSign(location, "", "", "");
        when(signStore.remove(location))
            .thenReturn(Optional.of(sign));

        subject.remove(location);

        verify(savesSignStore).save(signStore);
    }

}
