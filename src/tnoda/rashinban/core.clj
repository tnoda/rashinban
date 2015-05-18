(ns tnoda.rashinban.core
  (:refer-clojure :exclude [apply eval])
  (:require [clojure.string :as str])
  (:import (org.rosuda.REngine.Rserve RConnection)
           (org.rosuda.REngine REXP
                               REXPDouble
                               REXPGenericVector
                               REXPInteger
                               REXPLogical
                               REXPNull
                               REXPString
                               RList)))

(defonce connection (atom nil))

(defn connect
  []
  (swap! connection (fn
                  [^RConnection conn]
                  (if (and conn (.isConnected conn))
                    conn
                    (RConnection.)))))

(defn shutdown
  []
  (swap! connection #(.shutdown ^RConnection %)))

(defprotocol Rexp
  (->clj [x] "Convert an REngine objects into a Clojure value."))

(extend-protocol Rexp
  REXPDouble
  (->clj [rds] (-> rds .asDoubles vec))
  REXPGenericVector
  (->clj [rxs] (-> rxs .asList ->clj))
  REXPInteger
  (->clj [ris] (-> ris .asIntegers vec))
  REXPNull
  (->clj [_] nil)
  REXPString
  (->clj [rfs] (-> rfs .asStrings vec))
  REXP
  (->clj [rexp] (str rexp))
  RList
  (->clj [rxs] (reduce (fn
                         [acc k]
                         (assoc acc
                                (keyword k)
                                (->> (str k)
                                     (.at rxs)
                                     ->clj)))
                       {}
                       (.keys rxs)))
  nil
  (->clj [_] nil))

(defprotocol RData
  (->r [x] "Convert a Clojure value into an REngine objects"))

(extend-protocol RData
  clojure.lang.ISeq
  (->r [coll] (str "c(" (str/join \, (map ->r coll)) ")"))
  clojure.lang.PersistentVector
  (->r [coll] (->r (seq coll)))
  clojure.lang.Symbol
  (->r [x] (->r (str x)))
  java.lang.Long
  (->r [x] (str x))
  java.lang.Double
  (->r [x] (str x))
  java.lang.String
  (->r [s] (str "'" s "'")))

(defn eval
  [src]
  (if @connection
    (->clj (.eval ^RConnection @connection src))
    (throw (ex-info "Rserve connection has not been established."
                    {:connection @connection
                     :src src}))))


(defn apply
  ([rfn more]
   (let [optionize (fn
                     [m]
                     (map (fn
                            [[k v]]
                            (str (if (keyword? k)
                                   (name k)
                                   (str k))
                                 "="
                                 (->r v)))
                          m))
         args (if (map? (last more))
                (concat (map ->r (butlast more)) (optionize (last more)))
                (map ->r more))
         src (str rfn "(" (str/join \, args) ")")]
     (eval src)))
  ([rfn]
   (eval (str rfn "()"))))
