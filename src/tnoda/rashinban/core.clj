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

(defn eval
  [src]
  (if @connection
    (r->clj (.eval ^RConnection @connection src))
    (throw (ex-info "Rserve connection has not been established."
                    {:connection @connection
                     :src src}))))


(defn apply2
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
