## KTX - JSX for Kotlin

__Experimental__ implementation of something similar to JSX/TSX, but for Kotlin JS.

Kotlin's HTML DSLs are extremely nice. But when writing UIs for the web, people are used to HTML.
Not being able to use HTML directly in the code like JSX or TSX offers, is a serious drawback for a lot
of people. So I tried to find a way to offer it in Kotlin JS, so that for example React can be used
more similar to as it is in Javascript projects.

This is what I enabled:

```kotlin
 val CustomButton = KtxFC<Props>("CustomButton") {
     val (count, setCount) = useState(0)

     ktx("<button onClick=${{ setCount(count + 1) }}>$count</button>") ()
 }
 external interface HelloProps : Props {
     var name: String
 }
 val Hello = KtxFC<HelloProps>("Hello") { props ->
     ktx("<div><div>Hello ${props.name}!</div><CustomButton /><CustomButton /><CustomButton /></div>") ()
 }


fun main() {
    createRoot(document.getElementsByTagName("body")[0]).render(
        ktx("<Hello name='you'>").create()
    )
}
```

If you want to know how it's done, please take a look at the [corresponding blog post](https://hannomalie.github.io/posts/posts/ktx.html).
Either that, or you can do yourself a disfavour and look at the main source file, the only source file there is :)

## lit-kt - Lit with Kotlin

A second experiment based on the custom string literals compiler plugin mentioned above is using Lit in Kotlin.

I managed to pull off something like this:

```kotlin
class SimpleNumber : LitElement() {
    var number: Int = 0
        set(value) {
            field = value
            requestUpdate()
        }

    override fun render(): dynamic {
        return _html("<button @click=${ { number++; } }>$number</button>")
    }
}

customElements.define(HtmlTagName("simple-number"), SimpleNumber::class.js)

val result0 = _html("<div>${SimpleNumber(number)}</div>")
val result1 = _html("<div><simple-number></simple-number></div>")
```

I had a lot of issues with the experimental Kotlin compilation to ecma script classes, you can read all the details on
yet another [blog post](https://hannomalie.github.io/posts/posts/lit-kt.html). You can find the code in this repository
here, but on the branch [_lit_](https://github.com/hannomalie/ktx/tree/lit).