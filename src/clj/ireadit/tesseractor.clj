(ns ^{:doc "Meme transcriber: actual OCR interface"
      :author "Simon Brooke"}
  ireadit.tesseractor
  (:require [clojure.java.io :as io]
            [clojure.tools.logging :as log]
            [ireadit.config :refer [env]])
  (:import net.sourceforge.tess4j.Tesseract
           java.io.File
           java.net.URL
           javax.imageio.ImageIO))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;;;;
;;;; ireadit.tesseractor: actual OCR interface.
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
;;;; Copyright (C) 2016 Simon Brooke for Radical Independence Campaign
;;;;
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;;; Cribbed partly from https://github.com/hugoArregui/tesseract-clojure

(defn prepare-tesseract [data-path]
  (let [t (Tesseract.)]
    (.setDatapath t data-path)
    t))


(def tesseractor (prepare-tesseract (:tess-data env)))

(defn ocr
  "Perform optical charactor representation on `imgage` using the OCR engine
  `t`, assuming the ISO 639-3 language `lang`, and return any text found as a
  string. `image` may be supplied as a `File`, as `BufferedImage`, or as a
  string, in which case it will be treated as a URL."
  ([image]
   (ocr image tesseractor))
  ([image t]
   (ocr image t "eng"))
  ([image t lang]
   (let [img (if
               (string? image)
               (ImageIO/read (URL. image))
               image)]
   (.setLanguage t lang)
   (.doOCR t img))))

