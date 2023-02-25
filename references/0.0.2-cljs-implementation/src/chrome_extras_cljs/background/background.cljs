(ns chrome-extras-cljs.background.background
  (:require [chrome-extras-cljs.background.events :refer [constants
                                                          command-selector-router
                                                          omnibox-url-selector-router]]
            [chrome-extras-cljs.background.utils :refer [open-selection-on-google-maps
                                                         logging]]))

(enable-console-print!)

(logging "Loaded!")

;; Add listeners.
(.addListener js/chrome.commands.onCommand command-selector-router)
(.addListener js/chrome.omnibox.onInputEntered omnibox-url-selector-router)

;; Wire up the main parts.
(.create js/chrome.contextMenus (clj->js {:title    "Open in Google Maps"
                                          :contexts ["selection"]
                                          :onclick  open-selection-on-google-maps}))
