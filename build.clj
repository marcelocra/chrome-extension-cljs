(require 'cljs.build.api)

(cljs.build.api/build "src"
                      {:output-to "out/background.js"
                       :optimizations :advanced
                       :main 'chrome-extensions.background
                       :externs ["chrome_extensions.js"]})
