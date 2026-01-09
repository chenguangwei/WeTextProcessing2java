// Copyright (c) 2024 WeTextProcessing Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.wetext;

/**
 * Example usage of WetextProcessor for Text Normalization (TN) and
 * Inverse Text Normalization (ITN).
 *
 * Run with:
 *   java -Djava.library.path=/path/to/lib -cp . com.wetext.Example \
 *       /path/to/tagger.fst /path/to/verbalizer.fst
 */
public class Example {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("WeTextProcessing Java Example");
            System.out.println("==============================");
            System.out.println();
            System.out.println("Usage: java -Djava.library.path=<lib_path> -cp <classpath> " +
                "com.wetext.Example <tagger.fst> <verbalizer.fst> [text]");
            System.out.println();
            System.out.println("Arguments:");
            System.out.println("  tagger.fst      Path to tagger FST file");
            System.out.println("  verbalizer.fst  Path to verbalizer FST file");
            System.out.println("  text            Optional text to normalize (default: demo texts)");
            System.out.println();
            System.out.println("Example for Chinese TN:");
            System.out.println("  java -Djava.library.path=./build/lib -cp ./build/classes \\");
            System.out.println("      com.wetext.Example \\");
            System.out.println("      /path/to/tn/zh_tn_tagger.fst \\");
            System.out.println("      /path/to/tn/zh_tn_verbalizer.fst \\");
            System.out.println("      \"2.5平方电线\"");
            System.out.println();
            System.out.println("Example for Chinese ITN:");
            System.out.println("  java -Djava.library.path=./build/lib -cp ./build/classes \\");
            System.out.println("      com.wetext.Example \\");
            System.out.println("      /path/to/itn/zh_itn_tagger.fst \\");
            System.out.println("      /path/to/itn/zh_itn_verbalizer.fst \\");
            System.out.println("      \"二点五平方电线\"");
            return;
        }

        String taggerPath = args[0];
        String verbalizerPath = args[1];

        System.out.println("=".repeat(60));
        System.out.println("WeTextProcessing Java Example");
        System.out.println("=".repeat(60));
        System.out.println();
        System.out.println("Tagger FST:     " + taggerPath);
        System.out.println("Verbalizer FST: " + verbalizerPath);
        System.out.println();

        // Create processor using try-with-resources for automatic cleanup
        try (WetextProcessor processor = new WetextProcessor(taggerPath, verbalizerPath)) {
            System.out.println("[OK] Processor created successfully!");
            System.out.println();

            if (args.length >= 3) {
                // User provided custom text
                String inputText = args[2];
                processText(processor, inputText);
            } else {
                // Demo with sample texts
                runDemo(processor, taggerPath);
            }

        } catch (Exception e) {
            System.err.println("[ERROR] " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    private static void processText(WetextProcessor processor, String input) {
        System.out.println("-".repeat(60));
        System.out.println("Processing: \"" + input + "\"");
        System.out.println("-".repeat(60));

        // Step 1: Tag
        String tagged = processor.tag(input);
        System.out.println("Tagged:     " + tagged);

        // Step 2: Verbalize
        String verbalized = processor.verbalize(tagged);
        System.out.println("Verbalized: " + verbalized);

        // Or use normalize() which combines both steps
        String normalized = processor.normalize(input);
        System.out.println("Normalized: " + normalized);
        System.out.println();
    }

    private static void runDemo(WetextProcessor processor, String taggerPath) {
        System.out.println("Running demo with sample texts...");
        System.out.println();

        // Detect if this is TN or ITN based on tagger path
        boolean isTN = taggerPath.contains("_tn_");
        boolean isITN = taggerPath.contains("_itn_");
        boolean isChinese = taggerPath.contains("zh_");
        boolean isEnglish = taggerPath.contains("en_");
        boolean isJapanese = taggerPath.contains("ja_");

        String[] testTexts;

        if (isChinese && isTN) {
            // Chinese Text Normalization samples
            testTexts = new String[] {
                "2.5平方电线",
                "今天是2024年1月15日",
                "价格是￥199.99元",
                "电话号码是13812345678",
                "比分是3:2"
            };
        } else if (isChinese && isITN) {
            // Chinese Inverse Text Normalization samples
            testTexts = new String[] {
                "二点五平方电线",
                "今天是二零二四年一月十五日",
                "价格是一百九十九点九九元",
                "电话号码是一三八一二三四五六七八"
            };
        } else if (isEnglish && isTN) {
            // English Text Normalization samples
            testTexts = new String[] {
                "The price is $199.99",
                "Today is January 15, 2024",
                "Call me at 123-456-7890",
                "The ratio is 3:2"
            };
        } else if (isJapanese) {
            // Japanese samples
            testTexts = new String[] {
                "価格は1999円です",
                "今日は2024年1月15日です"
            };
        } else {
            // Generic samples
            testTexts = new String[] {
                "123",
                "45.67",
                "2024-01-15"
            };
        }

        for (String text : testTexts) {
            processText(processor, text);
        }

        // Performance test
        System.out.println("=".repeat(60));
        System.out.println("Performance Test");
        System.out.println("=".repeat(60));

        String testText = testTexts[0];
        int iterations = 1000;

        long startTime = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            processor.normalize(testText);
        }
        long endTime = System.nanoTime();

        double totalMs = (endTime - startTime) / 1_000_000.0;
        double avgUs = (endTime - startTime) / 1_000.0 / iterations;

        System.out.println("Text: \"" + testText + "\"");
        System.out.println("Iterations: " + iterations);
        System.out.println("Total time: " + String.format("%.2f", totalMs) + " ms");
        System.out.println("Average time: " + String.format("%.2f", avgUs) + " us per call");
        System.out.println();
    }
}
