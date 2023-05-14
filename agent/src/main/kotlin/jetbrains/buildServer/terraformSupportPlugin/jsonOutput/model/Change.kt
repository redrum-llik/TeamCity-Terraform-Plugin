package jetbrains.buildServer.terraformSupportPlugin.jsonOutput.model

import jetbrains.buildServer.terraformSupportPlugin.jsonOutput.deltas.ValueDelta

open class Change(
    val actions: List<Action>,
    val delta: ValueDelta
) {
    val isNotChanged: Boolean
        get() {
            val action = actions.first()
            return action == Action.NO_OP
                    || action == Action.READ
        }

    val isChanged: Boolean
        get() {
            return !isNotChanged
        }

    val isReplaced: Boolean
        get() {
            return actions.containsAll(
                listOf(
                    Action.CREATE,
                    Action.DELETE
                )
            )
        }

    val isDeleted: Boolean
        get() {
            return actions.size == 1 && actions.first() == Action.DELETE
        }

    val isCreated: Boolean
        get() {
            return actions.size == 1 && actions.first() == Action.CREATE
        }

    val isUpdated: Boolean
        get() {
            return actions.size == 1 && actions.first() == Action.UPDATE
        }

    val hasChangedValues: Boolean
        get() {
            return getChangedValues.isNotEmpty()
        }

    val getChangedValues: List<ValueDelta>
        get() {
            return this.delta.getChangedValues
        }
}