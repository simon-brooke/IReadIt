(ns ireadit.env
  (:require [selmer.parser :as parser]
            [clojure.tools.logging :as log]
            [ireadit.dev-middleware :refer [wrap-dev]]))

(def defaults
  {:init
   (fn []
     (parser/cache-off!)
     (log/info "\n-=[ireadit started successfully using the development profile]=-"))
   :stop
   (fn []
     (log/info "\n-=[ireadit has shut down successfully]=-"))
   :middleware wrap-dev})
