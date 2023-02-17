load("@build_bazel_rules_android//android:rules.bzl", "android_library")
load("//build_extensions:release.bzl", "axt_release_lib")
load("//build_extensions:maven_repo.bzl", "maven_artifact")
load("//build_extensions:axt_versions.bzl", "ANDROIDX_ANNOTATION_VERSION", "ESPRESSO_VERSION")
load("//build_extensions:combine_jars.bzl", "combine_jars")
load("//build_extensions:dackka_test.bzl", "dackka_test")
load("//build_extensions:api_checks.bzl", "api_checks")

# Description:
# Network Idling Resources
licenses(["notice"])

package(
    default_applicable_licenses = ["//espresso:license"],
    default_visibility = ["//:__subpackages__"],
)

android_library(
    name = "uri_idling_resource",
    srcs = ["UriIdlingResource.java"],
    visibility = ["//visibility:public"],
    deps = [
        "//espresso/idling_resource/java/androidx/test/espresso:idling_resource_internal",
        "@maven//:androidx_annotation_annotation",
    ],
)

# rule for outputting externally released binary. Produces
# idling_net_release_no_deps.jar and idling_net_release.aar
axt_release_lib(
    name = "idling_net_release",
    keep_spec = "androidx/test/espresso/idling/net",
    remove_spec = "androidx/test/espresso/idling/net/R[$$\\.]",
    deps = [
        ":uri_idling_resource",
    ],
)

filegroup(
    name = "idling_net_src",
    srcs = [
        ":liburi_idling_resource-src.jar",
    ],
)

combine_jars(
    name = "idling_net_src_jars",
    srcs = [
        ":idling_net_src",
    ],
)

dackka_test(
    name = "idling_net_doc",
    jar = ":idling_net_release_all_deploy.jar",
    src_jar = ":idling_net_src_jars.jar",
)

maven_artifact(
    name = "idling_net_maven_artifact",
    src = ":idling_net_release.aar",
    artifact_deps = [
        "androidx.annotation:annotation:%s" % ANDROIDX_ANNOTATION_VERSION,
        "androidx.test.espresso:espresso-idling-resource:%s" % ESPRESSO_VERSION,
    ],
    artifact_id = "idling-net",
    group_id = "androidx.test.espresso.idling",
    last_updated = "20170622000000",
    src_jar = ":idling_net_src_jars.jar",
    version = "%s" % ESPRESSO_VERSION,
)

api_checks(
    name = "idling_net_api",
    deploy_jar = ":idling_net_release_all_deploy.jar",
    src_jar = ":idling_net_src_jars.jar",
)