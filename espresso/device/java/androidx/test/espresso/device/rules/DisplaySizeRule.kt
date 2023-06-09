/*
 * Copyright (C) 2022 The Android Open Source Project
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
package androidx.test.espresso.device.rules

import android.app.Activity
import android.content.res.Configuration
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.device.controller.DeviceControllerOperationException
import androidx.test.espresso.device.util.executeShellCommand
import androidx.test.espresso.device.util.getDeviceApiLevel
import androidx.test.espresso.device.util.getResumedActivityOrNull
import androidx.test.platform.device.UnsupportedDeviceOperationException
import androidx.test.runner.lifecycle.ActivityLifecycleCallback
import androidx.test.runner.lifecycle.ActivityLifecycleMonitorRegistry
import androidx.test.runner.lifecycle.Stage
import java.util.concurrent.CountDownLatch
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/** Test rule for restoring device to its starting display size when a test case finishes */
class DisplaySizeRule : TestRule {
  override fun apply(statement: Statement, description: Description): Statement {
    return object : Statement() {
      override fun evaluate() {
        if (getDeviceApiLevel() < 24) {
          throw UnsupportedDeviceOperationException(
            "DisplaySizeRule is not supported on devices with APIs below 24."
          )
        }

        val startingDisplaySize = calculateCurrentDisplay()
        statement.evaluate()

        if (startingDisplaySize != calculateCurrentDisplay()) {
          val activity = getResumedActivityOrNull()
          if (activity != null) {
            val latch = CountDownLatch(1)
            val container: ViewGroup =
              activity.getWindow().findViewById(android.R.id.content) as ViewGroup
            val activityView: View =
              object : View(activity) {
                override fun onConfigurationChanged(newConfig: Configuration?) {
                  super.onConfigurationChanged(newConfig)
                  if (startingDisplaySize == calculateCurrentDisplay()) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                      Log.d(
                        TAG,
                        "View configuration changed. Display size restored to starting size."
                      )
                    }
                    latch.countDown()
                  }
                }
              }
            activity.runOnUiThread { container.addView(activityView) }
            val activityLifecyleCallback: ActivityLifecycleCallback =
              object : ActivityLifecycleCallback {
                override fun onActivityLifecycleChanged(activity: Activity, stage: Stage) {
                  if (
                    activity.getLocalClassName() == activity.getLocalClassName() &&
                      stage == Stage.PAUSED &&
                      startingDisplaySize == calculateCurrentDisplay()
                  ) {
                    if (Log.isLoggable(TAG, Log.DEBUG)) {
                      Log.d(TAG, "Activity restarted. Display size restored to starting size.")
                    }
                    latch.countDown()
                  }
                }
              }
            ActivityLifecycleMonitorRegistry.getInstance()
              .addLifecycleCallback(activityLifecyleCallback)

            executeShellCommand(
              "wm size ${startingDisplaySize.first}x${startingDisplaySize.second}"
            )
            latch.await()

            activity.runOnUiThread { container.removeView(activityView) }
            ActivityLifecycleMonitorRegistry.getInstance()
              .removeLifecycleCallback(activityLifecyleCallback)
          } else {
            throw DeviceControllerOperationException(
              "Device could not be set to the requested display size because there are no activities in" +
                " the resumed stage."
            )
          }
        }
      }
    }
  }

  private fun calculateCurrentDisplay(): Pair<Int, Int> {
    // "wm size" will output a string with the format
    // "Physical size: WxH
    //  Override size: WxH"
    val output = executeShellCommand("wm size")

    var subStringToFind = "Override size: "
    if (output.contains(subStringToFind)) {
      val displaySizes =
        output.substring(output.indexOf(subStringToFind) + subStringToFind.length).trim().split("x")
      val widthPx = displaySizes.get(0).toInt()
      val heightPx = displaySizes.get(1).toInt()
      return Pair(widthPx, heightPx)
    } else {
      // If the display size has not been overriden, the "wm size" output will only contain physical
      // size
      subStringToFind = "Physical size: "
      val displaySizes =
        output.substring(output.indexOf(subStringToFind) + subStringToFind.length).trim().split("x")
      val widthPx = displaySizes.get(0).toInt()
      val heightPx = displaySizes.get(1).split("\n").get(0).toInt()
      return Pair(widthPx, heightPx)
    }
  }

  companion object {
    private val TAG = DisplaySizeRule::class.java.simpleName
  }
}
