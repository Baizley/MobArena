package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.framework.Arena;
import com.garbagemule.MobArena.framework.ArenaMaster;
import org.bukkit.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SuppressWarnings("WeakerAccess")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class StoresNewSignTest {

    ArenaMaster arenaMaster;
    TemplateStore templateStore;
    SignStore signStore;
    SavesSignStore savesSignStore;

    StoresNewSign subject;

    @BeforeEach
    void setup() {
        arenaMaster = mock(ArenaMaster.class);
        when(arenaMaster.getArenaWithName(any()))
            .thenReturn(null);

        templateStore = mock(TemplateStore.class);

        signStore = mock(SignStore.class);

        savesSignStore = mock(SavesSignStore.class);

        subject = new StoresNewSign(
            arenaMaster,
            templateStore,
            signStore,
            savesSignStore
        );
    }

    @Test
    void throwOnNonExistentArena() {
        Location location = mock(Location.class);
        String arenaId = "castle";
        Assertions.assertThrows(IllegalArgumentException.class, () -> subject.store(location, arenaId, "", ""));
    }

    @Test
    void throwOnNonExistentTemplate() {
        Location location = mock(Location.class);
        String arenaId = "castle";
        Arena arena = mock(Arena.class);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(arena);
        String templateId = "template";
        Assertions.assertThrows(IllegalArgumentException.class, () -> subject.store(location, arenaId, templateId, ""));
    }

    @Test
    void throwOnNonInvalidSignType() {
        Location location = mock(Location.class);
        String arenaId = "castle";
        Arena arena = mock(Arena.class);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(arena);
        String templateId = "a very nice template";
        Template template = template(templateId);
        when(templateStore.findById(templateId))
            .thenReturn(Optional.of(template));
        String signType = "not a real sign type";
        Assertions.assertThrows(IllegalArgumentException.class, () -> subject.store(location, arenaId, templateId, signType));
    }

    @Test
    void storesSignAndWritesToDisk() {
        Location location = mock(Location.class);
        String arenaId = "castle";
        Arena arena = mock(Arena.class);
        when(arenaMaster.getArenaWithName(arenaId))
            .thenReturn(arena);
        String templateId = "a very nice template";
        Template template = template(templateId);
        when(templateStore.findById(templateId))
            .thenReturn(Optional.of(template));
        String signType = "join";

        subject.store(location, arenaId, templateId, signType);

        ArgumentCaptor<ArenaSign> captor = ArgumentCaptor.forClass(ArenaSign.class);
        verify(signStore).store(captor.capture());
        verify(savesSignStore).save(signStore);
        ArenaSign sign = captor.getValue();
        assertThat(sign.location, equalTo(location));
        assertThat(sign.arenaId, equalTo(arenaId));
        assertThat(sign.templateId, equalTo(templateId));
        assertThat(sign.type, equalTo(signType));
    }

    private Template template(String id) {
        return new Template.Builder(id)
            .withBase(new String[]{"", "", "", ""})
            .build();
    }

}
