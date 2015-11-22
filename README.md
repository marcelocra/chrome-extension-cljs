# An example Chrome extension built using ClojureScript

This is an attempt to create a Chrome extension using ClojureScript.
I started using `lein` and `cljsbuild` but had lots of problems, most likely due
to me being a beginner in ClojureScript, and so I decided to start from scratch.

The main guide is the ClojureScript [Quickstart][1].

So far I had some problems, which I'll list below, along with the solution, in
case I have already found one.

Problems:

+ Error when compiling with `:advanced` while using `lein` and `cljsbuild` (take
  a look [here][2], for details).
  - **Solution**: don't have any yet. Once I go back to using those tools I'll look
  further into that.
+ `:advanced` compilation mangling names of storage keys created by me.
  - **Solution**: check the [related question and answer][3] on StackOverflow.


Indeed it looks like my current workflow will be improved once I go back to
`lein` and `cljsbuild`, but I won't be doing that right away. Still want to deal
with less stuff so I can focus on the language.

[1]: https://github.com/clojure/clojurescript/wiki/Quick-Start
[2]: https://groups.google.com/forum/#!topic/clojurescript/XlBibYpA344
[3]: http://stackoverflow.com/q/33831723/1814970
