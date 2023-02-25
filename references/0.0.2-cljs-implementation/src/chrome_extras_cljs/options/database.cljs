(ns chrome-extras-cljs.options.database)

(enable-console-print!)

(def initial-value {:history       true
                    :history-items []})

(def app-state (atom initial-value))

(defn c-get
  ([key cb]
   (.get js/chrome.storage.sync
         key
         (fn [items]
           (cb (js->clj (aget items key))))))
  ([key default cb]
   (.get js/chrome.storage.sync
         (clj->js {key default})
         (fn [items]
           (cb (js->clj (aget items key)))))))

(defn c-set [key value]
  (.set js/chrome.storage.sync
        (clj->js {key value})))

(defn c-remove [korks cb]
  (.remove js/chrome.storage.sync
           (clj->js korks)
           (fn [] (cb))))

;; Load the information as soon as the file is loaded.

(c-get "history"
       (fn [hist]
         (swap! app-state assoc :history hist)))
(c-get "historyItems"
       []
       (fn [items]
         (println "history items => " items)
         (println "history items type => " (type items))
         (swap! app-state assoc :history-items (concat (:history-items @app-state) items))))