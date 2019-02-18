(ns ireadit.routes.services
  (:require [cemerick.url :refer (url-decode)]
            [clojure.tools.logging :as log]
            [compojure.api.sweet :refer :all]
            [ireadit.tesseractor :refer [ocr]]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

(def service-routes
  (api
   {:swagger {:ui "/swagger-ui"
              :spec "/swagger.json"
              :data {:info {:version "1.0.0"
                            :title "I Read It API"
                            :description "Meme transcription services"}}}}

   (context "/api" []
            :tags ["tesseractor"]

            (POST "/ocr/:uri" []
                  :return       String
                  :path-params [uri :- String]
                  (ok (let [decoded (url-decode uri)
                            text (ocr decoded)]
                        (log/info (str "ocr '" decoded "' => '" text "'"))
                        text))))))
