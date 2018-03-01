package com.mapfit.tetragon

import android.os.Build
import android.support.v4.util.ArrayMap
import android.util.SparseArray
import android.util.Xml
import com.mapfit.android.utils.logException
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.*
import java.util.*

class FontFileParser {

    private var fontDict: MutableMap<String, String>? = null
    private val fallbackFontDict: SparseArray<MutableList<String>>

    init {
        fontDict = if (Build.VERSION.SDK_INT > 18) {
            ArrayMap()
        } else {
            HashMap()
        }
        fallbackFontDict = SparseArray()
    }

    private fun addFallback(weight: Int?, filename: String) {
        val fullFileName = systemFontPath + filename
        if (!File(fullFileName).exists()) {
            return
        }

        if (fallbackFontDict.indexOfKey(weight!!) < 0) {
            fallbackFontDict.put(weight, ArrayList())
        }

        fallbackFontDict.get(weight).add(fullFileName)
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun processDocumentPreLollipop(parser: XmlPullParser) {
        parser.nextTag()
        parser.require(XmlPullParser.START_TAG, null, "familyset")

        val namesets = ArrayList<String>()
        val filesets = ArrayList<String>()

        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }

            if ("family" != parser.name) {
                skip(parser)
                continue
            }

            namesets.clear()
            filesets.clear()

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.eventType != XmlPullParser.START_TAG) {
                    continue
                }

                if ("nameset" == parser.name) {
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.eventType != XmlPullParser.START_TAG) {
                            continue
                        }

                        val name = parser.nextText()
                        namesets.add(name.toLowerCase())
                    }
                    continue
                }

                if ("fileset" == parser.name) {
                    while (parser.next() != XmlPullParser.END_TAG) {
                        if (parser.eventType != XmlPullParser.START_TAG) {
                            continue
                        }
                        val filename = parser.nextText()
                        // Don't use UI fonts
                        if (filename.contains("UI-")) {
                            continue
                        }
                        // Sorry - not yet supported
                        if (filename.contains("Emoji")) {
                            continue
                        }
                        filesets.add(filename)
                    }
                } else {
                    skip(parser)
                }

                // fallback_fonts.xml entries have no names
                if (namesets.isEmpty()) {
                    namesets.add("sans-serif")
                }

                for (filename in filesets) {
                    for (fontname in namesets) {

                        var style = "normal"
                        // The file structure in `/etc/system_fonts.xml` is quite undescriptive
                        // which makes it hard to make a matching from a font style to a font file
                        // e.g. italic -> font file, instead we extract this information from the
                        // font file name itself
                        val fileSplit = filename.split("-".toRegex()).dropLastWhile { it.isEmpty() }
                            .toTypedArray()
                        if (fileSplit.size > 1) {
                            style = fileSplit[fileSplit.size - 1].toLowerCase()
                            // Remove extension .ttf
                            style = style.substring(0, style.lastIndexOf('.'))

                            if ("regular" == style) {
                                style = "normal"
                            }
                        }

                        // Same here, font boldness is non-available for android < 5.0 file
                        // description, we default to integer boldness of 400 by default
                        fontDict!![fontname + "_400_" + style] = systemFontPath + filename

                        if ("sans-serif" == fontname && "normal" == style) {
                            addFallback(400, filename)
                        }
                    }
                }
            }
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun processDocument(parser: XmlPullParser) {
        val familyWeights = ArrayList<String>()

        parser.nextTag()
        // Parse Families
        parser.require(XmlPullParser.START_TAG, null, "familyset")
        while (parser.next() != XmlPullParser.END_DOCUMENT) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if ("family" == parser.name) {
                familyWeights.clear()
                // Parse this family:
                val name = parser.getAttributeValue(null, "name")

                //Unused : final String lang = parser.getAttributeValue(null, "lang");

                // fallback fonts
                if (name == null) {
                    parseFallBackFonts(parser, familyWeights)
                } else {
                    parseFallBackFonts2(parser, familyWeights, name)
                }
            } else if ("alias" == parser.name) {
                parseAliasFont(parser, familyWeights)
            } else {
                skip(parser)
            }
        }
    }

    private fun parseAliasFont(
        parser: XmlPullParser,
        familyWeights: ArrayList<String>
    ) {
        // Parse this alias to font to fileName
        val aliasName = parser.getAttributeValue(null, "name")
        val toName = parser.getAttributeValue(null, "to")
        val weightStr = parser.getAttributeValue(null, "weight")
        val aliasWeights: List<String>
        var fontFilename: String

        aliasWeights = if (weightStr == null) {
            familyWeights
        } else {
            listOf(weightStr)
        }

        for (weight in aliasWeights) {
            // Only 2 styles possible based on /etc/fonts.xml
            // Normal style
            fontFilename = fontDict!![toName + "_" + weight + "_normal"].toString()
            fontDict!![aliasName + "_" + weight + "_normal"] = fontFilename
            // Italic style
            fontFilename = fontDict!![toName + "_" + weight + "_italic"].toString()
            fontDict!![aliasName + "_" + weight + "_italic"] = fontFilename
        }
    }

    private fun parseFallBackFonts2(
        parser: XmlPullParser,
        familyWeights: ArrayList<String>,
        name: String
    ) {
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            val tag = parser.name
            if ("font" == tag) {
                var weightStr: String? = parser.getAttributeValue(null, "weight")
                if (weightStr == null) {
                    weightStr = "400"
                } else {
                    familyWeights.add(weightStr)
                }

                var styleStr: String? = parser.getAttributeValue(null, "style")
                if (styleStr == null) {
                    styleStr = "normal"
                }

                val filename = parser.nextText()
                val key = name + "_" + weightStr + "_" + styleStr
                fontDict!![key] = systemFontPath + filename

                if ("sans-serif" == name && "normal" == styleStr) {
                    addFallback(Integer.valueOf(weightStr), filename)
                }

            } else {
                skip(parser)
            }
        }
    }

    private fun parseFallBackFonts(
        parser: XmlPullParser,
        familyWeights: ArrayList<String>
    ) {
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            try {

                val tag = parser.name
                if ("font" == tag) {
                    var weightStr: String? = parser.getAttributeValue(null, "weight")
                    if (weightStr == null) {
                        weightStr = "400"
                    } else {
                        familyWeights.add(weightStr)
                    }

                    val filename = parser.nextText()

                    // Don't use UI fonts
                    if (filename.contains("UI-")) {
                        continue
                    }
                    // Sorry - not yet supported
                    if (filename.contains("Emoji")) {
                        continue
                    }

                    addFallback(Integer.valueOf(weightStr), filename)
                } else {
                    skip(parser)
                }
            } catch (e: Exception) {
                logException(e)
            }
        }
    }

    @Throws(XmlPullParserException::class, IOException::class)
    private fun skip(parser: XmlPullParser) {
        var depth = 1
        while (depth > 0) {
            when (parser.next()) {
                XmlPullParser.START_TAG -> depth++
                XmlPullParser.END_TAG -> depth--
            }
        }
    }

    fun parse() {
        var fontFile = File(fontXMLPath)

        if (fontFile.exists()) {
            parse(fontFile.absolutePath, false)
            return
        }

        fontFile = File(oldFontXMLPath)
        if (fontFile.exists()) {
            parse(fontFile.absolutePath, true)
        }
        fontFile = File(oldFontXMLFallbackPath)
        if (fontFile.exists()) {
            parse(fontFile.absolutePath, true)
        }
    }

    private fun parse(fileXml: String, oldXML: Boolean) {
        val `in`: InputStream

        try {
            `in` = FileInputStream(fileXml)
        } catch (e: FileNotFoundException) {
            logException(e)
            return
        }

        val parser = Xml.newPullParser()

        try {
            parser.setInput(`in`, null)

            if (oldXML) {
                processDocumentPreLollipop(parser)
            } else {
                processDocument(parser)
            }
        } catch (e: XmlPullParserException) {
            logException(e)
        } catch (e: IOException) {
            logException(e)

        }

        try {
            `in`.close()
        } catch (e: IOException) {
            logException(e)

        }
    }

    fun getFontFile(_key: String): String {
        return if (fontDict!!.containsKey(_key)) fontDict!![_key].toString() else ""
    }

    /*
     * Returns the next available font fallback, or empty string if not found
     * The integer value determines the fallback priority (lower is higher)
     * The weightHint value determines the closest fallback hint for boldness
     * See /etc/fonts/font_fallback for documentation
     */
    fun getFontFallback(importance: Int, weightHint: Int): String {
        var diffWeight = Integer.MAX_VALUE
        var fallback = ""

        for (i in 0 until fallbackFontDict.size()) {
            val diff = Math.abs(fallbackFontDict.keyAt(i) - weightHint)

            if (diff < diffWeight) {
                val fallbacks = fallbackFontDict.valueAt(i)

                if (importance < fallbacks.size) {
                    fallback = fallbacks[importance]
                    diffWeight = diff
                }
            }
        }

        return fallback
    }

    companion object {

        private const val systemFontPath = "/system/fonts/"
        // Android version >= 5.0
        private const val fontXMLPath = "/system/etc/fonts.xml"

        // Android version < 5.0
        private const val oldFontXMLPath = "/system/etc/system_fonts.xml"
        private const val oldFontXMLFallbackPath = "/system/etc/fallback_fonts.xml"
    }

}
