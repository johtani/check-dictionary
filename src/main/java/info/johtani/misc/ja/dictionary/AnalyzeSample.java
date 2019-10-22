/*
 * MIT License
 *
 * Copyright (c) 2019 Jun Ohtani
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package info.johtani.misc.ja.dictionary;

import info.johtani.misc.ja.dictionary.compare.ComponentContainer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.util.TokenizerFactory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import static java.lang.System.exit;

/**
 * Analyze with Lucene analyzer
 */
public class AnalyzeSample {

    private static String TOKENIZER_FACTORY_NAME = "org.apache.lucene.analysis.ja.JapaneseTokenizerFactory";
    private static Map<String,String> FACTORY_ARGS = new HashMap<>();
    private static Map<String,String> versionArgOnly() {
        return new HashMap<String, String>() {{
            put("luceneMatchVersion", Version.LATEST.toString());
        }};
    }

    private static Analyzer createAnalyzer(String jarPath) throws ClassNotFoundException {
        ComponentContainer ipadicContainer = new ComponentContainer(new File[]{new File(jarPath)});
        TokenizerFactory tokenizerFactory = (TokenizerFactory) ipadicContainer.createComponent(
                TOKENIZER_FACTORY_NAME, new Class[]{Map.class}, new Object[]{FACTORY_ARGS});
        return new Analyzer() {
            @Override
            protected TokenStreamComponents createComponents(String field) {
                Tokenizer tokenizer = tokenizerFactory.create();
                return new TokenStreamComponents(tokenizer, tokenizer);
            }

        };
    }

    private static void printTokens(Analyzer analyzer, String target) throws IOException {

        try (TokenStream tokens = analyzer.tokenStream("test", new StringReader(target))){
            tokens.reset();
            CharTermAttribute attr = tokens.getAttribute(CharTermAttribute.class);
            for (int i=0;tokens.incrementToken();i++) {
                System.out.println("token[i] is ["+attr.toString()+"]");
            }
            tokens.end();
            tokens.close();
        }
    }

    public static void main(String[] args) {

        String target = "令和元年も残りはあと2ヶ月ちょっととなりました。";

        // load kuromoji tokenizer from specified path
        String ipadicPath = "./target_jars/compare/lucene-analyzers-kuromoji-ipadic-9.0.0-SNAPSHOT.jar";
        String unidicPath = "./target_jars/compare/lucene-analyzers-kuromoji-unidic-9.0.0-SNAPSHOT.jar";

        try {
            System.out.println("+++ ipadic ++++++++++++++");
            printTokens(createAnalyzer(ipadicPath), target);
            System.out.println("+++ unidic ++++++++++++++");
            printTokens(createAnalyzer(unidicPath), target);
        } catch (ClassNotFoundException cnfe) {
            System.err.println("Class not found!");
            cnfe.printStackTrace();
            exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            exit(0);
        }
    }

}
