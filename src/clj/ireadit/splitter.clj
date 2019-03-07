(ns ireadit.splitter
  (:require [clojure.string :as s]))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;
;;;; ireadit.splitter: split OCR output into individual tweets.
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


(def prologue
  "Prologue for the first tweet in a thread of tweets."
  "I read it as: \"")

(def continuation
  "Epilogue of a single tweet for with there will be a successor."
  ">>>")

(def usable-length
  "The maximum length of the payload of a single tweet"
  (- 220 (+ (count prologue) (count continuation))))

(defn count-tweet
  "Count the number of characters in a tweet constructed from this list of tokens."
  [tokens]
  (+ (count (rest tokens)) (reduce + (map count tokens))))

(defn construct-tweets
  "Take this list of tokens, and return a lost of strings representing
  individual tweets"
  ([tokens]
   (let [tweets (map
                 #(s/join " " (reverse %))
                 (construct-tweets tokens nil))]
     (cons (str prologue (first tweets)) (rest tweets))))
  ([tokens tweet]
   (cond
    (empty? tokens) (list (cons "\"" tweet))
    (<
     (+
      (count-tweet tweet)
      (count (first tokens)))
     usable-length)
    (construct-tweets (rest tokens) (cons (first tokens) tweet))
    true
    (cons
     (cons continuation tweet)
     (construct-tweets tokens nil)))))

(defn split-into-tweets
  [input]
  (if
    (string? input)
    (split-into-tweets (s/split input #"\s+"))
    (construct-tweets input)))
