package io.poyarzun.concoursedsl.domain

sealed class Step {
    var tags: MutableList<String>? = null
    var timeout: String? = null
    var attempts: Int? = null

    data class GetStep(
        val get: String,
        var resource: String? = null,
        var version: String? = null,
        var passed: List<String>? = null,
        var params: Map<String, Any?>? = null,
        var trigger: Boolean? = null
    ) : Step()

    data class PutStep(
        val put: String,
        var resource: String? = null,
        var params: Map<String, Any?>? = null,
        var getParams: Map<String, Any?>? = null
    ) : Step()

    data class TaskStep(
        val task: String,

        // TODO: At most one of these is required
        var config: Task? = null,
        var file: String? = null,

        var privileged: Boolean? = null,
        var params: Map<String, Any?>? = null,
        var image: String? = null,
        var inputMapping: Map<String, String>? = null,
        var outputMapping: Map<String, String?>? = null
    ) : Step()

    data class AggregateStep(val aggregate: MutableList<Step> = ArrayList()): Step()

    data class DoStep(val `do`: MutableList<Step> = ArrayList()): Step()

    data class TryStep(val `try`: Step): Step()
}