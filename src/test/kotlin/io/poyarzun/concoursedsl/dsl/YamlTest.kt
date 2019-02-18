package io.poyarzun.concoursedsl.dsl

import io.poyarzun.concoursedsl.domain.Pipeline
import io.poyarzun.concoursedsl.domain.Step
import io.poyarzun.concoursedsl.domain.Task
import org.junit.Test
import kotlin.test.assertEquals

class YamlTest {
    @Test
    fun testBasicPipelineYamlUsesSnakeCase() {
        val pipeline = Pipeline().apply {
            resourceType("rss", "docker-image") {
                source = mapOf(
                        "repository" to "suhlig/concourse-rss-resource",
                        "tag" to "latest"
                )
                checkEvery = "10m"
            }

            resource("concourse-dsl-source", "git") {
                source = mapOf(
                        "uri" to "git@github.com:Logiraptor/concourse-dsl",
                        "private_key" to "((github-deploy-key))"
                )
                checkEvery = "20m"
                webhookToken = "totally-a-secret"
            }

            resource("results", "s3") {
                source = mapOf(
                        "bucket" to "results-bucket",
                        "access_key" to "((aws_access_key))",
                        "secret_key" to "((aws_secret_key))"
                )
            }

            job("Test") {
                plan {
                    get("concourse-dsl-source") {
                        trigger = true
                    }
                    task("run-tests") {
                        inputMapping = mapOf(
                                "source-code" to "concourse-dsl-source"
                        )
                        outputMapping = mapOf(
                                "result" to "output"
                        )
                        config = Task(
                                rootfsUri = "not-a-real-value",
                                platform = "linux",
                                imageResource = Task.Resource(
                                        type = "docker-image",
                                        source = mapOf(
                                                "resource" to "maven"
                                        )
                                ),
                                run = Task.RunConfig("/bin/sh", args = mutableListOf("-c", """
                        cd source-code
                        ./gradlew test
                        mkdir result
                        echo "OK" > result/result.out
                    """.trimIndent())),
                                inputs = mutableListOf(Task.Input("concourse-dsl-source"))
                        )
                    }
                    put("results") {
                        getParams = mapOf(
                                "skip_download" to "true"
                        )
                    }
                }

                buildLogsToRetain = 1
                serialGroups = mutableListOf("unique-jobs")
                maxInFlight = 1

                disableManualTrigger = false

                onSuccess = Step.GetStep("some-resource")
                onFailure = Step.GetStep("some-other-resource")
                onAbort  = Step.GetStep("yet-another-resource")
            }
        }

        val yaml = generateYML(pipeline)

        val expectedYaml = """
            ---
            jobs:
            - name: "Test"
              plan:
              - get: "concourse-dsl-source"
                trigger: true
              - task: "run-tests"
                config:
                  platform: "linux"
                  run:
                    path: "/bin/sh"
                    args:
                    - "-c"
                    - "cd source-code\n./gradlew test\nmkdir result\necho \"OK\" > result/result.out"
                  image_resource:
                    type: "docker-image"
                    source:
                      resource: "maven"
                  rootfs_uri: "not-a-real-value"
                  inputs:
                  - name: "concourse-dsl-source"
                input_mapping:
                  source-code: "concourse-dsl-source"
                output_mapping:
                  result: "output"
              - put: "results"
                get_params:
                  skip_download: "true"
              build_logs_to_retain: 1
              serial_groups:
              - "unique-jobs"
              max_in_flight: 1
              disable_manual_trigger: false
              on_success:
                get: "some-resource"
              on_failure:
                get: "some-other-resource"
              on_abort:
                get: "yet-another-resource"
            groups: []
            resources:
            - name: "concourse-dsl-source"
              type: "git"
              source:
                uri: "git@github.com:Logiraptor/concourse-dsl"
                private_key: "((github-deploy-key))"
              check_every: "20m"
              webhook_token: "totally-a-secret"
            - name: "results"
              type: "s3"
              source:
                bucket: "results-bucket"
                access_key: "((aws_access_key))"
                secret_key: "((aws_secret_key))"
            resource_types:
            - name: "rss"
              type: "docker-image"
              source:
                repository: "suhlig/concourse-rss-resource"
                tag: "latest"
              check_every: "10m"

        """.trimIndent()

        assertEquals(expectedYaml, yaml)
    }
}