# Description:
#  Device controller

load("@io_bazel_rules_kotlin//kotlin:android.bzl", "kt_android_library")

licenses(["notice"])

package(
    default_applicable_licenses = ["//espresso:license"],
    default_visibility = [
        "//espresso/device/java/androidx/test/espresso/device:device_pkg",
    ],
)

kt_android_library(
    name = "controller",
    srcs = [
        "DeviceControllerOperationException.kt",
        "DeviceMode.kt",
    ],
    deps = [
        "//espresso/core/java/androidx/test/espresso:interface",
    ],
)

kt_android_library(
    name = "controller_impl",
    testonly = 1,
    srcs = [
        "PhysicalDeviceController.kt",
    ],
    deps = [
        "//runner/monitor",
    ],
)
