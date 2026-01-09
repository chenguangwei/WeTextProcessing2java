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
 * WetextProcessor - Java wrapper for WeTextProcessing C++ library.
 *
 * This class provides Text Normalization (TN) and Inverse Text Normalization (ITN)
 * functionality through JNI calls to the native C++ implementation.
 *
 * <p>Usage example:
 * <pre>{@code
 * try (WetextProcessor processor = new WetextProcessor(
 *         "path/to/zh_tn_tagger.fst",
 *         "path/to/zh_tn_verbalizer.fst")) {
 *     String result = processor.normalize("2.5平方电线");
 *     System.out.println(result);  // Output: 二点五平方电线
 * }
 * }</pre>
 *
 * <p>Thread Safety: Each WetextProcessor instance is NOT thread-safe.
 * Create separate instances for concurrent usage, or synchronize access externally.
 */
public class WetextProcessor implements AutoCloseable {

    // Load native library
    static {
        try {
            System.loadLibrary("wetext_jni");
        } catch (UnsatisfiedLinkError e) {
            System.err.println("Failed to load wetext_jni library. " +
                "Make sure libwetext_jni.so (Linux), libwetext_jni.dylib (macOS), " +
                "or wetext_jni.dll (Windows) is in java.library.path");
            throw e;
        }
    }

    // Native handle to the C++ Processor object
    private long nativeHandle;

    // Track if this instance has been closed
    private volatile boolean closed = false;

    /**
     * Creates a new WetextProcessor with the specified FST model files.
     *
     * @param taggerPath     Path to the tagger FST file (e.g., zh_tn_tagger.fst)
     * @param verbalizerPath Path to the verbalizer FST file (e.g., zh_tn_verbalizer.fst)
     * @throws IllegalArgumentException if paths are null or empty
     * @throws RuntimeException         if failed to create the native processor
     */
    public WetextProcessor(String taggerPath, String verbalizerPath) {
        if (taggerPath == null || taggerPath.isEmpty()) {
            throw new IllegalArgumentException("taggerPath cannot be null or empty");
        }
        if (verbalizerPath == null || verbalizerPath.isEmpty()) {
            throw new IllegalArgumentException("verbalizerPath cannot be null or empty");
        }

        this.nativeHandle = nativeCreateProcessor(taggerPath, verbalizerPath);
        if (this.nativeHandle == 0) {
            throw new RuntimeException("Failed to create WetextProcessor. " +
                "Please check if the FST files exist and are valid.");
        }
    }

    /**
     * Performs text normalization on the input string.
     * This is a convenience method that combines tag() and verbalize().
     *
     * @param input The text to normalize
     * @return The normalized text
     * @throws IllegalStateException if the processor has been closed
     * @throws IllegalArgumentException if input is null
     */
    public String normalize(String input) {
        checkState();
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null");
        }
        return nativeNormalize(nativeHandle, input);
    }

    /**
     * Tags the input text with structured annotations.
     * This is the first step of the normalization pipeline.
     *
     * @param input The text to tag
     * @return The tagged text with structured annotations
     * @throws IllegalStateException if the processor has been closed
     * @throws IllegalArgumentException if input is null
     */
    public String tag(String input) {
        checkState();
        if (input == null) {
            throw new IllegalArgumentException("input cannot be null");
        }
        return nativeTag(nativeHandle, input);
    }

    /**
     * Verbalizes the tagged text to produce the final normalized output.
     * This is the second step of the normalization pipeline.
     *
     * @param taggedInput The tagged text from tag() method
     * @return The verbalized (normalized) text
     * @throws IllegalStateException if the processor has been closed
     * @throws IllegalArgumentException if taggedInput is null
     */
    public String verbalize(String taggedInput) {
        checkState();
        if (taggedInput == null) {
            throw new IllegalArgumentException("taggedInput cannot be null");
        }
        return nativeVerbalize(nativeHandle, taggedInput);
    }

    /**
     * Checks if this processor instance is still valid (not closed).
     *
     * @return true if the processor is valid and can be used
     */
    public boolean isValid() {
        return !closed && nativeHandle != 0;
    }

    /**
     * Releases native resources. After calling this method, the processor
     * cannot be used anymore.
     */
    @Override
    public void close() {
        if (!closed && nativeHandle != 0) {
            nativeDestroyProcessor(nativeHandle);
            nativeHandle = 0;
            closed = true;
        }
    }

    /**
     * Ensures native resources are released when garbage collected.
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            close();
        } finally {
            super.finalize();
        }
    }

    private void checkState() {
        if (closed) {
            throw new IllegalStateException("WetextProcessor has been closed");
        }
        if (nativeHandle == 0) {
            throw new IllegalStateException("WetextProcessor is not properly initialized");
        }
    }

    // Native method declarations
    private static native long nativeCreateProcessor(String taggerPath, String verbalizerPath);
    private static native void nativeDestroyProcessor(long handle);
    private static native String nativeNormalize(long handle, String input);
    private static native String nativeTag(long handle, String input);
    private static native String nativeVerbalize(long handle, String input);
}
