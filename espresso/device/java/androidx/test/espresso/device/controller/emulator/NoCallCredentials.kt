/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package androidx.test.espresso.device.controller.emulator

import io.grpc.CallCredentials
import io.grpc.Metadata
import java.util.concurrent.Executor

/** Call credentials that do nothing. */
internal class NoCallCredentials : CallCredentials() {

  override fun applyRequestMetadata(
    requestInfo: CallCredentials.RequestInfo,
    appExecutor: Executor,
    applier: CallCredentials.MetadataApplier
  ) {
    appExecutor.execute {
      var headers: Metadata = Metadata()
      applier.apply(headers)
    }
  }

  override fun thisUsesUnstableApi() {}
}
