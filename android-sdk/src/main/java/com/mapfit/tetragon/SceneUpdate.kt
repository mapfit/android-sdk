package com.mapfit.tetragon

/**
 * Represents a data structure to specify a yaml path and the corresponding value.
 *
 * @param path  path of the property to be changed. use dot(".") to declare scopes. e.g. "global.show_transit"
 * @param value yaml string which will update the value at the specified path
 */
data class SceneUpdate(val path: String, val value: String) {

    override fun toString() = "$path $value"

}
