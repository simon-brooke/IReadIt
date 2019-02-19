(ns ireadit.test.responder
  (:require [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [ireadit.bot.responder :refer :all]
            [muuntaja.core :as m]
            [mount.core :as mount]))



(def tweet (read-string (slurp "env/test/resources/data/original_format_image_alt_text.edn")))
(image-from-tweet tweet 1)

(use-fixtures
  :once
  (fn [f]
    (mount/start #'ireadit.config/env)
    (f)))

(deftest test-image-url-extraction
  (testing "Extraction of image URLs from Tweets"
    (let [path (clojure.java.io/resource "/data/original_format_image_alt_text.edn")
          p  "env/test/resources/data/original_format_image_alt_text.edn"
          tweet (read-string (slurp "env/test/resources/data/original_format_image_alt_text.edn"))]
      (is (= p path))
      (is (= tweet "froboz"))
      (let [expected "http://pbs.twimg.com/media/C3B5jtZVIAAtCue.jpg"
            actual (image-from-tweet tweet)]
        (is (= expected actual)
            (str "Failed to extract URL from tweet: expected `" expected "` found `" actual "`")))
      (let [expected "http://pbs.twimg.com/media/C3B5jtZVIAAtCue.jpg"
            actual (image-from-tweet tweet 1)]
        (is (= expected actual)
            (str "Failed to extract URL from tweet: expected `" expected "` found `" actual "`"))))))
