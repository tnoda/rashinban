(ns tnoda.rashinban-test
  (:require [clojure.test :refer :all]
            [clojure.test.check :as tc]
            [clojure.test.check.generators :as gen]
            [clojure.test.check.properties :as prop]
            [clojure.test.check.clojure-test :refer [defspec]]
            [tnoda.rashinban :as r]
            [tnoda.rashinban.core :as core])
  (:import (org.rosuda.REngine REXPDouble)))

(deftest test-eval
  (is (= [3.0]
         (vec (.asDoubles (core/eval "1 + 2"))))))

(deftest test-core-apply-function
  (is (= [3.0]
         (vec (.asDoubles ^REXPDouble (core/apply 'c [1 2]))))))
