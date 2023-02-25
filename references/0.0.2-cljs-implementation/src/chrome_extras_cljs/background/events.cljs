(ns chrome-extras-cljs.background.events
  (:require [chrome-extras-cljs.background.utils :refer [logging
                                                         error-handler
                                                         stringify]]))

(enable-console-print!)

(def constants (atom {:identifiers  {:tab-ids "tabsIds"
                                     :tab-list "tabList"}
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
    (.get js/chrome.storage.local
          (clj->js {tab-ids-key {}})
          (fn [items]
            (let [tabs (js->clj (aget items tab-ids-key))
                  tab-id (.-id tab)
                  tab-id-str (str tab-id)
                  ;; |tabs| is now a cljs map, with a string of the id of the tab
                  ;; as a key, since js only supports strings as object key.
                  curr-tab (get tabs tab-id-str)
                  curr-tab-id (get curr-tab "id")]
              (logging "tabs" tabs)
              (logging "curr-tab" curr-tab)
              (if (nil? curr-tab)
                (do (js/chrome.windows.create #js {:tabId tab-id})
                    (.set js/chrome.storage.local (clj->js {tab-ids-key (assoc tabs tab-id-str tab)})))
                (do (js/chrome.tabs.move curr-tab-id
                                         #js {:windowId (get curr-tab "windowId")
                                              :index    (get curr-tab "index")}
                                         (fn [moved-tab]
                                           (js/chrome.tabs.update (.-id moved-tab)
                                                                  #js {:active true})))
                    (.set js/chrome.storage.local (clj->js {tab-ids-key (dissoc tabs (str curr-tab-id))})))))))))

(defn- get-highlighted-tabs
  [cb]
  (.query js/chrome.tabs
          #js {:highlighted true :active true :currentWindow true}
          (fn [tabs]
            (cb tabs))))

(defn- get-and-update-highlighted-tabs-position
  [tabs]
  (let [tab-list-key (:tab-list (:identifiers @constants))]
    (.get js/chrome.storage.local
          (clj->js {tab-list-key nil})
          (fn [items]
            (let [old-window-id (js->clj (aget items tab-list-key))
                  tab-url-vec (map #(.-url %) tabs)
                  tab-id-vec (map #(.-id %) tabs)]
              (if (nil? old-window-id)
                (do (js/chrome.windows.create (clj->js {:url tab-url-vec}))
                    (.set js/chrome.storage.local (clj->js {tab-list-key (.-windowId (first tabs))})))
                (do (js/chrome.tabs.move tab-id-vec
                                         #js {:windowId old-window-id})
                    (.set js/chrome.storage.local (clj->js {tab-list-key nil})))))))))

;(defn- toggle-tab-to-window
;  []
;  (get-current-tab get-and-update-tab-position))

(defn- toggle-tab-to-window
  []
  (get-highlighted-tabs get-and-update-highlighted-tabs-position))

(defmulti command-selector
          (fn [command]
            (do
              (logging "command triggered" command)
              (keyword command))))

(defmethod command-selector :default
  [_]
  (logging "No command matched"))

(defmethod command-selector :toggle-tab-to-window
  [_]
  (toggle-tab-to-window))

(defmethod command-selector :print-storage
  [_]
  ;; Prints any element that is being saved to storage.
  (let [element-to-print "historyItems"]
    (.get js/chrome.storage.sync
          element-to-print
          (fn [items]
            (logging element-to-print (aget items element-to-print))))))

(defn command-selector-router
  [command]
  (command-selector command))

;; OMNIBOX.
;;
;; Check for mappings provided via omnibox.

(defn- fetch-url-for-text
  [text]
  ((keyword text) (:url-mappings @constants)))

(defmulti omnibox-url-selector
          (fn [text disposition]
            (do
              (logging "text" (stringify text))
              (logging "disposition" (stringify disposition))
              disposition)))

(defmethod omnibox-url-selector :default
  [_ _]
  (logging "No disposition matched"))

;; TODO: change the key here to "foregroundTab" once there is support for that.
;; Now it just creates a new tab next to the current one.
(defmethod omnibox-url-selector "currentTab"
  [text _]
  (get-current-tab
    (fn [tab]
      (when-let [url (fetch-url-for-text text)]
        (let [options {:url url}]
          (.create js/chrome.tabs
                   (clj->js (assoc options :active true :index (+ 1 (.-index tab))))))))))

;; TODO: uncomment this once the previous TODO is fixed.
;; Not supposed to get here. This is the desired behavior once there is
;; support for choosing either current, foreground or background tabs.
; (defmethod omnibox-url-selector "currentTab"
;   [text _]
;   (when-let [url (fetch-url-for-text text)]
;     (let [options {:url url}]
;       (.update js/chrome.tabs (clj->js options)))))

(defn omnibox-url-selector-router
  [text disposition]
  (omnibox-url-selector text disposition))
