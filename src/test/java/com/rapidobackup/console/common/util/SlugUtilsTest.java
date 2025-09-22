package com.rapidobackup.console.common.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Set;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for SlugUtils.
 *
 * Tests cover:
 * - Basic slug generation from various text inputs
 * - Unicode and accent handling
 * - Unique slug generation with collision handling
 * - Slug validation
 * - Edge cases and special characters
 */
class SlugUtilsTest {

    @Test
    void generateSlug_basicText_returnsExpectedSlug() {
        assertThat(SlugUtils.generateSlug("Hello World")).isEqualTo("hello-world");
        assertThat(SlugUtils.generateSlug("My Company Name")).isEqualTo("my-company-name");
        assertThat(SlugUtils.generateSlug("Test123")).isEqualTo("test123");
    }

    @ParameterizedTest
    @CsvSource({
        "'', ''",
        "'   ', ''",
        "'Hello World!', 'hello-world'",
        "'Company & Associates', 'company-associates'",
        "'Test@Company.com', 'test-company-com'",
        "'Multiple   Spaces', 'multiple-spaces'",
        "'UPPERCASE TEXT', 'uppercase-text'",
        "'mixed-Case_Text', 'mixed-case-text'",
        "'123 Numeric Start', '123-numeric-start'",
        "'End with Number 456', 'end-with-number-456'"
    })
    void generateSlug_variousInputs_returnsExpectedResults(String input, String expected) {
        assertThat(SlugUtils.generateSlug(input)).isEqualTo(expected);
    }

    @Test
    void generateSlug_unicodeAndAccents_removesAccentsCorrectly() {
        assertThat(SlugUtils.generateSlug("Café & Résumé")).isEqualTo("cafe-resume");
        assertThat(SlugUtils.generateSlug("Naïve Corporation")).isEqualTo("naive-corporation");
        assertThat(SlugUtils.generateSlug("München Office")).isEqualTo("munchen-office");
        assertThat(SlugUtils.generateSlug("São Paulo Branch")).isEqualTo("sao-paulo-branch");
        assertThat(SlugUtils.generateSlug("Français & Español")).isEqualTo("francais-espanol");
    }

    @Test
    void generateSlug_specialCharacters_handlesCorrectly() {
        assertThat(SlugUtils.generateSlug("Company (Subsidiary)")).isEqualTo("company-subsidiary");
        assertThat(SlugUtils.generateSlug("Profit & Loss Statement")).isEqualTo("profit-loss-statement");
        assertThat(SlugUtils.generateSlug("Version 2.0.1")).isEqualTo("version-2-0-1");
        assertThat(SlugUtils.generateSlug("100% Digital")).isEqualTo("100-digital");
        assertThat(SlugUtils.generateSlug("#1 Company")).isEqualTo("1-company");
    }

    @Test
    void generateSlug_multipleHyphens_consolidatesToSingle() {
        assertThat(SlugUtils.generateSlug("Test---Multiple---Hyphens")).isEqualTo("test-multiple-hyphens");
        assertThat(SlugUtils.generateSlug("Start---   ---End")).isEqualTo("start-end");
        assertThat(SlugUtils.generateSlug("A & B & C")).isEqualTo("a-b-c");
    }

    @Test
    void generateSlug_leadingTrailingChars_trimsCorrectly() {
        assertThat(SlugUtils.generateSlug("-Leading Hyphen")).isEqualTo("leading-hyphen");
        assertThat(SlugUtils.generateSlug("Trailing Hyphen-")).isEqualTo("trailing-hyphen");
        assertThat(SlugUtils.generateSlug("-Both-")).isEqualTo("both");
        assertThat(SlugUtils.generateSlug("   Spaces Around   ")).isEqualTo("spaces-around");
    }

    @Test
    void generateSlug_nullAndEmpty_returnsEmpty() {
        assertThat(SlugUtils.generateSlug(null)).isEmpty();
        assertThat(SlugUtils.generateSlug("")).isEmpty();
        assertThat(SlugUtils.generateSlug("   ")).isEmpty();
        assertThat(SlugUtils.generateSlug("\t\n")).isEmpty();
    }

    @Test
    void generateUniqueSlug_noCollisions_returnsBaseSlug() {
        Predicate<String> neverExists = slug -> false;

        assertThat(SlugUtils.generateUniqueSlug("Test Company", neverExists))
            .isEqualTo("test-company");
    }

    @Test
    void generateUniqueSlug_withCollisions_addsCounter() {
        Set<String> existingSlugs = Set.of("test-company", "test-company-1", "test-company-2");
        Predicate<String> existsChecker = existingSlugs::contains;

        assertThat(SlugUtils.generateUniqueSlug("Test Company", existsChecker))
            .isEqualTo("test-company-3");
    }

    @Test
    void generateUniqueSlug_emptyInput_usesUntitled() {
        Set<String> existingSlugs = Set.of("untitled");
        Predicate<String> existsChecker = existingSlugs::contains;

        assertThat(SlugUtils.generateUniqueSlug("", existsChecker))
            .isEqualTo("untitled-1");
        assertThat(SlugUtils.generateUniqueSlug(null, existsChecker))
            .isEqualTo("untitled-1");
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "hello-world",
        "test123",
        "company-name",
        "a",
        "test-company-1",
        "version-2-0-1"
    })
    void isValidSlug_validSlugs_returnsTrue(String slug) {
        assertThat(SlugUtils.isValidSlug(slug)).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "",
        "   ",
        "-invalid",
        "invalid-",
        "Invalid-Case",
        "with space",
        "with--double-hyphen",
        "with_underscore",
        "with.dot",
        "with@symbol",
        "with/slash"
    })
    void isValidSlug_invalidSlugs_returnsFalse(String slug) {
        assertThat(SlugUtils.isValidSlug(slug)).isFalse();
    }

    @Test
    void isValidSlug_nullInput_returnsFalse() {
        assertThat(SlugUtils.isValidSlug(null)).isFalse();
    }

    @Test
    void sanitizeSlug_invalidSlug_returnsValidSlug() {
        assertThat(SlugUtils.sanitizeSlug("Invalid Slug!")).isEqualTo("invalid-slug");
        assertThat(SlugUtils.sanitizeSlug("-bad-slug-")).isEqualTo("bad-slug");
        assertThat(SlugUtils.sanitizeSlug("UPPERCASE")).isEqualTo("uppercase");
    }

    @Test
    void generateSlug_realWorldExamples_handlesCorrectly() {
        // Real-world company names and scenarios
        assertThat(SlugUtils.generateSlug("Apple Inc.")).isEqualTo("apple-inc");
        assertThat(SlugUtils.generateSlug("AT&T Corporation")).isEqualTo("at-t-corporation");
        assertThat(SlugUtils.generateSlug("3M Company")).isEqualTo("3m-company");
        assertThat(SlugUtils.generateSlug("Johnson & Johnson")).isEqualTo("johnson-johnson");
        assertThat(SlugUtils.generateSlug("Procter & Gamble")).isEqualTo("procter-gamble");
        assertThat(SlugUtils.generateSlug("PricewaterhouseCoopers")).isEqualTo("pricewaterhousecoopers");

        // Department names
        assertThat(SlugUtils.generateSlug("Human Resources")).isEqualTo("human-resources");
        assertThat(SlugUtils.generateSlug("IT & Technology")).isEqualTo("it-technology");
        assertThat(SlugUtils.generateSlug("Sales & Marketing")).isEqualTo("sales-marketing");

        // International examples
        assertThat(SlugUtils.generateSlug("Société Générale")).isEqualTo("societe-generale");
        assertThat(SlugUtils.generateSlug("Nestlé S.A.")).isEqualTo("nestle-s-a");
    }

    @Test
    void generateSlug_performanceTest_handlesLargeInput() {
        String largeInput = "A".repeat(1000) + " " + "B".repeat(1000);

        long startTime = System.currentTimeMillis();
        String result = SlugUtils.generateSlug(largeInput);
        long endTime = System.currentTimeMillis();

        assertThat(result).isEqualTo("a".repeat(1000) + "-" + "b".repeat(1000));
        assertThat(endTime - startTime).isLessThan(500); // Should be fast even on slower environments
    }
}