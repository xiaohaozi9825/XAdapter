package pw.xiaohaozi.xadapter.smart.utils

import androidx.viewbinding.ViewBinding
import pw.xiaohaozi.xadapter.smart.entity.XMultiItemEntity
import pw.xiaohaozi.xadapter.smart.provider.TypeProvider
import pw.xiaohaozi.xadapter.smart.provider.XProvider
import java.lang.reflect.ParameterizedType

/** 供 public inline API 写入显式类型（内联到调用方后须能访问）。 */
@PublishedApi
internal fun XProvider<*, *>.applyExplicitTypes(viewBindingClass: Class<*>, dataClass: Class<*>) {
    setExplicitTypes(viewBindingClass, dataClass)
}

/** 解析 Provider 绑定的数据类型 D（优先 [XProvider.explicitDataClass]）。 */
@PublishedApi
internal fun resolveProviderDataClass(provider: TypeProvider<*, *>): Class<*>? {
    if (provider is XProvider<*, *>) {
        provider.explicitDataClass?.let { return it }
    }
    return resolveDataClassFromGenerics(provider.javaClass)
}

/** 解析 Provider 绑定的 [ViewBinding] 类型（优先 [XProvider.explicitViewBindingClass]）。 */
@PublishedApi
internal fun resolveProviderViewBindingClass(provider: TypeProvider<*, *>): Class<*>? {
    if (provider is XProvider<*, *>) {
        provider.explicitViewBindingClass?.let { return it }
    }
    return resolveViewBindingClassFromGenerics(provider.javaClass)
}

/** 判断 Provider 的数据类型是否为 [XMultiItemEntity]（解析失败时返回 false，不误杀）。 */
internal fun isMultiItemDataProvider(provider: TypeProvider<*, *>): Boolean {
    val dataClass = resolveProviderDataClass(provider) ?: return false
    return XMultiItemEntity::class.java.isAssignableFrom(dataClass)
}

private fun resolveDataClassFromGenerics(clazz: Class<*>): Class<*>? {
    var current: Class<*>? = clazz
    while (current != null && current != Any::class.java) {
        val generic = current.genericSuperclass
        if (generic is ParameterizedType) {
            val raw = generic.rawType as? Class<*> ?: break
            val args = generic.actualTypeArguments
            when {
                XProvider::class.java.isAssignableFrom(raw) && raw != XProvider::class.java && args.size >= 4 -> {
                    // SmartProvider / NodeProvider：PVB=index2, PD=index3
                    (args[3] as? Class<*>)?.let { return it }
                }
                XProvider::class.java.isAssignableFrom(raw) && args.size >= 2 -> {
                    (args[1] as? Class<*>)?.let { return it }
                }
            }
        }
        current = current.superclass
    }
    return null
}

private fun resolveViewBindingClassFromGenerics(clazz: Class<*>): Class<*>? {
    var current: Class<*>? = clazz
    while (current != null && current != Any::class.java) {
        val generic = current.genericSuperclass
        if (generic is ParameterizedType) {
            val raw = generic.rawType as? Class<*> ?: break
            val args = generic.actualTypeArguments
            when {
                XProvider::class.java.isAssignableFrom(raw) && raw != XProvider::class.java && args.size >= 4 -> {
                    (args[2] as? Class<*>)?.takeIf { ViewBinding::class.java.isAssignableFrom(it) }?.let { return it }
                }
                XProvider::class.java.isAssignableFrom(raw) && args.size >= 2 -> {
                    (args[0] as? Class<*>)?.takeIf { ViewBinding::class.java.isAssignableFrom(it) }?.let { return it }
                }
            }
            for (i in args.indices.reversed()) {
                (args[i] as? Class<*>)?.takeIf { ViewBinding::class.java.isAssignableFrom(it) }?.let { return it }
            }
        }
        current = current.superclass
    }
    return null
}
