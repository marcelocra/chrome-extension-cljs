(ns chrome-extensions.background.events
  (:require [chrome-extensions.background.utils :refer [logging
                                                        error-handler
                                                        stringify]]
            [goog.object]))
(enable-console-print!)

(def constants (atom {:commands {:toggle-tab-to-window "toggle-tab-to-window"
                                 :print-storage "print-storage"}
                      :dispositions {:current-tab "currentTab"
                                     :foreground-tab "foregroundTab"
                                     :background-tab "backgroundTab"}
                      :identifiers {:tab-ids "tabsIds"}
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

(defn- get-and-update-tab-position
  [tab]
  (let [tab-ids-key (:tab-ids (:identifiers @constants))]
    (js/chrome.storage.local.get
      (clj->js {tab-ids-key {}})
      (fn [items]
        (let [tabs (js->clj (aget items tab-ids-key))
              curr-tab (get tabs (str (.-id tab)))]
          (logging "tabs" tabs)
          (logging "curr-tab" curr-tab)
          (if (nil? curr-tab)
            (do (js/chrome.windows.create #js {:tabId (.-id tab)})
                (js/chrome.storage.local.set (clj->js {tab-ids-key (assoc tabs (str (.-id tab)) tab)})))
            (do (js/chrome.tabs.move (get curr-tab "id")
                                     #js {:windowId (get curr-tab "windowId")
                                          :index (get curr-tab "index")}
                                     (fn [moved-tab]
                                       (js/chrome.tabs.update (.-id moved-tab)
                                                              #js {:active true})))
                (js/chrome.storage.local.set (clj->js {tab-ids-key (dissoc tabs (str (get curr-tab "id")))})))))))))


(defn- toggle-tab-to-window
  []
  (get-current-tab get-and-update-tab-position))

(defn- command?
  [command k]
  (logging "command triggered" command)
  (logging "command key to compare" k)
  (= command (k (:commands @constants))))

(defn command-selector
  [command]
  (cond
     (command? command
               :toggle-tab-to-window) (toggle-tab-to-window)
     (command? command
               :print-storage) (.get js/chrome.storage.sync
                                     "historyItems"
                                     (fn [items]
                                       (logging "historyItems" (aget items "historyItems"))))))

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
