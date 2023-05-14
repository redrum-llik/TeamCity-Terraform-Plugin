package jetbrains.buildServer.terraformSupportPlugin.jsonOutput.model

import jetbrains.buildServer.terraformSupportPlugin.jsonOutput.deltas.ValueDelta
import jetbrains.buildServer.terraformSupportPlugin.report.ChangeItemBackground

class ResourceChange(
    val name: String,
    val type: String,
    val moduleAddress: String,
    actions: List<Action>,
    delta: ValueDelta
) : Change(actions, delta) {
    constructor(
        name: String,
        type: String,
        moduleAddress: String,
        change: Change
    ) : this(name, type, moduleAddress, change.actions, change.delta)

    val collapsibleButtonColorCSSClass: String
        get() {
            return when {
                isCreated -> ChangeItemBackground.GREEN.cssClass
                isUpdated -> ChangeItemBackground.BLUE.cssClass
                isReplaced -> ChangeItemBackground.ORANGE.cssClass
                isDeleted -> ChangeItemBackground.RED.cssClass
                else -> ChangeItemBackground.FALLBACK.cssClass
            }
        }

    override fun toString(): String {
        return "[$moduleAddress.$name]"
    }
}