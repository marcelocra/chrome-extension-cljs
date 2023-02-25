(defproject chrome-extras-cljs "0.1.0-SNAPSHOT"
  :description "Chrome Extension that includes extra functionality in Chrome."
  :url "https://github.com/marcelocra/chrome-extras-cljs"
  :license {:name         "MIT License"
            :url          "http://opensource.org/licenses/MIT"
            :distribution :repo}

  :dependencies [[org.clojure/clojure "1.7.0"]
                 [org.clojure/clojurescript "1.7.170"]
                 [org.clojure/core.async "0.2.374"]
                 [sablono "0.3.6"]
                 [org.omcljs/om "0.9.0"]]

  :plugins [[lein-cljsbuild "1.1.1"]
            [lein-figwheel "0.5.0-1"]]

  :source-paths ["src"]

  :clean-targets ^{:protect false} ["target"
                                    "resources/public/js/compiled"]


  :profiles {:devpack
             {:cljsbuild {:builds
                          {:background
                           {:source-paths ["src/chrome_extras_cljs/background"]
                            :compiler     {:output-to             "resources/public/js/compiled/background/background.js"
                                           :output-dir            "resources/public/js/compiled/background"
                                           :asset-path            "js/compiled/background"
                                           :externs               ["resources/public/js/chrome_extensions.js"]
                                           :optimizations         :whitespace
                                           :anon-fn-naming-policy :unmapped
                                           :main                  chrome-extras-cljs.background.background
                                           :compiler-stats        true
                                           :cache-analysis        true
                                           :pretty-print          true
                                           :verbose               true
                                           :source-map            "resources/public/js/compiled/background/background.js.map"
                                           :source-map-timestamp  true}}
                           :options
                           {:source-paths ["src/chrome_extras_cljs/options"]
                            :compiler     {:output-to             "resources/public/js/compiled/options/options.js"
                                           :output-dir            "resources/public/js/compiled/options"
                                           :asset-path            "js/compiled/options"
                                           :externs               ["resources/public/js/chrome_extensions.js"]
                                           :optimizations         :whitespace
                                           :anon-fn-naming-policy :unmapped
                                           :main                  chrome-extras-cljs.options.options
                                           :compiler-stats        true
                                           :cache-analysis        true
                                           :pretty-print          true
                                           :verbose               true
                                           :source-map            "resources/public/js/compiled/options/options.js.map"
                                           :source-map-timestamp  true}}
                           :content-script
                           {:source-paths ["src/chrome_extras_cljs/content_script"]
                            :compiler     {:output-to             "resources/public/js/compiled/content_script/content_script.js"
                                           :output-dir            "resources/public/js/compiled/content_script"
                                           :asset-path            "js/compiled/content_script"
                                           :externs               ["resources/public/js/chrome_extensions.js"]
                                           :optimizations         :whitespace
                                           :anon-fn-naming-policy :unmapped
                                           :main                  chrome-extras-cljs.content-script.content-script
                                           :compiler-stats        true
                                           :cache-analysis        true
                                           :pretty-print          true
                                           :verbose               true
                                           :source-map            "resources/public/js/compiled/content_script/content_script.js.map"
                                           :source-map-timestamp  true}}}}}

             :release
             {:cljsbuild {:builds
                          {:background
                           {:source-paths ["src/chrome_extras_cljs/background"]
                            :compiler     {:output-to      "resources/public/js/compiled/background/background.js"
                                           :output-dir     "resources/public/js/compiled/background"
                                           :asset-path     "js/compiled/background"
                                           :externs        ["resources/public/js/chrome_extensions.js"]
                                           :main           chrome-extras-cljs.background.background
                                           :optimizations  :advanced
                                           :compiler-stats true
                                           :elide-asserts  true}}
                           :options
                           {:source-paths ["src/chrome_extras_cljs/options"]
                            :compiler     {:output-to      "resources/public/js/compiled/options/options.js"
                                           :output-dir     "resources/public/js/compiled/options"
                                           :asset-path     "js/compiled/options"
                                           :externs        ["resources/public/js/chrome_extensions.js"]
                                           :main           chrome-extras-cljs.options.options
                                           :optimizations  :advanced
                                           :compiler-stats true
                                           :elide-asserts  true}}
                           :content-script
                           {:source-paths ["src/chrome_extras_cljs/content_script"]
                            :compiler     {:output-to      "resources/public/js/compiled/content_script/content_script.js"
                                           :output-dir     "resources/public/js/compiled/content_script"
                                           :asset-path     "js/compiled/content_script"
                                           :externs        ["resources/public/js/chrome_extensions.js"]
                                           :main           chrome-extras-cljs.content-script.content-script
                                           :optimizations  :advanced
                                           :compiler-stats true
                                           :elide-asserts  true}}}}}}

  :aliases {"dev-build"  ["with-profile" "+devpack" "do" "clean," "cljsbuild" "once" "background" "options" "content-script"]
            "auto-build" ["with-profile" "+devpack" "cljsbuild" "auto" "background" "options" "content-script"]
            "release"    ["with-profile" "+release" "do" "clean," "cljsbuild" "once" "background" "options" "content-script"]})
