(ns tnoda.rashinban.protocols
  (:import (org.rosuda.REngine REXPDouble
                               REXPInteger
                               REXPLogical
                               REXPNull
                               REXPString)))

(defprotocol ToREXP
  (->rexp [x] "Protocol to convert a Clojure value into an REngine object"))

(defn- seq->rexp
  [s]
  (let [x (first s)]
    (cond
      (number? x)
      (REXPDouble. (double-array s))

      (or (= x true) (= x false))
      (->> s
           (map #(if % REXPLogical/TRUE REXPLogical/FALSE))
           byte-array
           REXPLogical.)

      :default
      (->> s
           (map str)
           (into-array String)
           REXPString.))))

(extend-protocol ToREXP
  nil
  (->rexp [x] (REXPNull.))

  Boolean
  (->rexp [x] (REXPLogical. x))

  String
  (->rexp [x] (REXPString. x))

  clojure.lang.Seqable
  (->rexp [x] (-> x seq seq->rexp))

  Iterable
  (->rexp [x] (-> x seq seq->rexp))

  CharSequence
  (->rexp [x] (-> x seq seq->rexp))

  Object
  (->rexp [x] (cond
                (number? x)
                (REXPDouble. (double x))

                (.isArray (class x))
                (seq->rexp x)

                :default
                (REXPString. (str x)))))

(defprotocol ToClj
  (->clj [x] "Protocol to convert an REngine object into a Clojure value"))

(extend-protocol ToClj
  REXPDouble
  (->clj [x] (-> x .asDoubles vec))

  REXPInteger
  (->clj [x] (-> x .asIntegers vec))

  REXPLogical
  (->clj [x] (mapv #(= % REXPLogical/TRUE) (.asBytes x)))

  REXPString
  (->clj [x] (-> x .asStrings vec))

  REXPNull
  (->clj [_] nil))
