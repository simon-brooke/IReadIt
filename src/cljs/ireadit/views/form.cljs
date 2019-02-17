(ns ireadit.views.form
  (:require [baking-soda.core :as b]
            [day8.re-frame.http-fx]
            [reagent.core :as r]
            [re-frame.core :as rf]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [ireadit.ajax :as ajax]
            [ireadit.events]
            [secretary.core :as secretary])
  (:import goog.History))


(defn form-page []
  [:div
   [:h1 "Transcribe the text of an image"]
   [:div.container {:id "main-container"}
    [:div
     [:p.widget
      [b/Label {:for "image-url" :title "URL of the image you wish to transcribe"}"Image URL"]
      [b/Input {:id "image-url" :type "text" :size "80"
                :on-change #(rf/dispatch [:set-url (.-value (.-target %))])}]]
     [:p.widget
      [b/Label {:for "send"} "To transcribe the image"]
      [b/Button {:id "send" :on-click #(rf/dispatch [:fetch-transcription])} "Transcribe!"]]]
    [:div.transcription @(rf/subscribe [:transcription])]
    ]])

