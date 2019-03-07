(ns ireadit.routes.services
  (:require [cemerick.url :refer (url-decode)]
            [clojure.tools.logging :as log]
            [compojure.api.sweet :refer :all]
            [ireadit.tesseractor :refer [ocr]]
            [ring.util.http-response :refer :all]
            [schema.core :as s]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;
;;;; ireadit.routes.services: JSON API.
;;;;
;;;; This program is free software; you can redistribute it and/or
;;;; modify it under the terms of the GNU General Public License
;;;; as published by the Free Software Foundation; either version 2
;;;; of the License, or (at your option) any later version.
;;;;
;;;; This program is distributed in the hope that it will be useful,
;;;; but WITHOUT ANY WARRANTY; without even the implied warranty of
;;;; MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
;;;; GNU General Public License for more details.
;;;;
;;;; You should have received a copy of the GNU General Public License
;;;; along with this program; if not, write to the Free Software
;;;; Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
;;;; USA.
;;;;
;;;; Copyright (C) 2019 Simon Brooke
;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;


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
