(require 'cljs.build.api)

(cljs.build.api/watch "src/chrome_extensions/options"
                      {:output-to "out/options/options.js"
                       :optimizations :whitespace
                       :main 'chrome-extensions.background
                       :externs ["chrome_extensions.js"]})
