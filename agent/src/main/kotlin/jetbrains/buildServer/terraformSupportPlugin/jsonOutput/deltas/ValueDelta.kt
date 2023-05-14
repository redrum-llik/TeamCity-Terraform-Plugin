package jetbrains.buildServer.terraformSupportPlugin.jsonOutput.deltas

import jetbrains.buildServer.terraformSupportPlugin.report.ChangeType

abstract class ValueDelta(
    val name: String,
    val forcesReplacement: Boolean = false
) {
    /**
     * Returns true if this element is complex,
     * e.g. describes delta between two collections of the same type.
     */
    abstract val isComplex: Boolean

    /**
     * Returns list of deltas that are described by this element.
     */
    abstract val deltas: List<ValueDelta>

    val getChangedValues: List<ValueDelta>
        get() {
            return deltas.filter { delta -> delta.isChanged }
        }

    /**
     * Returns true if this element has been changed anyhow.
     */
    abstract val isChanged: Boolean

    /**
     * Returns true if this element has been newly added.
     */
    abstract val isAdded: Boolean

    /**
     * Returns true if this element has been removed.
     */
    abstract val isRemoved: Boolean

    /**
     * Returns true if this element`s value has changed.
     */
    abstract val isUpdated: Boolean

    val changeType: ChangeType
        get() {
            return when {
                isAdded -> ChangeType.ADDED
                isRemoved -> ChangeType.REMOVED
                isUpdated -> ChangeType.UPDATED
                else -> ChangeType.FALLBACK
            }
        }
}