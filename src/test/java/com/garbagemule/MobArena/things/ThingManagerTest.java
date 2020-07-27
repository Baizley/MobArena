package com.garbagemule.MobArena.things;

import com.garbagemule.MobArena.MobArena;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

public class ThingManagerTest {

    private ThingManager subject;

    @BeforeEach
    void setup() {
        MobArena plugin = mock(MobArena.class);
        subject = new ThingManager(plugin);
    }

    @Test
    void afterCoreParsersInOrder() {
        ThingParser first = mock(ThingParser.class);
        ThingParser second = mock(ThingParser.class);
        when(second.parse(anyString())).thenReturn(mock(Thing.class));
        subject.register(first /*, false */);
        subject.register(second /*, false */);

        subject.parse("thing");

        InOrder order = inOrder(first, second);
        order.verify(first).parse("thing");
        order.verify(second).parse("thing");
    }

    @Test
    void beforeCoreParsersInverseOrder() {
        ThingParser first = mock(ThingParser.class);
        ThingParser second = mock(ThingParser.class);
        when(first.parse(anyString())).thenReturn(mock(Thing.class));
        subject.register(first, true);
        subject.register(second, true);

        subject.parse("thing");

        InOrder order = inOrder(first, second);
        order.verify(second).parse("thing");
        order.verify(first).parse("thing");
    }

    @Test
    void firstNonNullThingIsReturned() {
        Thing thing = mock(Thing.class);
        ThingParser first = mock(ThingParser.class);
        ThingParser second = mock(ThingParser.class);
        ThingParser third = mock(ThingParser.class);
        when(first.parse("thing")).thenReturn(null);
        when(second.parse("thing")).thenReturn(thing);
        when(third.parse("thing")).thenReturn(thing);
        subject.register(first);
        subject.register(second);
        subject.register(third);

        subject.parse("thing");

        verify(first).parse("thing");
        verify(second).parse("thing");
        verifyNoInteractions(third);
    }

    @Test
    void throwsIfNoParsersSucceed() {
        ThingParser first = mock(ThingParser.class);
        ThingParser second = mock(ThingParser.class);
        when(first.parse("thing")).thenReturn(null);
        when(second.parse("thing")).thenReturn(null);
        subject.register(first);
        subject.register(second);

        Assertions.assertThrows(InvalidThingInputString.class, () -> subject.parse("thing"));
    }

}
