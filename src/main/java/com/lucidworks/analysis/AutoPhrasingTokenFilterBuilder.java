package com.lucidworks.analysis;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.TokenStream;

public class AutoPhrasingTokenFilterBuilder {
    private TokenStream input;
    private CharArraySet phraseSet;
    private boolean emitSingleTokens;
    private Character replaceWhitespaceWith;

    public AutoPhrasingTokenFilterBuilder setInput(TokenStream input) {
        this.input = input;
        return this;
    }

    public AutoPhrasingTokenFilterBuilder setPhraseSet(CharArraySet phraseSet) {
        this.phraseSet = phraseSet;
        return this;
    }

    public AutoPhrasingTokenFilterBuilder setEmitSingleTokens(boolean emitSingleTokens) {
        this.emitSingleTokens = emitSingleTokens;
        return this;
    }

    public AutoPhrasingTokenFilterBuilder setReplaceWhitespaceWith(Character replaceWhitespaceWith) {
        this.replaceWhitespaceWith = replaceWhitespaceWith;
        return this;
    }

    public AutoPhrasingTokenFilter createAutoPhrasingTokenFilter() {
        return new AutoPhrasingTokenFilter(input, phraseSet, emitSingleTokens, replaceWhitespaceWith);
    }
}