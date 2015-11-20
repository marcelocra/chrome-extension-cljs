(ns chrome-extensions.background.background
  (:require [chrome-extensions.background.events :refer [constants
                                                         command-selector
                                                         alarm-selector]]
            [chrome-extensions.background.utils :refer [open-selection-on-google-maps]]))

(enable-console-print!)

;; Add listeners.
(.addListener js/chrome.alarms.onAlarm alarm-selector)
(.addListener js/chrome.commands.onCommand command-selector)

;; Wire up the main parts.
(.create js/chrome.alarms (:initialize-history (:alarms constants))
                          (clj->js {:when (.now js/Date)}))
(.create js/chrome.contextMenus (clj->js {:title    "Open in Google Maps"
                                          :contexts ["selection"]
                                          :onclick  open-selection-on-google-maps}))
