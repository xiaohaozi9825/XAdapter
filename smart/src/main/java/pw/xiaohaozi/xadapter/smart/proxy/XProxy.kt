package pw.xiaohaozi.xadapter.smart.proxy

/**
 * 代理类接口
 * 描述：所有的代理类接口都需要继承该接口
 * 作者：小耗子
 * 简书地址：https://www.jianshu.com/u/2a2ea7b43087
 * github：https://github.com/xiaohaozi9825
 * 创建时间：2024/6/9 8:37
 */
interface XProxy<Employer : XProxy<Employer>> {
    /**
     * 雇主对象实例，雇主可能是adapter，也可能是provider。
     * 该对象需要在雇主类中赋值，否则会报属性为初始化异常
     */
    var employer: Employer
    fun init(employer: Employer) {
        this.employer = employer
    }
}