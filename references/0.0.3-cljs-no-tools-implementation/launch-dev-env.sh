#!/bin/bash
# Helper utility to launch both watchers at the same time, assuming `tmux`
# is being used.

tmux neww -n watchers 'java -cp src:cljs.jar clojure.main watch-background.clj'
tmux split-window 'java -cp src:cljs.jar clojure.main watch-options.clj'

