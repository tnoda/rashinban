(ns tnoda.rashinban-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [tnoda.rashinban :as r]
            [tnoda.rashinban.core :as rc]))

(deftest test-eval-without-parse
  (is (= 3.0 (.asDouble (rc/apply 'r/c [1 2])))))
