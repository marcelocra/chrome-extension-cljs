(ns chrome-extensions.background.background
  (:require [chrome-extensions.background.events :refer [command-selector]]))

(enable-console-print!)

(.addListener js/chrome.commands.onCommand command-selector)
