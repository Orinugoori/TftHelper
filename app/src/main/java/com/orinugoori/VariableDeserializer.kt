package com.orinugoori

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.orinugoori.tfthelper.data.Variables
import java.lang.reflect.Type

class VariablesDeserializer : JsonDeserializer<Variables> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Variables {
        val jsonObject = json.asJsonObject
        val standardKeys = mutableMapOf<String, Double>()
        val dynamicKeys = mutableMapOf<String, Any>()

        for ((key, value) in jsonObject.entrySet()) {
            if (key.matches(Regex("[a-zA-Z]+"))) {
                standardKeys[key] = value.asDouble
            } else {
                dynamicKeys[key] = when {
                    value.isJsonPrimitive -> value.asDoubleOrNull() ?: value.asString
                    else -> value.toString()
                }
            }
        }

        return Variables(standardKeys, dynamicKeys)
    }

    private fun JsonElement.asDoubleOrNull(): Double? = try {
        this.asDouble
    } catch (e: Exception) {
        null
    }
}