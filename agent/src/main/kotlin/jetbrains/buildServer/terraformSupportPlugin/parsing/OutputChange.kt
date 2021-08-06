package jetbrains.buildServer.terraformSupportPlugin.parsing

class OutputChange(
    actions: List<Action>,
    val before: List<String>?,
    val after: List<String>?
) : ActionDetails(actions)