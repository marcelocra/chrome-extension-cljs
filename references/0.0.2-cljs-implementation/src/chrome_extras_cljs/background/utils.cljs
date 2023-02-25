(ns chrome-extras-cljs.background.utils
  (:require [chrome-extras-cljs.options.database :refer [app-state]]))

(defn- format-js-date
  "Properly formats dates, as such: 'Sat Nov 24 2015 00:00:00'."
  [date]
  (-> date (.toString) (.slice 0 24)))

(defn stringify
  "Makes a json element of the given JavaScript element."
  [js-element]
  (.stringify js/JSON js-element))

;; Define if logs should be printed or not.
(def ^:private debug? true)

(defn logging
  "Logs the given variable, along with the helper text."
  ([helper-text] (logging helper-text nil debug?))
  ([helper-text var] (logging helper-text var debug?))
  ([helper-text var debug] (if debug
                             (println (str "[" (format-js-date (js/Date.)) "] "
                                           helper-text
                                           (if var
                                             (str " => " var)))))))

(defn error-handler
  "Retrieve and print Chrome's API last error, in case it exists, or the given
  success message."
  [success-message]
  (let [error (.-lastError js/chrome.runtime)]
    (if (nil? error)
      (logging "success" success-message)
      (logging "error" error))))

(defn- maybe-save-elements
  [selection-text]
  (.sendMessage js/chrome.runtime #js {"selection-text" selection-text}))

(defn open-selection-on-google-maps
  [info tab]
  (let [base-url "https://www.google.com/maps?q="
        selection-text (.-selectionText info)
        url (str base-url selection-text)]
    ;(.create js/chrome.tabs #js {:url url})
    (logging "selection object" (stringify info))
    (logging "final url" {:url url})
    (maybe-save-elements selection-text)))
