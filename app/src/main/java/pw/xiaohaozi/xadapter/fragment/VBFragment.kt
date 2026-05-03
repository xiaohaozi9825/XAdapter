package pw.xiaohaozi.xadapter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import java.lang.reflect.Method

abstract class VBFragment<V : ViewBinding> : Fragment() {
    private var _binding: V? = null
    val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = createBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.initView()
    }

    private fun createBinding(inflater: LayoutInflater, container: ViewGroup?): V {
        // 1. 获取当前类的泛型参数类型（关键代码，不会强转崩溃）
        val type = javaClass.genericSuperclass as java.lang.reflect.ParameterizedType
        val bindingClass = type.actualTypeArguments[0] as Class<V>

        // 2. 获取 inflate 方法（三参数，最标准、最稳定）
        val method: Method = bindingClass.getMethod(
            "inflate",
            LayoutInflater::class.java,
            ViewGroup::class.java,
            Boolean::class.java
        )

        // 3. 调用静态方法
        return method.invoke(null, inflater, container, false) as V
    }

    abstract fun V.initView()

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}