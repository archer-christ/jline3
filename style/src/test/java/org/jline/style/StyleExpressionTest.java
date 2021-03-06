/*
 * Copyright (c) 2002-2018, the original author or authors.
 *
 * This software is distributable under the BSD license. See the terms of the
 * BSD license in the documentation provided with this software.
 *
 * http://www.opensource.org/licenses/bsd-license.php
 */
package org.jline.style;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.junit.Before;
import org.junit.Test;

import static org.jline.utils.AttributedStyle.*;

/**
 * Tests for {@link StyleExpression}.
 */
public class StyleExpressionTest extends StyleTestSupport {

    private StyleExpression underTest;

    @Before
    public void setUp() {
        super.setUp();
        this.underTest = new StyleExpression(new StyleResolver(source, "test"));
    }

    @Test
    public void evaluateExpressionWithPrefixAndSuffix() {
        AttributedString result = underTest.evaluate("foo @{bold bar} baz");
        DefaultGroovyMethods.println(this, result.toAnsi());
        assert result.equals(new AttributedStringBuilder().append("foo ").append("bar", BOLD).append(" baz").toAttributedString());
    }

    @Test
    public void evaluateExpressionWithPrefix() {
        AttributedString result = underTest.evaluate("foo @{bold bar}");
        DefaultGroovyMethods.println(this, result.toAnsi());
        assert result.equals(new AttributedStringBuilder().append("foo ").append("bar", BOLD).toAttributedString());
    }

    @Test
    public void evaluateExpressionWithSuffix() {
        AttributedString result = underTest.evaluate("@{bold foo} bar");
        DefaultGroovyMethods.println(this, result.toAnsi());
        assert result.equals(new AttributedStringBuilder().append("foo", BOLD).append(" bar").toAttributedString());
    }

    @Test
    public void evaluateExpression() {
        AttributedString result = underTest.evaluate("@{bold foo}");
        DefaultGroovyMethods.println(this, result.toAnsi());
        assert result.equals(new AttributedString("foo", BOLD));
    }

    @Test
    public void evaluateExpressionWithDefault()

    {
        AttributedString result = underTest.evaluate("@{.foo:-bold foo}");
        DefaultGroovyMethods.println(this, result.toAnsi());
        assert result.equals(new AttributedString("foo", BOLD));
    }

    @Test
    public void evaluateExpressionWithMultipleReplacements() {
        AttributedString result = underTest.evaluate("@{bold foo} @{fg:red bar} @{underline baz}");
        DefaultGroovyMethods.println(this, result.toAnsi());
        assert result.equals(new AttributedStringBuilder().append("foo", BOLD).append(" ").append("bar", DEFAULT.foreground(RED)).append(" ").append("baz", DEFAULT.underline()).toAttributedString());
    }

    @Test
    public void evaluateExpressionWithRecursiveReplacements() {
        AttributedString result = underTest.evaluate("@{underline foo @{fg:cyan bar}}");
        DefaultGroovyMethods.println(this, result.toAnsi());
        assert result.equals(new AttributedStringBuilder().append("foo ", DEFAULT.underline()).append("bar", DEFAULT.underline().foreground(CYAN)).toAttributedString());
    }

    @Test
    public void evaluateExpressionMissingVvalue() {
        AttributedString result = underTest.evaluate("@{bold}");
        DefaultGroovyMethods.println(this, result.toAnsi());
        assert result.equals(new AttributedString("@{bold}", DEFAULT));
    }

    @Test
    public void evaluateExpressionMissingTokens() {
        AttributedString result = underTest.evaluate("foo");
        DefaultGroovyMethods.println(this, result.toAnsi());
        assert result.equals(new AttributedString("foo", DEFAULT));
    }

    @Test
    public void evaluateExpressionWithPlaceholderValue() {
        AttributedString result = underTest.evaluate("@{bold,fg:cyan ${foo\\}}");
        DefaultGroovyMethods.println(this, result.toAnsi());
        assert result.equals(new AttributedString("${foo}", DEFAULT.bold().foreground(CYAN)));
    }

    @Test
    public void evaluateWithStyleReference() {
        source.set("test", "very-red", "bold,fg:red");
        AttributedString string = underTest.evaluate("@{.very-red foo bar}");
        assert string.equals(new AttributedString("foo bar", BOLD.foreground(RED)));
    }

}
