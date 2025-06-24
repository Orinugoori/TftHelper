package com.orinugoori

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.orinugoori.tfthelper.data.Effects
import java.lang.reflect.Type

class EffectsDeserializer(private val hashMapping : Map<String, String>) : JsonDeserializer<Effects> {
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Effects {
        val jsonObject = json.asJsonObject
        val standardEffect = mutableMapOf<String, Double>()
        val hashEffect = mutableMapOf<String, Any>()

        for ((key, value) in jsonObject.entrySet()) {
            if (key.matches(Regex("[a-zA-Z]+"))) { // 정형화된 키
                standardEffect[key] = value.asDoubleOrNull() ?: 0.0
            } else {
                    val mappedName = hashMapping[key] ?:key
                    hashEffect[mappedName] = value.toString()

            }
        }

        return Effects(standardEffect, hashEffect)
    }

    private fun JsonElement.asDoubleOrNull(): Double? = try {
        this.asDouble
    } catch (e: Exception) {
        null
    }
}