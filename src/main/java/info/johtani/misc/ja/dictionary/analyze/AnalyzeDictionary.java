
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

package info.johtani.misc.ja.dictionary.analyze;

import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class AnalyzeDictionary {

    private static int LEFT_ID_COL = 1;
    private static int RIGHT_ID_COL = 2;
    // has an issue. e.g. アンプラグド - アンプラグド-unplugged
    //private static int BASEFORM_COL = 11;
    private static int BASEFORM_COL = 14;
    private static int SURFACE_READING_COL = 13;

    public static void main(String[] args) {
        // ipadic
        //String dictionaryPath = "/Users/johtani/tmp/dictionary/mecab-ipadic-2.7.0-20070801";
        // UniDic 2.1.2
        String dictionaryPath = "/Users/johtani/tmp/dictionary/unidic-mecab-2.1.2_src";
        // UniDic 2.3.0
        //String dictionaryPath = "/Users/johtani/tmp/dictionary/unidic-cwj-2.3.0";
        CsvParserSettings settings = new CsvParserSettings();
        CsvFormat format = new CsvFormat();
        format.setComment('\0');
        settings.setFormat(format);
        CsvParser parser = new CsvParser(settings);

        try (DirectoryStream<Path> ds = Files.newDirectoryStream(Paths.get(dictionaryPath), "*.csv")){
            for (Path file : ds) {
                checkFile(parser, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Finished!");
    }

    private static void checkFile(CsvParser parser, Path file) throws IOException {
        System.out.println("File: ["+file.getFileName()+"]");
        // TODO change if ipadic
        List<String> lines = Files.readAllLines(file);
        System.out.println(lines.size());
        int count = 0;
        try {
            for (String line : lines) {
                count ++;
                String[] values = parser.parseLine(line);

                if (mismatchLeftRightCost(values[LEFT_ID_COL], values[RIGHT_ID_COL])) {
                    System.err.println("01.["+values[0]+"] has LEFT_COST_ID["+values[LEFT_ID_COL]+"] != RIGHT_COST_ID["+values[RIGHT_ID_COL]+"]");
                    System.err.println(line);
                }
                if (overflowLeftCostId(values[LEFT_ID_COL])) {
                    System.err.println("02.["+values[0]+"] has LEFT_COST_ID["+values[LEFT_ID_COL]+"] >= 8192");
                    System.err.println(line);
                }
                if (overflowBasicFormLength(values[BASEFORM_COL], values[0])) {
                    System.err.println("03.["+values[0]+"] has long baseform ["+values[BASEFORM_COL]+"] and length["+values[BASEFORM_COL].length()+"]");
                    System.err.println(line);
                }
                if (nonExistReading(values[SURFACE_READING_COL])) {
                    System.err.println("04.["+values[0]+"] has empty reading. base form["+values[BASEFORM_COL]+"]");
                    System.err.println(line);
                }
//                System.out.println(count);
            }
        } catch (NullPointerException npe) {
            System.out.println(lines.get(count));
            throw npe;
        }
    }

    private static boolean mismatchLeftRightCost(String left, String right) {
        return left.compareTo(right) != 0;
    }

    private static boolean overflowLeftCostId(String left) {
        int id = Integer.parseInt(left);
        return id >= 8192;
    }

    private static boolean overflowBasicFormLength(String baseForm, String surface) {
        if (!("*".equals(baseForm) || baseForm.equals(surface))) {
            return baseForm.length() >= 16;
        } else {
            return false;
        }
    }

    private static boolean nonExistReading(String surfaceReading) {
        //return surfaceReading == null || surfaceReading.isEmpty();
        //Lucene uses surface form if surface reading doesn't exist
        return false;
    }
}
