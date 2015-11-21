(ns chrome-extensions.background.utils)

(defn- format-js-date
  [date]
  (-> date (.toString) (.slice 0 24)))

(defn stringify
  [js-element]
  (.stringify js/JSON js-element))

(defn logging
  ([name var] (logging name var true))
  ([name var debug] (if debug
                      (println (str "[" (format-js-date (js/Date.)) "] "
                                    name " => " var)))))

(defn error-handler
  [success-message]
  (let [error (.-lastError js/chrome.runtime)]
      (if (nil? error)
        (logging "success" success-message)
        (logging "error" error))))

(defn- update-storage-history-items
  [updated-history-items]
  (.set js/chrome.storage.sync
        (clj->js {:historyItems updated-history-items})
        (error-handler "Items properly saved")))

(defn- treat-retrieved-elements
  [selection-text items]
  (logging "items" (stringify items))
  (let [history-items (aget items "historyItems")
        ; history-items (.-historyItems items)  ;; => Y U DOESN'T WORK???
        updated-history-items (.concat history-items selection-text)]
    (logging "history items" (stringify history-items))
    (logging "updated history items" (stringify updated-history-items))
    (if (nil? (.-history items))
      (update-storage-history-items updated-history-items))))

(defn- retrieve-and-treat-elements
  [selection-text]
  (.get js/chrome.storage.sync
        (clj->js {:history nil :historyItems []})
        (partial treat-retrieved-elements selection-text)))

(defn open-selection-on-google-maps
  [info tab]
  (let [base-url "https://www.google.com/maps?q="
        selection-text (.-selectionText info)
        url (str base-url selection-text)]
    ; (.create js/chrome.tabs #js {:url url})
    (logging "selection object" (stringify info))
    (logging "final url" {:url url})
    (retrieve-and-treat-elements selection-text)))
