(ns chrome-extensions.background.events
  (:require [chrome-extensions.background.utils :refer [logging
                                                        error-handler
                                                        stringify]]))

(enable-console-print!)

(def constants (atom {:commands {:tab-to-window "print-to-console"
                                 :print-history-items "print-history-items"}
                      :alarms {:initialize-history "initialize-history"}
                      :dispositions {:current-tab "currentTab"
                                     :foreground-tab "foregroundTab"
                                     :background-tab "backgroundTab"}
                      :url-mappings {:google "https://www.google.com"}}))

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
  [command k]
  (logging "command triggered" command)
  (logging "command key to compare" k)
  (= command (k (:commands @constants))))

(defn command-selector
  [command]
  (cond
     (command? command
               :tab-to-window) (toggle-tab-to-window)
     (command? command
               :print-history-items) (.get js/chrome.storage.sync
                                           "historyItems"
                                           (fn [items]
                                             (logging "historyItems" (aget items "historyItems"))))))

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
  [alarm k]
  (logging "triggered alarm" (.-name alarm))
  (logging "alarm key to compare" k)
  (= (.-name alarm) (k (:alarms @constants))))

(defn alarm-selector
  [alarm]
  (cond
    (alarm? alarm :initialize-history) (initialize-history-items)))

;; OMNIBOX.
;;
;; Check for mappings provided via omnibox.

(defn- fetch-url-for-text
  [text]
  ((keyword text) (:url-mappings @constants)))

(defn- disposition-match-with-key?
  [disposition k]
  (logging "disposition" disposition)
  (logging "disposition key to compare" k)
  (= disposition (k (:dispositions @constants))))

(defn omnibox-url-selector
  [text disposition]
  (logging "text" (stringify text))
  (logging "disposition" (stringify disposition))
  (when-let [url (fetch-url-for-text text)]
    (let [disposition-for-key (partial disposition-match-with-key? disposition)
          options {:url url}]
      (cond
        ;; TODO: change the key here to :foreground-tab once there is support
        ;; for that. Now it just creates a new tab next to the current one.
        (disposition-for-key :current-tab) (get-current-tab
                                                (fn [tab]
                                                  (.create js/chrome.tabs
                                                           (clj->js (assoc options :active true :index (+ 1 (.-index tab)))))))
        ;; TODO: fix this once the previous TODO is fixed.
        ;; Not supposed to get here. This is the desired behavior once there is
        ;; support for choosing either current, foreground or background tabs.
        (disposition-for-key :current-tab) (.update js/chrome.tabs (clj->js options))))))
