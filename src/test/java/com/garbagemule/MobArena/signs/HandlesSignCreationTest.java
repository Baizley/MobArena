package com.garbagemule.MobArena.signs;

import com.garbagemule.MobArena.Messenger;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;

@SuppressWarnings("WeakerAccess")
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
public class HandlesSignCreationTest {

    StoresNewSign storesNewSign;
    RendersTemplateById rendersTemplate;
    Messenger messenger;

    HandlesSignCreation subject;

    @BeforeEach
    void setup() {
        storesNewSign = mock(StoresNewSign.class);

        rendersTemplate = mock(RendersTemplateById.class);
        lenient()
            .when(rendersTemplate.render(any(), any()))
            .thenReturn(new String[]{"", "", "", ""});

        messenger = mock(Messenger.class);

        subject = new HandlesSignCreation(
            storesNewSign,
            rendersTemplate,
            messenger
        );
    }

    @Test
    void noHeaderNoAction() {
        String[] lines = {"why", "so", "serious", "?"};
        SignChangeEvent event = event(lines, null);

        subject.on(event);

        verifyZeroInteractions(storesNewSign, rendersTemplate, messenger);
    }

    @Test
    void nullLinesHandledGracefully() {
        String[] lines = {"[MA]", null, null, null};
        SignChangeEvent event = event(lines, null);

        subject.on(event);
    }

    @Test
    void useSignTypeIfTemplateNotAvailable() {
        String arenaId = "castle";
        String type = "join";
        String[] lines = {"[MA]", arenaId, type, null};
        Location location = mock(Location.class);
        SignChangeEvent event = event(lines, location);

        subject.on(event);

        verify(storesNewSign).store(location, arenaId, type, type);
    }

    @Test
    void useTemplateIfAvailable() {
        String arenaId = "castle";
        String type = "join";
        String templateId = "potato";
        String[] lines = {"[MA]", arenaId, type, templateId};
        Location location = mock(Location.class);
        SignChangeEvent event = event(lines, location);

        subject.on(event);

        verify(storesNewSign).store(location, arenaId, templateId, type);
    }

    @Test
    void typeIsLowercased() {
        String type = "JOIN";
        String[] lines = {"[MA]", "", type, ""};
        SignChangeEvent event = event(lines, null);

        subject.on(event);

        String lower = type.toLowerCase();
        verify(storesNewSign).store(any(), any(), any(), eq(lower));
    }

    @Test
    void templateIdIsLowercased() {
        String templateId = "BEST-TEMPLATE";
        String[] lines = {"[MA]", "", "", templateId};
        SignChangeEvent event = event(lines, null);

        subject.on(event);

        String lower = templateId.toLowerCase();
        verify(storesNewSign).store(any(), any(), eq(lower), any());
    }

    @Test
    void rendersTemplateAfterStoring() {
        String arenaId = "castle";
        String type = "join";
        String templateId = "potato";
        String[] lines = {"[MA]", arenaId, type, templateId};
        Location location = mock(Location.class);
        SignChangeEvent event = event(lines, location);

        subject.on(event);

        verify(rendersTemplate).render(templateId, arenaId);
    }

    @Test
    void errorPassedToMessenger() {
        String msg = "you messed up";
        doThrow(new IllegalArgumentException(msg))
            .when(storesNewSign).store(any(), any(), any(), any());
        String[] lines = {"[MA]", "", "", ""};
        SignChangeEvent event = event(lines, null);

        subject.on(event);

        verifyNoInteractions(rendersTemplate);
        verify(messenger).tell(event.getPlayer(), msg);
    }

    private SignChangeEvent event(String[] lines, Location location) {
        Block block = mock(Block.class);
        lenient().when(block.getLocation()).thenReturn(location);
        Player player = mock(Player.class);
        return new SignChangeEvent(block, player, lines);
    }

}
