(ns chrome-extensions.background.events
  (:require [chrome-extensions.background.utils :refer [logging
                                                        error-handler
                                                        stringify]]))

(enable-console-print!)

(def constants {:commands {:tab-to-window "print-to-console"
                           :print-history-items "print-history-items"}
                :alarms {:initialize-history "initialize-history"}
                :url-mappings {:google "https://www.google.com"}})
                               

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
     (command? command
               :tab-to-window) (toggle-tab-to-window)
     (command? command
               :print-history-items) (.get js/chrome.storage.sync
                                           "historyItems"
                                           (fn [items]
                                             (logging "historyItems" (.-historyItems items))))))

;; ALARMS.
;;
;; Check for alarms. If alarms are triggered, filter them here.

(defn- initialize-history-items
  []
  (.get js/chrome.storage.sync
        "historyItems"
        (fn [items]
          (if (nil? (.-historyItems items))
            (.set js/chrome.storage.sync
                  (clj->js {:historyItems []})
                  (error-handler "Successfully initialized :historyItems"))))))

(defn- alarm?
  [alarm key]
  (= (.-name alarm) (key (:alarms constants))))

(defn alarm-selector
  [alarm]
  (cond
    (alarm? alarm :initialize-history) (initialize-history-items)))

;; OMNIBOX.
;;
;; Check for mappings provided via omnibox.

(defn omnibox-url-selector [text diposition]
  (logging "text" (stringify text))
  (logging "disposition" (stringify disposition))
  (.create js/chrome.tabs #js {:url ((keyword text) (:url-mappings constants))}))
