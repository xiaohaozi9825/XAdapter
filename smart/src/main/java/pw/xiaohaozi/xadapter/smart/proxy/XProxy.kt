package pw.xiaohaozi.xadapter.smart.proxy

/**
 * 代理类接口
 * 描述：所有的代理类接口都需要继承该接口
 *
 * 作者：小耗子
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/9 8:37
 */
interface XProxy<Employer : XProxy<Employer>> {
    /**
     * 雇主对象实例，雇主可能是adapter，也可能是provider。
     * 该对象需要在雇主类中赋值，否则会报属性为初始化异常
     */
    var employer: Employer

    /**
     * 绑定代理的宿主；子类若在初始化链中重写，应调用 `super.initProxy(employer)`。
     * @param employer 实际宿主（通常为 Adapter 或 Provider 自身）。
     */
    fun initProxy(employer: Employer) {
        this.employer = employer
    }
}
