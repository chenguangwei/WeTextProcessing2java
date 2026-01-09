# WeTextProcessing Java JNI Bindings

Java bindings for WeTextProcessing using JNI (Java Native Interface).

## Prerequisites

- Java JDK 8 or higher
- CMake 3.14+
- C++14 compatible compiler (GCC, Clang, MSVC)

## Quick Start

### 1. Generate FST Models (if not already done)

```bash
cd /path/to/WeTextProcessing

# Generate Chinese TN models
python -m tn --text "测试" --overwrite_cache

# Generate Chinese ITN models
python -m itn --text "测试" --overwrite_cache
```

### 2. Build

```bash
cd runtime/java

# Build everything (native + Java)
./build.sh

# Or build with CMake directly
cmake -B build -S ../.. -DBUILD_JNI=ON -DCMAKE_BUILD_TYPE=Release
cmake --build build
```

### 3. Run Example

```bash
# Run Chinese TN example
./build.sh run tn

# Run Chinese ITN example
./build.sh run itn

# Run with custom text
./build.sh run tn "2.5平方电线"
./build.sh run itn "二点五平方电线"
```

## Java API Usage

```java
import com.wetext.WetextProcessor;

public class MyApp {
    public static void main(String[] args) {
        // Use try-with-resources for automatic cleanup
        try (WetextProcessor processor = new WetextProcessor(
                "/path/to/zh_tn_tagger.fst",
                "/path/to/zh_tn_verbalizer.fst")) {

            // Simple normalization
            String result = processor.normalize("2.5平方电线");
            System.out.println(result);  // Output: 二点五平方电线

            // Or use two-step process
            String tagged = processor.tag("价格是￥199.99");
            String verbalized = processor.verbalize(tagged);
            System.out.println(verbalized);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

## API Reference

### WetextProcessor

| Method | Description |
|--------|-------------|
| `WetextProcessor(String taggerPath, String verbalizerPath)` | Create a new processor with FST model paths |
| `String normalize(String input)` | Normalize text (combines tag + verbalize) |
| `String tag(String input)` | Tag input text with structured annotations |
| `String verbalize(String taggedInput)` | Verbalize tagged text to final output |
| `boolean isValid()` | Check if processor is still valid |
| `void close()` | Release native resources |

## Integration with Maven/Gradle

### Maven

```xml
<dependency>
    <groupId>com.wetext</groupId>
    <artifactId>wetext-processor</artifactId>
    <version>0.1.0</version>
</dependency>
```

Note: You'll need to publish to your local/private Maven repository first.

### Loading Native Library

The native library must be loadable at runtime. Options:

1. **System property**: `-Djava.library.path=/path/to/lib`
2. **Environment variable**: `LD_LIBRARY_PATH` (Linux) or `DYLD_LIBRARY_PATH` (macOS)
3. **Copy to system path**: `/usr/local/lib` or similar

## File Structure

```
runtime/java/
├── CMakeLists.txt              # CMake build for JNI library
├── build.sh                    # Build and run script
├── README.md                   # This file
└── src/main/
    ├── cpp/
    │   └── wetext_jni.cc       # JNI bridge implementation
    └── java/com/wetext/
        ├── WetextProcessor.java # Java wrapper class
        └── Example.java         # Usage example
```

## Troubleshooting

### UnsatisfiedLinkError

If you see `UnsatisfiedLinkError`, ensure:

1. Native library is in `java.library.path`
2. All dependent libraries (libwetext_processor_c, libfst) are also loadable
3. Library architecture matches JVM (both 64-bit or both 32-bit)

### FST File Not Found

Generate FST files first:

```bash
python -m tn --text "test" --overwrite_cache
python -m itn --text "test" --overwrite_cache
```

FST files will be created in:
- `tn/zh_tn_tagger.fst`, `tn/zh_tn_verbalizer.fst`
- `itn/zh_itn_tagger.fst`, `itn/zh_itn_verbalizer.fst`
