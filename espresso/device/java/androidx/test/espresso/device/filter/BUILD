# Description:
#   Filters that can be used to skip tests based on device properties.

load("@io_bazel_rules_kotlin//kotlin:android.bzl", "kt_android_library")

licenses(["notice"])

package(
    default_applicable_licenses = ["//espresso:license"],
    default_visibility = [
        "//espresso/device/java/androidx/test/espresso/device:device_pkg",
    ],
)

kt_android_library(
    name = "filter",
    testonly = 1,
    srcs = glob(["*.kt"]),
    deps = [
        "//annotation/java/androidx/test/annotation",
        "//espresso/device/java/androidx/test/espresso/device/controller",
        "//espresso/device/java/androidx/test/espresso/device/sizeclass",
        "//espresso/device/java/androidx/test/espresso/device/util",
        "//runner/android_junit_runner",
        "//runner/monitor",
        "@maven//:junit_junit",
    ],
)

kt_android_library(
    name = "requires_display_filter",
    testonly = 1,
    srcs = [
        "RequiresDisplay.kt",
        "RequiresDisplayFilter.kt",
    ],
    deps = [
        "//annotation/java/androidx/test/annotation",
        "//espresso/device/java/androidx/test/espresso/device/sizeclass",
        "//runner/android_junit_runner",
        "//runner/monitor",
        "@maven//:junit_junit",
    ],
)
