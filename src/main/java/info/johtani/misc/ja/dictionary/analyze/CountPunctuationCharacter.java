package info.johtani.misc.ja.dictionary.analyze;

import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CountPunctuationCharacter {

    public static void main(String[] args) {
        CountPunctuationCharacter runner = new CountPunctuationCharacter();
        runner.run();
    }

    private CountPunctuationCharacter() {

    }

    private void run() {
        // for ipadic
        String dictionaryPath = "/Users/johtani/tmp/dictionary/mecab-ipadic-2.7.0-20070801";
        String encoding = "euc-jp";
        // for unidic 2.1.2
        //String dictionaryPath = "/Users/johtani/tmp/dictionary/unidic-mecab-2.1.2_src";
        //String encoding = "utf-8";

        CsvParserSettings settings = new CsvParserSettings();
        CsvFormat format = new CsvFormat();
        format.setComment('\0');
        settings.setFormat(format);
        CsvParser parser = new CsvParser(settings);
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(dictionaryPath), "*.csv")){
            for (Path file : ds) {
                checkFile(parser, file, encoding);
            }
            System.out.println("start with punctuation: ["+ this.startWithPunctuation.size() + "]. one char is ["+oneCharInStartWithPunctuation+"] ");
            System.out.println("all punctuations: ["+this.allPunctuations.size()+"]");
            System.out.println("hasPunctuations: ["+this.hasPunctuation.size()+"]");
            System.out.println("+++++++++++++++++ Start with Punctuation +++++++++++++++++++++");
            for (String token : this.startWithPunctuation) {
                //if (token.toCharArray().length > 1) {
                    System.out.println(token);
                //}
            }
            System.out.println("+++++++++++++++++ all Punctuations  +++++++++++++++++++++");
            for (String token: this.allPunctuations) {
                System.out.println(token);
            }
            System.out.println("+++++++++++++++++ Has Punctuation without 1st char +++++++++++++++++++++");
            for (String token: this.hasPunctuation) {
                System.out.println(token);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Finished!");
    }


    private List<String> startWithPunctuation = new ArrayList<>();
    private List<String> allPunctuations = new ArrayList<>();
    private List<String> hasPunctuation = new ArrayList<>();
    private int oneCharInStartWithPunctuation = 0;

    private void checkFile(CsvParser parser, Path file, String encoding) throws IOException {
        System.out.println("File: ["+file.getFileName()+"]");
        Charset cs = Charset.forName(encoding);
        List<String> lines = Files.readAllLines(file, cs);
        //System.out.println(lines.size());
        int count = 0;
        try {
            for (String line : lines) {
                count ++;
                String[] values = parser.parseLine(line);
                String surface = values[0];
                char[] token = surface.toCharArray();
                if (isPunctuation(token[0])) {
                    if (token.length == 1) {
                        oneCharInStartWithPunctuation++;
                        startWithPunctuation.add(surface);
                    } else if (!isAllCharPunctuation(token)) {
                        startWithPunctuation.add(surface);
                    } else {
                        allPunctuations.add(surface);
                    }
                } else if (existPunctuation(token)) {
                    hasPunctuation.add(surface);
                } else {
                    //no-op
                }
            }
        } catch (NullPointerException npe) {
            System.out.println(lines.get(count));
            throw npe;
        }
    }

    private static boolean isPunctuation(char ch) {
        switch(Character.getType(ch)) {
            case Character.SPACE_SEPARATOR:
            case Character.LINE_SEPARATOR:
            case Character.PARAGRAPH_SEPARATOR:
            case Character.CONTROL:
            case Character.FORMAT:
            case Character.DASH_PUNCTUATION:
            case Character.START_PUNCTUATION:
            case Character.END_PUNCTUATION:
            case Character.CONNECTOR_PUNCTUATION:
            case Character.OTHER_PUNCTUATION:
            case Character.MATH_SYMBOL:
            case Character.CURRENCY_SYMBOL:
            case Character.MODIFIER_SYMBOL:
            case Character.OTHER_SYMBOL:
            case Character.INITIAL_QUOTE_PUNCTUATION:
            case Character.FINAL_QUOTE_PUNCTUATION:
                return true;
            default:
                return false;
        }
    }

    private static boolean existPunctuation(char[] ch) {
        boolean flag = false;
        for (int i = 0; i < ch.length; i++) {
            if (isPunctuation(ch[i])) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    private static boolean isAllCharPunctuation(char[] ch) {
        boolean flag = true;
        for (int i = 0; i < ch.length; i++) {
            if (!isPunctuation(ch[i])) {
                flag = false;
                break;
            }
        }
        return flag;
    }

}
