package com.mapfit.tetragon

/**
 * Represents a data structure to specify a yaml path and the corresponding value.
 *
 * @param path  Series of yaml keys separated by a ".". Represents the scene path to be updated
 * @param value A yaml string which will update the value at the specified path
 */
data class SceneUpdate(val path: String, val value: String) {

    override fun toString() = "$path $value"

}
