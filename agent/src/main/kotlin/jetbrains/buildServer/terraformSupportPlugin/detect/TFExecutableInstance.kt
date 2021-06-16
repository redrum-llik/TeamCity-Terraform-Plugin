package jetbrains.buildServer.terraformSupportPlugin.detect

data class TFExecutableInstance(
    val version: String,
    val executablePath: String,
    val isDefault: Boolean
) : Comparable<TFExecutableInstance> {
    override fun compareTo(other: TFExecutableInstance): Int {
        return if (this.isDefault && other.isDefault) {
            versionCompare(this.version, other.version)
        }
        else if (this.isDefault) 1
        else 0
    }

    companion object {
        fun versionCompare(firstVersion: String, secondVersion: String): Int {
            val firstVersionNumbers = firstVersion.split("\\.").toTypedArray()
            val secondVersionNumbers = secondVersion.split("\\.").toTypedArray()
            var i = 0
            // set index to first non-equal ordinal or length of shortest version string
            while (i < firstVersionNumbers.size && i < secondVersionNumbers.size && firstVersionNumbers[i] == secondVersionNumbers[i]) {
                i++
            }
            // compare first non-equal ordinal number
            return if (i < firstVersionNumbers.size && i < secondVersionNumbers.size) {
                val diff = Integer.valueOf(firstVersionNumbers[i]).compareTo(Integer.valueOf(secondVersionNumbers[i]))
                Integer.signum(diff)
            } else {
                Integer.signum(firstVersionNumbers.size - secondVersionNumbers.size)
            }
        }
    }
}