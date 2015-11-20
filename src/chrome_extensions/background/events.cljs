(ns chrome-extensions.background.events)

(enable-console-print!)

(def constants {:commands {:tab-to-window "print-to-console"}})

;; COMMANDS.
;;
;; Check for commands. If the user press any of the available keyboard
;; shortcuts, process accordingly.

(defn- get-current-tab
  [cb]
  (.query js/chrome.tabs
          #js {:active true :currentWindow true}
          (fn [tabs]
            (cb (first tabs)))))

(defn- toggle-tab-to-window
  []
  (get-current-tab
    (fn [tab]
      (.log js/console (.-url tab)))))

(defn- command?
  [command key]
  (= command (key (:commands constants))))

(defn command-selector
  [command]
  (cond
     (command? command :tab-to-window) (toggle-tab-to-window)))
