package com.garbagemule.MobArena.things;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class CommandThingParserTest {

    private CommandThingParser subject;

    @BeforeEach
    void setup() {
        subject = new CommandThingParser();
    }

    @Test
    void emptyStringReturnsNull() {
        Thing result = subject.parse("");

        assertThat(result, is(nullValue()));
    }

    @Test
    void commandWithoutPrefixReturnsNull() {
        Thing result = subject.parse("/give <player> dirt");

        assertThat(result, is(nullValue()));
    }

    @Test
    void barePrefixReturnsNull() {
        Thing result = subject.parse("cmd");

        assertThat(result, is(nullValue()));
    }

    @Test
    void commandWithShortPrefix() {
        String command = "/give <player> dirt";

        Thing result = subject.parse("cmd:" + command);

        Thing expected = new CommandThing(command);
        assertThat(result, equalTo(expected));
    }

    @Test
    void commandWithLongPrefix() {
        String command = "/give <player> dirt";

        Thing result = subject.parse("command:" + command);

        Thing expected = new CommandThing(command);
        assertThat(result, equalTo(expected));
    }

    @Test
    void commandWithTitle() {
        String command = "/give <player> dirt";
        String title = "the best command";

        Thing result = subject.parse("cmd(" + title + "):" + command);

        Thing expected = new CommandThing(command, title);
        assertThat(result, equalTo(expected));
    }

    @Test
    void missingCloseParenThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> subject.parse("cmd(name:/give <player> dirt"));
    }

    @Test
    void missingColonAfterTitleThrows() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> subject.parse("cmd(name)"));
    }

}
