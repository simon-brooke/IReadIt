(ns ireadit.events
  (:require [re-frame.core :as rf]
            [ajax.core :as ajax]
            [cemerick.url :refer (url-encode)]))

;;dispatchers

(rf/reg-event-db
 :set-url
 (fn [db [_ url]]
   (assoc db :url url)))

(rf/reg-event-db
  :navigate
  (fn [db [_ page]]
    (assoc db :page page)))

(rf/reg-event-db
  :set-docs
  (fn [db [_ docs]]
    (assoc db :docs docs)))

(rf/reg-event-db
  :set-transcription
  (fn [db [_ transcription]]
    (assoc db :transcription transcription)))

(rf/reg-event-fx
  :fetch-docs
  (fn [_ _]
    {:http-xhrio {:method          :get
                  :uri             "/docs"
                  :response-format (ajax/raw-response-format)
                  :on-success       [:set-docs]}}))

(rf/reg-event-fx
 :fetch-transcription
 (fn [{db :db} _]
   (let [uri (str "/api/ocr/" (url-encode (:url db)))]
     (js/console.log
      (str
       "Fetching transcription data: " uri))
     {:http-xhrio {:method          :post
                   :uri             uri
                   :format          (ajax/json-request-format)
                   :response-format (ajax/json-response-format {:keywords? true})
                   :on-success      [:set-transcription]
                   :on-failure      [:bad-transcription]}})))

(rf/reg-event-fx
  :bad-transcription
  (fn
    [{db :db} [_ response]]
    ;; TODO: signal something has failed? It doesn't matter very much, unless it keeps failing.
    (js/console.log (str "Failed to fetch transcription data" response))))

(rf/reg-event-db
  :common/set-error
  (fn [db [_ error]]
    (assoc db :common/error error)))

;;subscriptions

(rf/reg-sub
  :page
  (fn [db _]
    (:page db)))

(rf/reg-sub
  :docs
  (fn [db _]
    (:docs db)))

(rf/reg-sub
  :common/error
  (fn [db _]
    (:common/error db)))

(rf/reg-sub
 :url
  (fn [db _]
    (:url db)))

(rf/reg-sub
  :transcription
  (fn [db _]
    (:transcription db)))

