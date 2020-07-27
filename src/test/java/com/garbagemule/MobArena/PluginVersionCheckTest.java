package com.garbagemule.MobArena;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class PluginVersionCheckTest {

    public static Stream<Arguments> data() {
        return Stream.of(
                // Patch: local < remote
                Arguments.arguments( "1.1.1", "1.1.2", true ),
                // Patch: local > remote
                Arguments.arguments( "1.1.2", "1.1.1", false ),
                // Patch: equal
                Arguments.arguments( "1.1.2", "1.1.2", false ),

                // Minor: local < remote
                Arguments.arguments( "1.1.1", "1.2.1", true ),
                Arguments.arguments( "1.1.2", "1.2.1", true ),
                // Minor: local > remote
                Arguments.arguments( "1.2.1", "1.1.1", false ),
                Arguments.arguments( "1.2.1", "1.1.2", false ),
                // Minor: equal
                Arguments.arguments( "1.2.1", "1.2.1", false ),
                Arguments.arguments( "1.2.2", "1.2.2", false ),

                // Major: local < remote
                Arguments.arguments( "1.1.1", "2.1.1", true ),
                Arguments.arguments( "1.1.2", "2.1.1", true ),
                Arguments.arguments( "1.2.1", "2.1.1", true ),
                Arguments.arguments( "1.2.2", "2.1.1", true ),
                // Major: local > remote
                Arguments.arguments( "2.1.1", "1.1.1", false ),
                Arguments.arguments( "2.1.1", "1.1.2", false ),
                Arguments.arguments( "2.1.1", "1.2.1", false ),
                Arguments.arguments( "2.1.1", "1.2.2", false ),
                // Major: equal
                Arguments.arguments( "2.1.1", "2.1.1", false ),
                Arguments.arguments( "2.2.1", "2.2.1", false ),
                Arguments.arguments( "2.2.2", "2.2.2", false ),

                // Incomplete: local < remote
                Arguments.arguments( "1",     "1.1.1", true ),
                Arguments.arguments( "1.1",   "1.1.1", true ),
                Arguments.arguments( "1.1.1", "2"    , true ),
                Arguments.arguments( "1.1.1", "1.2"  , true ),
                // Incomplete: local > remote
                Arguments.arguments( "1.1.1", "1",     false ),
                Arguments.arguments( "1.1.1", "1.1",   false ),
                Arguments.arguments( "2"    , "1.1.1", false ),
                Arguments.arguments( "1.2"  , "1.1.1", false ),

                // Snapshot: local < remote
                Arguments.arguments( "1.1.1-SNAPSHOT", "1.1.2", true ),
                Arguments.arguments( "1.1.1-SNAPSHOT", "1.2.1", true ),
                Arguments.arguments( "1.1.1-SNAPSHOT", "2.1.1", true ),
                // Snapshot: local > remote
                Arguments.arguments( "1.1.2-SNAPSHOT", "1.1.1", false ),
                Arguments.arguments( "1.2.1-SNAPSHOT", "1.1.1", false ),
                Arguments.arguments( "2.1.1-SNAPSHOT", "1.1.1", false ),
                // Snapshot: equal
                Arguments.arguments( "1.1.2-SNAPSHOT", "1.1.2", true ),
                Arguments.arguments( "1.2.1-SNAPSHOT", "1.2.1", true ),
                Arguments.arguments( "2.1.1-SNAPSHOT", "2.1.1", true )
        );
    }

    @ParameterizedTest
    @MethodSource("data")
    public void test(String local, String remote, boolean expected) {
        boolean actual = PluginVersionCheck.lessThan(local, remote);

        assertThat("Expected " + local + " < " + remote + "?",  expected, equalTo(actual));
    }

}
