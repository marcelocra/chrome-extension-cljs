(ns chrome-extensions.background
  (:require [chrome-extensions.core :refer [command-selector]]))

(enable-console-print!)

(.addListener js/chrome.commands.onCommand command-selector)
