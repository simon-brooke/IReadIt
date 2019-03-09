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

(defn maybe-disable [m]
  (if
    @(rf/subscribe [:pending])
    (assoc m :disabled "disabled")
    m))

(defn form-page []
  [:div.container-fluid {:id "main-container"}
   [:h1 "Transcribe the text of an image"]
   [:div
    [b/Row
     [:div.col-sm-3
      [b/Label {:for "image-url" :title "URL of the image you wish to transcribe"}"Image URL"]]
     [:div.col-sm-9
      [b/Input {:id "image-url" :type "text" :size "80"
                :on-change #(rf/dispatch [:set-url (.-value (.-target %))])
                }]]]
    [b/Row
     [:div.col-sm-3
      [b/Label {:for "send"} "To transcribe the image"]]
     [:div.col-sm-9
      [b/Button (maybe-disable {:id "send" :on-click #(rf/dispatch [:fetch-transcription])}) "Transcribe!"]]]
    [b/Row]
     (map #(vector
            :div.tweet {:class "row"}
            [:div.col-sm-1
                   [:img {:src "/img/ireadit.png"
                          :width "64"
                          :height "64"
                           :class "rounded-circle"
                           :alt "Please don't post text as images to social media"}]]
            [:div.col-sm-1]
            [:div.col-sm-10 [:div [:b "IReadIt"]] %])
          (let [m @(rf/subscribe [:transcription])]
            (when (map? m) (m "tweets"))))
    [b/Collapse {:class (if @(rf/subscribe [:error]) "col-sm-12 show" "hide")}
     [b/Alert {:color "warning"} @(rf/subscribe [:error])]]]])

(vector 'div.foo "foo")

