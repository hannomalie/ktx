package org.example

import io.exoquery.terpal.Interpolator
import js.globals.globalThis
import react.*
import react.dom.client.createRoot
import react.dom.html.ReactHTML.button
import web.dom.Element
import web.dom.document
import web.dom.parsing.DOMParserSupportedType
import kotlin.reflect.KFunction
import kotlin.reflect.KProperty

// TODO: Should normally be possible to declare like this, but it's either buggy and doesn't work or I made a mistake somewhere
// private val CustomButton by KtxFC<Props> {
private val CustomButton = KtxFC<Props>("CustomButton") {
    val (count, setCount) = useState(0)

    ktx("<button onClick=${{ setCount(count + 1) }}>$count</button>")()
}

external interface HelloProps : Props {
    var name: String
}

private val Hello = KtxFC<HelloProps>("Hello") { props ->
    ktx("<div><div>Hello ${props.name}!</div><CustomButton /><CustomButton /><CustomButton /></div>")()
}


//fun main() {
//    createRoot(document.getElementsByTagName("body")[0]).render(
//        ktx("<Hello name='you'>").create()
//    )
//}

class KTXInterpolator : Interpolator<Any, FC<*>> {
    override fun interpolate(parts: () -> List<String>, params: () -> List<Any>) = FC<Props> {

        val params = params()
        var completeString = ""

        parts().forEachIndexed { index, part ->
            completeString += part

            if (index < params.size) {
                when (val param = params[index]) {
                    is String -> completeString += param
                    is Int -> completeString += param
                    is KFunction<*> -> completeString += "\"\""
                    // TODO: Support more types
                }
            }
        }
        val currentSnippet = web.dom.parsing.DOMParser().parseFromString(completeString, DOMParserSupportedType.textXml)

        println(completeString)
        println(currentSnippet)

        currentSnippet.children.iterator().forEach { child ->
            handleChild(child, params)
        }
    }
}

fun ChildrenBuilder.handleChild(child: Element, params: List<Any>) {
    println("child $child with tagName ${child.tagName}")
    println("child attributes: " + child.attributes)
    // React/JSX convention has custom components always be uppercase
    val tagIsLowerCase = child.tagName.lowercase() == child.tagName
    if (tagIsLowerCase) {
        if (child.tagName == "button") {
            println("got button")
            val onClickOrNull = params.firstOrNull()
            println("params: " + params.size)

            button {
                onClickOrNull?.let { _onClick ->
                    onClick = {
                        (_onClick.unsafeCast<() -> Unit>())()
                    }
                }
                +child.textContent
            }
        } else {
            IntrinsicType<PropsWithClassName>(child.tagName)() {
                // TODO: Passing an empty list is not appropriate here, we need to find out
                // the list of attributes that go to the child
                val hasAnyChildren = handleChildren(child, emptyList())
                if (!hasAnyChildren) {
                    +child.textContent
                }
            }
        }
    } else {
        val element = globalThis[child.tagName]
        println("From globalThis: ${child.tagName} - " + element)
        ((element.unsafeCast<IntrinsicType<PropsWithClassName>>()) {
            println("attributes: " + child.attributes)
            child.attributes.iterator().forEach { attribute ->
                this.asDynamic()[attribute.name] = (attribute.value)
            }
        })
    }
}

private fun ChildrenBuilder.handleChildren(parent: Element, params: List<String>): Boolean {
    var hasAnyChildren = false
    parent.children.iterator().forEach { grandChild ->
        hasAnyChildren = true
        handleChild(grandChild, params)
    }
    return hasAnyChildren
}

val ktx = KTXInterpolator()

class KtxFC<P : Props>(
    private val block: @ReactDsl ChildrenBuilder.(props: P) -> Unit,
) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): FC<P> = invoke(property.name, block)

    companion object {
        operator fun <P : Props> invoke(name: String, block: @ReactDsl ChildrenBuilder.(props: P) -> Unit): FC<P> =
            FC(block).apply {
                globalThis.asDynamic()[name] = this
            }
    }
}
