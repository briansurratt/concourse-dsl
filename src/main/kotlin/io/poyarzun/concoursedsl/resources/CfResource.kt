package io.poyarzun.concoursedsl.resources

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import io.poyarzun.concoursedsl.domain.Pipeline
import io.poyarzun.concoursedsl.domain.Resource
import io.poyarzun.concoursedsl.domain.Step
import io.poyarzun.concoursedsl.dsl.ConfigBlock
import io.poyarzun.concoursedsl.dsl.StepBuilder
import io.poyarzun.concoursedsl.dsl.baseResource

// https://github.com/concourse/cf-resource
object Cf {
    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
    data class SourceParams(
            val api: String,
            val organization: String,
            val space: String,
            var username: String? = null,
            var password: String? = null,
            var clientId: String? = null,
            var clientSecret: String? = null,
            var skipCertCheck: Boolean? = null,
            var verbose: Boolean? = null
    )

    class GetParams

    @JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
    data class PutParams(
            val manifest: String,
            var path: String? = null,
            var currentAppName: String? = null,
            var environmentVariables: Map<String, String>? = null,
            var vars: Map<String, String>? = null,
            var varsFiles: List<String>? = null,
            var dockerUserName: String? = null,
            var dockerPassword: String? = null,
            var showAppLog: Boolean? = null,
            var noStart: Boolean? = null
    )

}

fun Pipeline.cfResource(name: String,
                        api: String,
                        organization: String,
                        space: String,
                        configBlock: ConfigBlock<Resource<Cf.SourceParams>>) =
        this.baseResource(name, "cf", Cf.SourceParams(api, organization, space), configBlock)

fun StepBuilder.get(resource: Resource<Cf.SourceParams>, configBlock: ConfigBlock<Step.GetStep<Cf.GetParams>>) =
        this.baseGet(resource.name, Cf.GetParams(), configBlock)

fun StepBuilder.put(resource: Resource<Cf.SourceParams>, manifest: String, configBlock: ConfigBlock<Step.PutStep<Cf.GetParams, Cf.PutParams>>) =
        this.basePut(resource.name, Cf.PutParams(manifest), Cf.GetParams(), configBlock)