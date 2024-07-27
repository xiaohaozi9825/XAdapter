package pw.xiaohaozi.xadapter.smart.utils

import java.util.*
import java.util.regex.Pattern

/**
 * 描述：
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2022/10/20 21:06
 */
object UnderlineToCamelUtils {
    /**
     * 下划线转驼峰法
     *
     * @param smallCamel 大小驼峰,是否为小驼峰
     * @return 转换后的字符串
     */
    fun String?.toCamel(smallCamel: Boolean = false): String {
        if (this.isNullOrBlank()) return ""
        val sb = StringBuffer()
        val pattern = Pattern.compile("([A-Za-z\\d]+)(_)?")
        val matcher = pattern.matcher(this)
        while (matcher.find()) {
            val word = matcher.group()
            sb.append(if (smallCamel && matcher.start() == 0) word[0].lowercaseChar() else word[0].uppercaseChar())
            val index = word.lastIndexOf('_')
            if (index > 0) {
                sb.append(word.substring(1, index).lowercase(Locale.getDefault()))
            } else {
                sb.append(word.substring(1).lowercase(Locale.getDefault()))
            }
        }
        return sb.toString()
    }

    /**
     * 驼峰法转下划线
     *
     * @return 转换后的字符串
     */
    fun String?.toUnderline(): String {
        if (this.isNullOrBlank()) return ""
        val line = this[0].toString().uppercase(Locale.getDefault()) + this.substring(1)
        val sb = StringBuffer()
        val pattern = Pattern.compile("[A-Z]([a-z\\d]+)?")
        val matcher = pattern.matcher(line)
        while (matcher.find()) {
            val word = matcher.group()
            sb.append(word.uppercase(Locale.getDefault()))
            sb.append(if (matcher.end() == line.length) "" else "_")
        }
        return sb.toString()
    }
}