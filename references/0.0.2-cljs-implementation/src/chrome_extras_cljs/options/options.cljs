(ns chrome-extras-cljs.options.options
  (:require [chrome-extras-cljs.background.events :refer [constants]]
            [chrome-extras-cljs.background.utils :refer [stringify logging]]
            [chrome-extras-cljs.options.database :refer [app-state initial-value] :as db]
            [om.core :as om :include-macros true]
            [sablono.core :as html :refer-macros [html]]
            [clojure.string :as s]))

(defn- user-feedback
  [message]
  (let [user-feedback-elem (.getElementById js/document "user-feedback")
        last-error (.-lastError js/chrome.runtime)]
    (if (nil? last-error)
      (do
        (set! (.-textContent user-feedback-elem) message)
        (.setTimeout js/window #(set! (.-textContent user-feedback-elem) "") 750))
      (set! (.-textContent user-feedback-elem) last-error))))

(defn headers [{:keys [name class] :as data} owner]
  (reify
    om/IRender
    (render [this]
      (html
        (let [base-option {:role "presentation"}]
          [:li (if (not class)
                 base-option
                 (assoc base-option :class class))
           [:a
            {:href          (str "#" name "-tab")
             :aria-controls (str name "-tab")
             :role          "tab"
             :data-toggle   "tab"}
            (s/capitalize name)]])))))

(defn home-tab [data owner]
  (reify
    om/IRender
    (render [this]
      (html
        [:div {:role "tabpanel" :class "tab-pane active" :id "home-tab"}
         [:p "Chrome Extras!"]
         [:div
          [:p "What can you do with this?"]
          [:ul
           [:li "Search things in Google Maps by selecting the text, right clicking it and choosing
                         'Open in Google Maps'"]
           [:li "Check the history of all searches you did with the extension"]
           [:li "Choose whether you want to keep your history saved"]
           [:li "Clear all your history"]
           [:li "Detach the current tab from the current window just by pressing 'alt+shift+d'
                         and attach it back in the same position pressing the shortcut again"]]]]))))

(defn history-tab [data owner]
  (reify
    om/IRender
    (render [this]
      (html
        [:div {:role "tabpanel" :class "tab-pane" :id "history-tab"}
         [:p "Extension options:"]
         [:div {:class "input-group"}
          [:span
           [:input
            {:type    "checkbox"
             :id      "history"
             :checked (:history data)
             :onClick (fn [] (om/transact! data
                                           :history
                                           (fn [curr-value]
                                             (let [new-value (not curr-value)]
                                               (db/c-set "history" new-value)
                                               (user-feedback
                                                 (str "History status: "
                                                      (if new-value
                                                        "saving"
                                                        "not saving")))
                                               new-value))))}
            "Save history of searches"]]]
         [:div
          [:p "Saved items"]
          [:div {:id "saved-items"}
           (into [:ul] (map (fn [e] [:li e]) (:history-items data)))]]]))))

(defn danger-tab [data owner]
  (reify
    om/IRender
    (render [this]
      (html
        [:div {:role "tabpanel" :class "tab-pane" :id "danger-tab"}
         [:div
          [:p "Danger zone!"]
          [:button
           {:id      "clear-history"
            :class   "btn btn-default"
            :onClick (fn [] (do
                              (db/c-remove ["history" "historyItems"]
                                           (user-feedback "History removed"))
                              (reset! app-state initial-value)))}
           "Clear all history"]]]))))

(defn widget [data owner]
  (reify
    om/IRender
    (render [_]
      (html
        (let [show-home true]
          [:div {:class "main"}
           ;; Tabs in the header of the HTML.
           [:ul {:class "nav nav-tabs" :role "tablist"}
            (om/build-all headers [{:name "home" :class "active"}
                                   {:name "history"}
                                   {:name "danger"}])]

           ;; Div with the content of the tabs.
           [:div {:class "tab-content"}
            (om/build home-tab data)
            (om/build history-tab data)
            (om/build danger-tab data)]

           ;; Show user feedback here.
           [:div {:id "user-feedback"}]])))))

(om/root
  widget
  app-state
  {:target (. js/document (getElementById "app"))})

;; Receives messages containing the text used for the search. This text is added to
;; the history, should the history be saved.
(.addListener js/chrome.runtime.onMessage
              (fn [request sender send-response]
                (if (:history @app-state)
                  (let [hist-items (:history-items @app-state)
                        updated-hist-items (conj hist-items (aget request "selection-text"))]
                    (logging "initial history items" hist-items)
                    (logging "updated history items" updated-hist-items)
                    (swap! app-state assoc :history-items updated-hist-items)
                    (db/c-set "historyItems" updated-hist-items))
                  (logging "Didn't save history items"))))
