package com.github.phillip.h.acutelib.util;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UtilTest {

    @Test
    @DisplayName("stripLegacyFormattingCodes() correct")
    public void stripLegacyFormattingCodesCorrect() {
        MatcherAssert.assertThat(Util.stripLegacyFormattingCodes(null), nullValue());
        MatcherAssert.assertThat(Util.stripLegacyFormattingCodes(""), is(""));
        MatcherAssert.assertThat(Util.stripLegacyFormattingCodes("   "), is("   "));
        MatcherAssert.assertThat(Util.stripLegacyFormattingCodes("A"), is("A"));
        MatcherAssert.assertThat(Util.stripLegacyFormattingCodes("AB"), is("AB"));
        MatcherAssert.assertThat(Util.stripLegacyFormattingCodes("Hello, world!"), is("Hello, world!"));
        MatcherAssert.assertThat(Util.stripLegacyFormattingCodes("§2Hello, world!"), is("Hello, world!"));
        MatcherAssert.assertThat(Util.stripLegacyFormattingCodes("§2§lHello, world!"), is("Hello, world!"));
        MatcherAssert.assertThat(Util.stripLegacyFormattingCodes("§2§lHello, §rworld!"), is("Hello, world!"));
        MatcherAssert.assertThat(Util.stripLegacyFormattingCodes("§2§lHello, §rworld!§4"), is("Hello, world!"));
        MatcherAssert.assertThat(Util.stripLegacyFormattingCodes("§2§lHello, §rworld!§4§"), is("Hello, world!§"));
    }

    @Test
    @DisplayName("substituteVariables() correct")
    public void substituteVariablesCorrect() {
        assertThrows(NullPointerException.class, () -> Util.substituteVariables(null, null));
        assertThrows(NullPointerException.class, () -> Util.substituteVariables("", null));
        assertThrows(NullPointerException.class, () -> Util.substituteVariables(null, new HashMap<>()));

        final LinkedHashMap<Pair<Integer, String>, String> empty = new LinkedHashMap<Pair<Integer, String>, String>(){{
            put(new Pair<>(0, ""), "");
        }};
        MatcherAssert.assertThat(Util.substituteVariables("", new HashMap<>()), is(empty));

        final LinkedHashMap<Pair<Integer,String>, String> noVars = new LinkedHashMap<Pair<Integer,String>, String>(){{
            put(new Pair<>(0, "Hello, world! How are you today?"), "Hello, world! How are you today?");
        }};
        MatcherAssert.assertThat(Util.substituteVariables("Hello, world! How are you today?", new HashMap<>()), is(noVars));

        final LinkedHashMap<Pair<Integer,String>, String> oneVar = new LinkedHashMap<Pair<Integer,String>, String>(){{
            put(new Pair<>(0, "Hello, "), "Hello, ");
            put(new Pair<>(1, "[target]"), "world");
            put(new Pair<>(2, "! How are you today?"), "! How are you today?");
        }};
        final HashMap<String, String> oneVarMap = new HashMap<String, String>() {{
            put("[target]", "world");
        }};
        MatcherAssert.assertThat(Util.substituteVariables("Hello, [target]! How are you today?", oneVarMap), is(oneVar));

        final LinkedHashMap<Pair<Integer,String>, String> manyVars = new LinkedHashMap<Pair<Integer,String>, String>(){{
            put(new Pair<>(0, "[title]"), "[MOD]");
            put(new Pair<>(1, "[killed]"), "zizmax");
            put(new Pair<>(2, " was slain by "), " was slain by ");
            put(new Pair<>(3, "[killer]"), "dizigma");
            put(new Pair<>(4, " using "), " using ");
            put(new Pair<>(5, "[item]"), "Sorcerous Blade of Desires");
        }};
        final HashMap<String, String> manyVarsMap = new HashMap<String, String>() {{
            put("[title]", "[MOD]");
            put("[killed]", "zizmax");
            put("[killer]", "dizigma");
            put("[item]", "Sorcerous Blade of Desires");
        }};
        MatcherAssert.assertThat(Util.substituteVariables("[title][killed] was slain by [killer] using [item]", manyVarsMap), is(manyVars));
    }

    @Test
    @DisplayName("stream() correct")
    public void optionalStreamCorrect() {
        //noinspection OptionalAssignedToNull
        assertThrows(NullPointerException.class, () -> Util.stream(null));
        assertThat(Util.stream(Optional.empty()).count(), is(0L));
        assertThat(Util.stream(Optional.of("hello")).collect(Collectors.toList()), contains("hello"));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    @DisplayName("mapMap() correct")
    public void mapMapCorrect() {
        final Map<String, String> testMap = new HashMap<>();
        testMap.put("foo", "bar");
        testMap.put("hello", "world");

        assertThrows(NullPointerException.class, () -> Util.mapMap(null, null));
        assertThrows(NullPointerException.class, () -> Util.mapMap(null, x -> x));
        assertThrows(NullPointerException.class, () -> Util.mapMap(testMap, null));

        assertThat(Util.mapMap(testMap, x -> x), is(testMap));

        final Map<String, Integer> lengthMap = Util.mapMap(testMap, String::length);
        assertThat(lengthMap.values(), containsInAnyOrder(3, 5));
    }

}
