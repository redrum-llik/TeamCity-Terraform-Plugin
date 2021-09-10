package jetbrains.buildServer.terraformSupportPlugin.parsing

class OutputChange(
    actions: List<Action>,
    val before: Any?,
    val after: Any?
) : ActionDetails(actions)