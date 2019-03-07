(ns ^{:doc "Meme transcriber: Twitter bot"
      :author "Simon Brooke"}
  ireadit.bot.responder
  (:require [clojure.data.json :as json]
            [http.async.client :as ac]
            [clojure.edn :as edn]
            [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [ireadit.config :refer [env]]
            [ireadit.tesseractor :as tess]
            [twitter.api.restful :as tar]
            ;;    [twitter.api.streaming :as tas]
            ;;    [twitter.callbacks :as tc]
            [twitter.callbacks.handlers :as tch]
            [twitter.oauth :as to]
            [twitter-streaming-client.core :as client])
  (:import
   (twitter.callbacks.protocols SyncSingleCallback)
   (twitter.callbacks.protocols SyncStreamingCallback)))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;
;;;; ireadit.bot.responder: Twitter bot.
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

;;; partially cribbed from https://github.com/kelseyq/clojure-twitter-bot

(def credentials
  (to/make-oauth-creds (:app_key env)
                    (:app_secret env)
                    (:user_token env)
                    (:user_secret env)))

(def mentions-stream
  (client/create-twitter-stream twitter.api.streaming/user-stream
                                                   :oauth-creds credentials :params {:with "user"}))

(defn do-every
  [ms callback]
  (loop []
    (do
      (Thread/sleep ms)
      (try (callback)
           (catch Exception e (log/error e (str "caught exception: " (.getMessage e))))))
    (recur)))

(defn is-legit-mention?
  [tweetMap]
  (not (or (empty? tweetMap)
           (= (get-in tweetMap [:user :screen_name]) (:bot-account env))
           (and (= (get-in tweetMap [:retweeted_status :user :screen_name]) (:bot-account env))
                (.startsWith (get-in tweetMap [:retweeted_status :text]) "@")))))

;; if the mention is in response to a tweet, then it will have a top level
;; `in_reply_to_status_id_str` attribute. It is the tweet whose id is the
;; value of this attribute which is the one which probably contains the image.
(defn tweet-replied-to
  "Given this `tweet`, assumed to be a map representing a JSON representation
  of a tweet, return an equivalent representation of the tweet to which it was
  a reply. If `tweet` was not a reply, returns `nil`."
  [tweet]
  (let [parent-id (:in_reply_to_status_id_str tweet)]
    (if
      parent-id
      (tar/statuses-show-id :oauth-creds credentials
                            :params {:id parent-id
                                     :include-entities true}
                            :callbacks (SyncSingleCallback. tch/response-return-body
                                                            tch/response-throw-error
                                                            tch/exception-rethrow)))))

(defn image-from-tweet
  "Return the url of a media entity from this `tweet`, assumed to be a map
  representing a JSON representation of a tweet,; if `index` is not
  specified, the first media entity; else the indexth. If no such entity
  exists, returns `nil`."
  ([tweet]
   (image-from-tweet tweet 0))
  ([tweet index]
   (:media_url
    (nth
     (or
      (get-in tweet [:extended_entities :media])
      (get-in tweet [:entities :media]))
     index))))

(defn truncate
  "If string `s` is longer than `n` characters, return a string like `s`
  truncated to `n` characters and with an added trailing ellipsis."
  [s n]
  (if (> (count s) n)
    (str (subs s 0 (min (count s) n)) "…")
    s))

(defn reply-to-mention
  "From this `mention`, assumed to be a map representing a JSON representation
  of a tweet, extract the tweet replied to, and, from that,
  extract the first media entity if present; pass the entity to the
  tesseractor, and post a reply based on its response."
  [mention]
  (let [screen-name (str "@" (get-in mention [:user :screen_name]))
        image (image-from-tweet (tweet-replied-to [mention]))
        transcription (if image
                       (try
                         (str "Hi, "
                              screen-name
                              ", I read it as '"
                              (tess/ocr image)
                              "'.")
                         (catch Exception e
                           (log/error
                            e
                            (str "error transribing image " image))
                       (str "I'm sorry, "
                            screen-name
                            ", I'm afraid I can't do that.")))
                       "I'm sorry, I didn't find an image in that tweet.")]
    (log/debug (str "replying to mention from " name))
    (try
      (tar/statuses-update :oauth-creds credentials
                           :params {:status (truncate transcription 279)
                                    :in_reply_to_status_id (:id_str mention)}
                           :callbacks (SyncSingleCallback. tch/response-return-body
                                                           tch/response-throw-error
                                                           tch/exception-rethrow))
      (catch Exception e (log/error e (str "error replying to mention from " (get-in mention [:user :screen_name])))))))

(defn reply-to-mentions
  [user-mentions]
  (when-let [mentions (seq (->> user-mentions
                                (filter is-legit-mention?)))]
    (doseq [m mentions] (reply-to-mention m))))

(defn handle-user-stream
  []
  (let [stream (client/retrieve-queues mentions-stream)
        user-events (:unknown stream)
        mentions (:tweet stream)]
    (reply-to-mentions mentions)))

(defn bot []
  (let [;;state (atom (assoc empty-state :minutes-since-update 30))
        previous (atom #{})]

    ;;(start-streams)

    (future (log/debug "STARTING USER STREAM")
      (do-every 60500 handle-user-stream))))
