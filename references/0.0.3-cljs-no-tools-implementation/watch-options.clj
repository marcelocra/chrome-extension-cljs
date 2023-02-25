(require 'cljs.build.api)

(cljs.build.api/watch "src/chrome_extensions/options"
                      {:output-dir "out/options/"
                       :output-to "out/options/options.js"
                       :optimizations :advanced
                      ;  :optimizations :whitespace
                       :pretty-print true
                      ;  :pseudo-names true
                      ;  :static-fns true
                       :verbose true
                       :source-map "out/options/options.js.map"
                       :main 'chrome-extensions.options.options
                       :externs ["chrome_extensions.js"]})
