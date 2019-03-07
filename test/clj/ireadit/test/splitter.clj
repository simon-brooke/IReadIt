(ns ireadit.test.splitter
  (:require [clojure.string :as s]
            [clojure.test :refer :all]
            [ring.mock.request :refer :all]
            [ireadit.splitter :refer :all]
            [ireadit.middleware.formats :as formats]
            [muuntaja.core :as m]
            [mount.core :as mount]))

(deftest test-splitter
  (testing "Splitting a string into tweets"
    (let [test-input "We believe that the Scottish Parliament should have the
          right to hold another referendum if there is clear and sustained
          evidence that independence has become the preferred option of a
          majority of the Scottish people — or if there is a significant
          and material change in the circumstances that prevailed in 2014,
          such as Scotland being taken out of the EU against our will."
          expected (list "I read it as: \"We believe that the Scottish Parliament should have the right to hold another referendum if there is clear and sustained evidence that independence has become the preferred option of a majority of the >>>"
                         "Scottish people — or if there is a significant and material change in the circumstances that prevailed in 2014, such as Scotland being taken out of the EU against our will. \"")
          actual (split-into-tweets test-input)]
      (is (= actual expected)))
    (let [test-input "We believe that the Scottish Parliament should have the
          right to hold another referendum if there is clear and sustained
          evidence that independence has become the preferred option of a
          majority of the Scottish people — or if there is a significant
          and material change in the circumstances that prevailed in 2014,
          such as Scotland being taken out of the EU against our will."
      tweets (split-into-tweets test-input)
      expected true
      actual (s/starts-with? (first tweets) prologue)]
      (is (= actual expected)))
      ))

