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
  "Protocol for REngine objects that can be turned into Clojure values"
  (->val-with-meta [x] "Transformes an REngine object into Clojure value with metadata."))

(extend-protocol Rexp
  REXPDouble
  (->val-with-meta [rds] (-> rds .asDoubles vec))
  REXPGenericVector
  (->val-with-meta [rxs] (-> rxs .asList ->val-with-meta))
  REXPInteger
  (->val-with-meta [ris] (-> ris .asIntegers vec))
  REXPLogical
  (->val-with-meta [rls] (->> rls .asBytes (map pos?) vec))
  REXPNull
  (->val-with-meta [_] nil)
  REXPString
  (->val-with-meta [rfs] (-> rfs .asStrings vec))
  REXP
  (->val-with-meta [rexp] (str rexp))
  RList
  (->val-with-meta [rxs] (reduce (fn
                         [acc k]
                         (assoc acc
                                (keyword k)
                                (->> (str k)
                                     (.at rxs)
                                     ->val-with-meta)))
                       {}
                       (.keys rxs)))
  nil
  (->val-with-meta [_] nil))



(defn- attr
  [^REXP r]
  (some-> r ._attr .asList ->val-with-meta))


(defn r->clj
  [r]
  (with-meta (->val-with-meta r) (attr r)))

(defprotocol RData
  (->r [x] "Convert a Clojure value into an REngine objects"))

(extend-protocol RData
  clojure.lang.ISeq
  (->r [coll] (str "c(" (str/join \, (map ->r coll)) ")"))
  clojure.lang.PersistentVector
  (->r [coll] (->r (seq coll)))
  clojure.lang.Symbol
  (->r [x] (name x))
  java.lang.Boolean
  (->r [x] (if x "TRUE" "FALSE"))
  java.lang.Long
  (->r [x] (str x))
  java.lang.Double
  (->r [x] (str x))
  java.lang.String
  (->r [s] (str "'" s "'")))

(defn eval
  [src]
  (if @connection
    (r->clj (.eval ^RConnection @connection src))
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
