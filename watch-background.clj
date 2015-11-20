(require 'cljs.build.api)

(cljs.build.api/watch "src/chrome_extensions/background"
                      {:output-to "out/background/background.js"
                       :optimizations :advanced
                       :main 'chrome-extensions.background.background
                       :externs ["chrome_extensions.js"]})
