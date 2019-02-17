(ns ireadit.env
  (:require [clojure.tools.logging :as log]))

(def defaults
  {:init
   (fn []
     (log/info "\n-=[ireadit started successfully]=-"))
   :stop
   (fn []
     (log/info "\n-=[ireadit has shut down successfully]=-"))
   :middleware identity})
