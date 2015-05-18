(ns tnoda.rashinban-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [tnoda.rashinban :as r]))

(defspec a-number-array-is-converted-into-an-array-of-doubles
  100
  (prop/for-all [xs (gen/vector gen/int)]
    (= (seq (map double xs)) (apply r/c xs))))

(defspec boolean-values-support
  100
  (prop/for-all [bs (gen/vector gen/boolean)]
    (= (seq bs) (apply r/c bs))))

(defspec string-values-support
  100
  (prop/for-all [ss (gen/vector gen/string-alphanumeric)]
    (= (seq ss) (apply r/c ss))))

