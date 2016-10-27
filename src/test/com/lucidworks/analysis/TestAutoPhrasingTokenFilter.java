package com.lucidworks.analysis;

import com.carrotsearch.ant.tasks.junit4.dependencies.com.google.common.collect.Sets;
import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.OffsetAttribute;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.apache.lucene.analysis.BaseTokenStreamTestCase.assertAnalyzesTo;
import static org.junit.Assert.assertEquals;

public class TestAutoPhrasingTokenFilter {

    @Test
    public void testAutoPhrase() throws Exception {
        final CharArraySet phraseSets = new CharArraySet(Arrays.asList(
                "income tax", "tax refund", "property tax"), false);

        final String input = "what is my income tax";


        Analyzer analyzer = getAnalyzerForTesting(phraseSets, false);

        assertAnalyzesTo(analyzer, input,
                new String[]{"what", "is", "my", "income_tax"});


    }

    private Analyzer getAnalyzerForTesting(CharArraySet phraseSets, final boolean emitSingleTokens) {

        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String fieldName) {
                Tokenizer tokenizer = new StandardTokenizer();
                TokenFilter filters = new LowerCaseFilter(tokenizer);
                final AutoPhrasingTokenFilter autoPhrasingTokenFilter = new AutoPhrasingTokenFilterBuilder().setInput(filters).setPhraseSet(phraseSets).setEmitSingleTokens(emitSingleTokens)
                        .setReplaceWhitespaceWith('_').createAutoPhrasingTokenFilter();
                autoPhrasingTokenFilter.setReplaceWhitespaceWith(new Character('_'));
                filters = autoPhrasingTokenFilter;
                return new TokenStreamComponents(tokenizer, filters);
            }

        };
    }

    //TODO: Does not work.
    public void testAutoPhraseEmitSingle() throws Exception {
        final CharArraySet phraseSets = new CharArraySet(Arrays.asList(
                "income tax", "tax refund", "property tax"), false);

        final String input = "what is my income tax refund this year now that my property tax is so high";


        Analyzer analyzer = getAnalyzerForTesting(phraseSets, false);

        assertAnalyzesTo(analyzer, input,
                new String[]{"what", "is", "my", "income", "income_tax", "tax", "tax_refund"});
    }

    @Test
    public void testOverlappingAtBeginning() throws Exception {
        final CharArraySet phraseSets = new CharArraySet(Arrays.asList(
                "new york", "new york city", "city of new york"), false);

        final String input = "new york city is great";


        final Analyzer analyzerForTesting = getAnalyzerForTesting(phraseSets, false);
        assertAnalyzesTo(analyzerForTesting, input,
                new String[]{"new_york_city", "is", "great"});


    }

    @Test
    public void testOverlappingAtEnd() throws Exception {
        final CharArraySet phraseSets = new CharArraySet(Arrays.asList(
                "new york", "new york city", "city of new york"), false);

        final String input = "the great city of new york";

        TokenStream tokenStream = getAnalyzerForTesting(phraseSets, false).tokenStream("somefield", input);
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        tokenStream.reset();
        Set<String> tokens = Sets.newHashSet();
        while (tokenStream.incrementToken()) {
            String term = charTermAttribute.toString();
            tokens.add(term);
        }

        final HashSet actual = Sets.newHashSet();
        actual.add("the");
        actual.add("great");
        actual.add("city_of_new_york");
        assertEquals("Should have matching tokens", tokens, actual);

    }

    @Test
    public void testIncompletePhrase() throws Exception {

        final CharArraySet phraseSets = new CharArraySet(Arrays.asList(
                "new york city", "city of new york"), false);

        final String input = "some where in new york";

        TokenStream tokenStream = getAnalyzerForTesting(phraseSets, false).tokenStream("somefield", input);
        OffsetAttribute offsetAttribute = tokenStream.addAttribute(OffsetAttribute.class);
        CharTermAttribute charTermAttribute = tokenStream.addAttribute(CharTermAttribute.class);

        tokenStream.reset();
        Set<String> tokens = Sets.newHashSet();
        while (tokenStream.incrementToken()) {
            String term = charTermAttribute.toString();
            tokens.add(term);
        }

        final HashSet actual = Sets.newHashSet();
        actual.add("some");
        actual.add("where");
        actual.add("in");
        actual.add("new");
        actual.add("york");
        assertEquals("Should have matching tokens", tokens, actual);

    }

}
