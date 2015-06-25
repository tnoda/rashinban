(ns tnoda.rashinban.protocols
  (:import (org.rosuda.REngine REXP
                               REXPDouble
                               REXPGenericVector
                               REXPInteger
                               REXPLogical
                               REXPNull
                               REXPString)))

(defprotocol CljToREXP
  (clj->rexp [x] "Protocol to convert a Clojure value into an REngine object"))

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

(extend-protocol CljToREXP
  nil
  (clj->rexp [x] (REXPNull.))

  Boolean
  (clj->rexp [x] (REXPLogical. x))

  String
  (clj->rexp [x] (REXPString. x))

  clojure.lang.Seqable
  (clj->rexp [x] (-> x seq seq->rexp))

  Iterable
  (clj->rexp [x] (-> x seq seq->rexp))

  CharSequence
  (clj->rexp [x] (-> x seq seq->rexp))

  Object
  (clj->rexp [x] (cond
                (number? x)
                (REXPDouble. (double x))

                (.isArray (class x))
                (seq->rexp x)

                :default
                (throw (IllegalArgumentException.
                        (str "clj->rexp could not convert a Clojure value into a REXP object: "
                             {:value x :class (class x) :type (type x)}))))))

(defprotocol JavaToClj
  (java->clj [x] "Protocol to convert native Java objects of REngine to Clojure values"))

;;; double
(extend-protocol JavaToClj
  (Class/forName "[D")
  (java->clj [x]
    (vec x)))

;;; integer
(extend-protocol JavaToClj
  (Class/forName "[I")
  (java->clj [x]
    (vec x)))

;;; boolean
(extend-protocol JavaToClj
  (Class/forName "[B")
  (java->clj [x]
    (mapv #(= % REXPLogical/TRUE) x)))

(extend-protocol JavaToClj
  (Class/forName "[Ljava.lang.String;")
  (java->clj [x]
    (vec x)))

;;; Generic vectors
(extend-protocol JavaToClj
  java.util.Map
  (java->clj [x]
    (into {} x))
  java.util.List
  (java->clj [x]
    (mapv java->clj x)))

;;; Default
(extend-protocol JavaToClj
  nil
  (java->clj [_]
    nil)

  Object
  (java->clj [x]
    x))
