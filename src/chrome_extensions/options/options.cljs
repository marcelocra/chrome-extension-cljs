(ns chrome-extensions.options.options
  (:require [chrome-extensions.background.events :refer [constants]]
            [chrome-extensions.background.utils :refer [stringify logging]]))

(defn- user-feedback
  [message]
  (fn []
    (let [user-feedback-elem (.getElementById js/document "user-feedback")
          last-error (.-lastError js/chrome.runtime)]
      (if (nil? last-error)
        (do
          (set! (.-textContent user-feedback-elem) message)
          (.setTimeout js/window #(set! (.-textContent user-feedback-elem) "") 750))
        (set! (.-textContent user-feedback-elem) last-error)))))


(defn- restore-options
  []
  (.get js/chrome.storage.sync
        (clj->js ["history" "historyItems"])
        (fn [items]
          (set! (.-checked (.getElementById js/document "history"))
                (aget items "history"))
          (let [history-items (aget items "historyItems")
                doc js/document
                ul (.createElement doc "ul")
                dom-saved-items (.getElementById doc "saved-items")
                li-items (map (fn [item]
                                (let [li (.createElement doc "li")]
                                  (.appendChild li (.createTextNode doc item))
                                  li))
                              history-items)]
            (logging "li-items" (stringify li-items))
            (doseq [li-item li-items]
              (.appendChild ul li-item))
            (.appendChild dom-saved-items ul)))))

(defn- save-options
  []
  (.set js/chrome.storage.sync
        #js {:history (-> js/document
                        (.getElementById "history")
                        (.-checked))}
        (user-feedback "Options saved")))

(defn- clear-history
  []
  (.remove js/chrome.storage.sync
           #js ["history" "historyItems"]
           (user-feedback "History removed"))
  (let [doc js/document]
    (-> doc
        (.getElementById "history")
        (.-checked)
        (set! false))
    (-> doc
        (.getElementById "history")
        (.-innerHTML)
        (set! ""))))

(let [doc js/document]
  (.addEventListener doc "DOMContentLoaded" restore-options)
  (-> doc
      (.getElementById "save")
      (.addEventListener "click" save-options))
  (-> doc
      (.getElementById "clear-history")
      (.addEventListener "click" clear-history)))
