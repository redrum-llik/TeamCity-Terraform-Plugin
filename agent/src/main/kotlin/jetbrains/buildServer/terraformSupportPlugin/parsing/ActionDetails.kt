package jetbrains.buildServer.terraformSupportPlugin.parsing

open class ActionDetails(
    val actions: List<Action>
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
}