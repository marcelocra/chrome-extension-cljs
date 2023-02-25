# chrome-extras-cljs

Features not present in Google Chrome that should be there, as far as I'm
concerned.

## Usage

- Press `alt+shift+d` to detach your current tab from the current window into a new, separate window.
- Select any address and right click it, to be able to send it over to Google Maps easily. You can also see the history of addresses that you searched for (in case you want to).
- (Not working currently) Create your own mappings between words and urls. For this to work, first you need to create your own mappings in Chrome Extras options page. Just type the shortcut you want to use (for example `gg` for google) and then the url it is going to point to `https://google.com`). To use the feature, then, type `x` in Chrome's omnibox (address bar) and type `tab`. Then type the shortcut (`gg` in the example) and hit `enter`. You should be taken to the correct website.

## Installation and Development

- Install [Leiningen][1].
- Run `lein dev-build` if you want to build once, to be able to use the extension
- Run `lein auto-build` if you want to let `cljsbuild` watch for changes and recompile automatically
- Load the folder as a Chrome's unpacked extension.

## License

MIT.

Check the `LICENSE` file.


[1]: http://Leiningen.org
