(require 'cljs.build.api)

(cljs.build.api/watch "src/chrome_extensions/options"
                      {:output-to "out/options/options.js"
                       :optimizations :advanced
                       :main 'chrome-extensions.background
                       :externs ["chrome_extensions.js"]})
