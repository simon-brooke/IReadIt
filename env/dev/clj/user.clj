(ns user
  (:require [ireadit.config :refer [env]]
            [clojure.spec.alpha :as s]
            [expound.alpha :as expound]
            [mount.core :as mount]
            [ireadit.figwheel :refer [start-fw stop-fw cljs]]
            [ireadit.core :refer [start-app]]))

(alter-var-root #'s/*explain-out* (constantly expound/printer))

(defn start []
  (mount/start-without #'ireadit.core/repl-server))

(defn stop []
  (mount/stop-except #'ireadit.core/repl-server))

(defn restart []
  (stop)
  (start))


