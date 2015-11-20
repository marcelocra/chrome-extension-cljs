(ns chrome-extensions.core)

(enable-console-print!)

; (defn command-selector
;   [command]
;   (.log js/console command))

(def constants {:commands {:tab-to-window "print-to-console"}})

(defn command?
  [command key]
  (= command (key (:commands constants))))

(defn command-selector
  [command]
  (cond
     (command? command :tab-to-window) (.log js/console command)))
