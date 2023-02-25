If you are planning to run this code, you'll need a copy of the
`chrome_extensions.js` extern that you can find in Google Closure repo. Take a
look [here](https://stackoverflow.com/q/17757898/1814970) for some details.

# An example Chrome extension built using ClojureScript

This is an attempt to create a Chrome extension using ClojureScript. I started
using `lein` and `cljsbuild` but had lots of problems, most likely due to me
being a beginner in ClojureScript, and so I decided to start from scratch.

The main guide is the ClojureScript [Quickstart][1].

So far I had some problems, which I'll list below, along with the solution, in
case I have already found one.

Problems:

- Error when compiling with `:advanced` while using `lein` and `cljsbuild` (take
  a look [here][2], for details).
  - **Solution**: don't have any yet. Once I go back to using those tools I'll
    look further into that.
- `:advanced` compilation mangling names of storage keys created by me.
  - **Solution**: check the [related question and answer][3] on StackOverflow.
- Sometimes Chrome commands' `suggested_key` simply doesn't work. If you already
  set the keys in your manifest and it is not working, try deleting the
  extension and adding it back again.

Indeed it looks like my current workflow will be improved once I go back to
`lein` and `cljsbuild`, but I won't be doing that right away. Still want to deal
with less stuff so I can focus on the language.

## Installation

#### For development

1. Download the [`cljs.jar`][4] to your project folder.
1. If you are using `tmux`, use the helper script to launch both watchers at the
   same time.

   `$ ./launch-dev-env.sh`

   Otherwise, run each script independently, in separate terminal windows:

   - `java -cp src:cljs.jar clojure.main watch-background.clj` generates the
     `out/background/background.js` file.
   - `java -cp src:cljs.jar clojure.main watch-options.clj` generates the
     `out/options/options.js` file.

1. Load your folder in Chrome using the unpacked extensions support.

#### For use

Clone this repository or download and unpack the zip file to a folder and load
the folder in Chrome, using the unpacked extension support from the developer
mode.

## Development environment

To be able to use the language without the need to spend hours setting up a
complex dev environment, just grab `Atom Editor` and the `parinfer` plugin. This
is a great combination to start (and maybe later too).

## A simpler version of this project, if you are starting

Please, take a look [here][5] for the very first functional version of this
extension. It might be a good place for you to start, in case you care only
about the minimal working version.

[1]: https://github.com/clojure/clojurescript/wiki/Quick-Start
[2]: https://groups.google.com/forum/#!topic/clojurescript/XlBibYpA344
[3]: http://stackoverflow.com/q/33831723/1814970
[4]:
  https://github.com/clojure/clojurescript/releases/download/r1.7.170/cljs.jar
[5]: https://github.com/marcelocra/chrome-extension-cljs-example/tree/bare-bones
