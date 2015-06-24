(ns tnoda.rashinban-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [tnoda.rashinban :as r]
            [tnoda.rashinban.core :as core]))

(deftest test-eval
  (is (= [3.0]
         (core/eval "1 + 2"))))

(defspec test-protocols-for-longs
  100
  (prop/for-all [xs (gen/vector gen/int)]
    (= (seq (map double xs)) (core/apply "c" xs))))

(defspec test-protocols-for-long-object
  100
  (prop/for-all [x gen/int
                 y gen/int
                 z gen/int]
    (= (map double [(+ x z) (+ y z)])
       (core/apply "+" [[x y] z]))))

(defspec test-protocols-for-doubles
  100
  (prop/for-all [xs (gen/vector (gen/fmap double gen/int))]
    (= (seq xs) (core/apply "c" xs))))

(defspec test-protocols-for-booleans
  100
  (prop/for-all [xs (gen/vector gen/boolean)]
    (= (seq xs) (core/apply "c" xs))))

(defspec test-protocols-for-strings
  100
  (prop/for-all [xs (gen/vector gen/string-ascii)]
    (= (seq xs) (core/apply "c" xs))))
