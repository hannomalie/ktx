import io.exoquery.terpal.Interpolator
import web.components.customElements
import web.html.HTMLElement
import web.html.HtmlTagName
import web.window.window
import kotlin.reflect.KProperty

//@JsModule("lit")
//@JsNonModule
open external class LitElement: HTMLElement {
    open fun render(): dynamic
    fun requestUpdate()
}

external fun html(a: TemplateStringsArray, vararg b: Any?): dynamic
//external fun html(a: TemplateStringsArray, b: Any?, c: Any? = definedExternally, d: Any? = definedExternally, e: Any? = definedExternally, f: Any? = definedExternally): dynamic
fun _html(strings: Array<String>, values: Array<Any?>) = html(TemplateStringsArray(strings), values)

external interface TemplateStringsArray {
    var raw: Array<String>
}
fun TemplateStringsArray(array: Array<String>): TemplateStringsArray {
    return js("new TemplateStringsArray()").unsafeCast<TemplateStringsArray>().apply {
        this.asDynamic()["raw"] = array
        val x = this
        array.forEach {
            x.asDynamic().push(it)
        }
    }
}

class SimpleNumber: LitElement() {
    var number: Int = 0
        set(value) {
            field = value
            requestUpdate()
        }

    override fun render(): dynamic {
        return _html("<div>hallo $number<button @click=${ { number++; } }>$number</button><div>")
    }
//    companion object {
//        @JsStatic
//        fun properties() = js("""({ "number" : { "type" : Number } })""")
//    }
}
class SimpleGreeting: LitElement() {
    val number: Int = 13
    var foo: String = "everyone"
        set(value) {
            field = value
            requestUpdate()
        }
//    var foo by State("everyone")

    override fun render(): dynamic {
        return _html("<div><div>hello $foo</div>${ SimpleNumber().apply { this.number = number } }<button @click=${ { foo = "bar" } }>Click Me</button><button @click=${MyHandler("b")}>Click Me</button><button @click=${MyHandler("c")}>Click Me</button></div>")
    }
}

open class MyHandler(val name: String) {
    @JsName("handleEvent") open fun handleEvent(e: dynamic) {
        console.log("MyHandler $name called")
    }
}

fun main() {
    customElements.define(HtmlTagName("simple-greeting"), SimpleGreeting::class.js)
    customElements.define(HtmlTagName("simple-number"), SimpleNumber::class.js)
    window["SimpleNumberClass"] = SimpleNumber::class.js
    console.log("class: " + SimpleNumber::class.js)
}

class LitInterpolator : Interpolator<Any, dynamic> {
    override fun interpolate(parts: () -> List<String>, params: () -> List<Any>): dynamic {
        val params: Array<Any?> = params().toTypedArray()
//        return html(TemplateStringsArray(parts().toTypedArray()), params[0], params[1], params[2], params[3], params[4])
        return html(TemplateStringsArray(parts().toTypedArray()), *params)
    }
}

val _html = LitInterpolator()

data class State<T>(@JsName("underlying") var underlying: T) {
    init {
        this.asDynamic().underlying = underlying
    }
    operator fun getValue(thisRef: LitElement, property: KProperty<*>): T {
        return underlying
    }
    operator fun setValue(thisRef: LitElement, property: KProperty<*>, value: Any?) {
        underlying = value as T
        thisRef.requestUpdate()
    }
}
