(ns tnoda.rashinban-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [tnoda.rashinban :as r]
            [tnoda.rashinban.core :as core])
  (:import (org.rosuda.REngine REXPDouble)))

(deftest test-core-apply-function
  (is (= [3.0] (.asDoubles ^REXPDouble (core/apply 'c [1 2])))))
