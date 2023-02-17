# Description: Build rules for building androidx.test.truth from source

load("@build_bazel_rules_android//android:rules.bzl", "android_library")
load("@io_bazel_rules_kotlin//kotlin:android.bzl", "kt_android_library")
load("//build_extensions:release.bzl", "axt_release_lib")
load("//build_extensions:maven_repo.bzl", "maven_artifact")
load(
    "//build_extensions:axt_versions.bzl",
    "ANDROIDX_ANNOTATION_VERSION",
    "ANDROIDX_CONCURRENT_VERSION",
    "ANDROIDX_LIFECYCLE_VERSION",
    "ANDROIDX_TRACING_VERSION",
    "CORE_VERSION",
    "GUAVA_LISTENABLEFUTURE_VERSION",
    "KOTLIN_VERSION",
    "MONITOR_VERSION",
    "SERVICES_VERSION",
)
load("//build_extensions:dackka_test.bzl", "dackka_test")
load("//build_extensions:api_checks.bzl", "api_checks")

# all users should reference the equivalent targets in //core
package(
    default_applicable_licenses = ["//:license"],
    default_visibility = [
        ":allowlist",
    ],
)

package_group(
    name = "allowlist",
    packages = [
        "//...",
    ],
)

licenses(["notice"])

# target containing bootstrap activity manifest entries
# Add this to your application under test to avoid a separate test process getting launched
# for the bootstrap activities
android_library(
    name = "manifest",
    exports_manifest = 1,
    manifest = "AndroidManifest.xml",
    resource_files = glob(["res/**"]),
)

kt_android_library(
    name = "core_internal",
    srcs = glob(
        [
            "**/*.java",
            "**/*.kt",
        ],
    ),
    tags = ["alt_dep=//core"],
    visibility = ["//visibility:private"],
    deps = [
        ":manifest",
        "//annotation",
        "//runner/monitor",
        "//services/storage",
        "@maven//:androidx_annotation_annotation",
        "@maven//:androidx_concurrent_concurrent_futures",
        "@maven//:androidx_lifecycle_lifecycle_common",
        "@maven//:androidx_tracing_tracing",
        "@maven_listenablefuture//:com_google_guava_listenablefuture",
    ],
)

# TODO(b/199522848): delete these targets once espresso moves to testonly
android_library(
    name = "core",
    testonly = 1,
    exports = [":core_internal"],
)

android_library(
    name = "core_not_test_only",
    visibility = ["//espresso/core/java/androidx/test/espresso:__subpackages__"],
    exports = [":core_internal"],
)

# group of targets to use to produce release binary + docs
android_library(
    name = "core_release_lib",
    testonly = 1,
    visibility = ["//:__subpackages__"],
    exports = [
        ":core_internal",
    ],
)

alias(
    name = "core-src",
    actual = ":libcore_internal-src.jar",
    visibility = ["//:__pkg__"],
)

# Generate rules for the release artifacts. This generates three targets
# genrule output: core_release_no_deps.jar and core_release.aar
# a java_library target libcore_release_no_deps
axt_release_lib(
    name = "core_release",
    # keep all androidx.test.core classes except androidx.test.core.R, since that will be
    # auto-generated by consuming build system
    keep_spec = "androidx/test/core/.*|META-INF/androidx.test.core.kotlin_module",
    remove_spec = "androidx/test/core/R[$$\\.]",
    resource_files = glob(["res/**"]),
    deps = [
        ":core_release_lib",
    ],
)

dackka_test(
    name = "core_doc",
    jar = ":core_release_all_deploy.jar",
    src_jar = ":libcore_internal-src.jar",
)

maven_artifact(
    name = "core_maven_artifact",
    src = ":core_release.aar",
    artifact_deps = [
        "androidx.annotation:annotation:%s" % ANDROIDX_ANNOTATION_VERSION,
        "androidx.test:monitor:%s" % MONITOR_VERSION,
        "androidx.test.services:storage:%s" % SERVICES_VERSION,
        "androidx.lifecycle:lifecycle-common:%s" % ANDROIDX_LIFECYCLE_VERSION,
        "androidx.tracing:tracing:%s" % ANDROIDX_TRACING_VERSION,
        "com.google.guava:listenablefuture:%s" % GUAVA_LISTENABLEFUTURE_VERSION,
        "org.jetbrains.kotlin:kotlin-stdlib:%s" % KOTLIN_VERSION,
        "androidx.concurrent:concurrent-futures:%s" % ANDROIDX_CONCURRENT_VERSION,
    ],
    artifact_id = "core",
    group_id = "androidx.test",
    last_updated = "20180403000000",
    src_jar = ":libcore_internal-src.jar",
    version = "%s" % CORE_VERSION,
)

api_checks(
    name = "core_api",
    deploy_jar = ":core_release_all_deploy.jar",
    src_jar = ":libcore_internal-src.jar",
)