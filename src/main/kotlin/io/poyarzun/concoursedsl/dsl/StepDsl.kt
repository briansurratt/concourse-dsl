package io.poyarzun.concoursedsl.dsl

import io.poyarzun.concoursedsl.domain.Step

fun Step.tags(vararg tags: String) {
    this.tags = mutableListOf(*tags)
}

inline class StepBuilder(val addStep: (Step) -> Any?) {
    fun get(resource: String, init: Init<Step.GetStep>) =
        addStep(Step.GetStep(resource).apply(init))

    fun put(resource: String, init: Init<Step.PutStep>) =
        addStep(Step.PutStep(resource).apply(init))

    fun task(name: String, init: Init<Step.TaskStep>) =
        addStep(Step.TaskStep(name).apply(init))

    fun aggregate(init: Init<StepBuilder>) {
        val aggregateStep = Step.AggregateStep()
        StepBuilder(aggregateStep.aggregate::add).apply(init)
        addStep(aggregateStep)
    }

    fun `do`(init: Init<StepBuilder>) {
        val doStep = Step.DoStep()
        StepBuilder(doStep.`do`::add).apply(init)
        addStep(doStep)
    }

    fun `try`(init: Init<StepBuilder>) =
        oneTimeStepBuilder("try") {
            addStep(
                Step.TryStep(
                    it
                )
            )
        }.apply(init)

    private fun oneTimeStepBuilder(configName: String, addStep: (Step) -> Any?): StepBuilder {
        var called = false
        return StepBuilder {
            if (called) throw IllegalStateException("$configName may only contain at most one step")
            called = true
            addStep(it)
        }
    }
}