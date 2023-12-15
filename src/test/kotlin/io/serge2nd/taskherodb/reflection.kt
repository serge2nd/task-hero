package io.serge2nd.taskherodb

import java.lang.invoke.MethodHandles.lookup
import java.lang.invoke.MethodHandles.privateLookupIn
import java.lang.reflect.Field
import java.lang.reflect.Modifier.FINAL
import io.hypersistence.utils.common.ReflectionUtils.getField as field
import java.lang.Integer.TYPE as INT

inline fun <reified T> cl() = T::class.java

/** Works only before the first access to the field */
inline fun <reified T> setFinal(name: String, value: Any?) = field(cl<T>(), name)
    .run { MODS_VH.set(this, modifiers and FINAL.inv()); set(null, value) }

val MODS_VH = privateLookupIn(cl<Field>(), lookup()).findVarHandle(cl<Field>(), "modifiers", INT)
