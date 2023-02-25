(require 'cljs.build.api)

(cljs.build.api/watch "src/chrome_extensions/background"
                      {:output-dir "out/background/"
                       :output-to "out/background/background.js"
                       :optimizations :advanced
                      ;  :optimizations :whitespace
                       :pretty-print true
                      ;  :pseudo-names true
                      ;  :static-fns true
                       :verbose true
                       :source-map "out/background/background.js.map"
                       :main 'chrome-extensions.background.background
                       :externs ["chrome_extensions.js"]})
