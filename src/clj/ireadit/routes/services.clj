(ns ireadit.routes.services
  (:require [ring.util.http-response :refer :all]
            [cemerick.url :refer (url-decode)]
            [compojure.api.sweet :refer :all]
            [ireadit.tesseractor :refer [ocr]]
            [schema.core :as s]))

(def service-routes
  (api
   {:swagger {:ui "/swagger-ui"
              :spec "/swagger.json"
              :data {:info {:version "1.0.0"
                            :title "Sample API"
                            :description "Sample Services"}}}}

   (context "/api" []
            :tags ["tesseractor"]

            (POST "/ocr/:uri" []
                  :return       String
                  :path-params [uri :- String]
                  (ocr (url-decode uri))))))
