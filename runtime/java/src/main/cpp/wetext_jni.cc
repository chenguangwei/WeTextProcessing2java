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

#include <jni.h>
#include <string>
#include "processor/wetext_processor_c_api.h"

extern "C" {

/*
 * Class:     com_wetext_WetextProcessor
 * Method:    nativeCreateProcessor
 * Signature: (Ljava/lang/String;Ljava/lang/String;)J
 */
JNIEXPORT jlong JNICALL Java_com_wetext_WetextProcessor_nativeCreateProcessor(
    JNIEnv *env, jclass cls, jstring taggerPath, jstring verbalizerPath) {
  const char *tagger = env->GetStringUTFChars(taggerPath, nullptr);
  const char *verbalizer = env->GetStringUTFChars(verbalizerPath, nullptr);

  WetextProcessorHandle handle = wetext_create_processor(tagger, verbalizer);

  env->ReleaseStringUTFChars(taggerPath, tagger);
  env->ReleaseStringUTFChars(verbalizerPath, verbalizer);

  return reinterpret_cast<jlong>(handle);
}

/*
 * Class:     com_wetext_WetextProcessor
 * Method:    nativeDestroyProcessor
 * Signature: (J)V
 */
JNIEXPORT void JNICALL Java_com_wetext_WetextProcessor_nativeDestroyProcessor(
    JNIEnv *env, jclass cls, jlong handle) {
  if (handle != 0) {
    wetext_destroy_processor(reinterpret_cast<WetextProcessorHandle>(handle));
  }
}

/*
 * Class:     com_wetext_WetextProcessor
 * Method:    nativeNormalize
 * Signature: (JLjava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_wetext_WetextProcessor_nativeNormalize(
    JNIEnv *env, jclass cls, jlong handle, jstring input) {
  if (handle == 0) {
    return env->NewStringUTF("");
  }

  const char *text = env->GetStringUTFChars(input, nullptr);
  const char *result =
      wetext_normalize(reinterpret_cast<WetextProcessorHandle>(handle), text);
  env->ReleaseStringUTFChars(input, text);

  jstring jresult = env->NewStringUTF(result ? result : "");
  if (result) {
    wetext_free_string(result);
  }
  return jresult;
}

/*
 * Class:     com_wetext_WetextProcessor
 * Method:    nativeTag
 * Signature: (JLjava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_wetext_WetextProcessor_nativeTag(
    JNIEnv *env, jclass cls, jlong handle, jstring input) {
  if (handle == 0) {
    return env->NewStringUTF("");
  }

  const char *text = env->GetStringUTFChars(input, nullptr);
  const char *result =
      wetext_tag(reinterpret_cast<WetextProcessorHandle>(handle), text);
  env->ReleaseStringUTFChars(input, text);

  jstring jresult = env->NewStringUTF(result ? result : "");
  if (result) {
    wetext_free_string(result);
  }
  return jresult;
}

/*
 * Class:     com_wetext_WetextProcessor
 * Method:    nativeVerbalize
 * Signature: (JLjava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_com_wetext_WetextProcessor_nativeVerbalize(
    JNIEnv *env, jclass cls, jlong handle, jstring input) {
  if (handle == 0) {
    return env->NewStringUTF("");
  }

  const char *text = env->GetStringUTFChars(input, nullptr);
  const char *result =
      wetext_verbalize(reinterpret_cast<WetextProcessorHandle>(handle), text);
  env->ReleaseStringUTFChars(input, text);

  jstring jresult = env->NewStringUTF(result ? result : "");
  if (result) {
    wetext_free_string(result);
  }
  return jresult;
}

}  // extern "C"
