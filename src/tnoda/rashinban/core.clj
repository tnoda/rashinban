(ns tnoda.rashinban.core
  (:refer-clojure :exclude [apply eval])
  (:require [clojure.core :as clj]
            [tnoda.rashinban.protocols :refer [->rexp ->clj]])
  (:import (org.rosuda.REngine.Rserve RConnection)
           (org.rosuda.REngine REXP
                               REXPDouble
                               REXPGenericVector
                               REXPInteger
                               REXPLogical
                               REXPNull
                               REXPString
                               REXPSymbol
                               RList)))

(defonce connection (atom nil))

(defn connect
  []
  (swap! connection (fn
                      [^RConnection conn]
                      (if (and conn (.isConnected conn))
                        conn
                        (RConnection.)))))

(defn- get-conn ^RConnection
  []
  (or @connection
      (throw (ex-info "Rserve connection has not been established."
                      {:connection @connection}))))

(defn shutdown
  []
  (swap! connection #(.shutdown ^RConnection %)))

(defn eval
  [src]
  (-> (get-conn)
      (.eval src)
      ->clj))

(defn apply
  [^String rfn & more]
  (let [args (->> (clj/apply list* more)
                  (map ->rexp)
                  (into-array REXP))
        what (REXP/asCall rfn args)]
    (-> (get-conn)
        (.eval what nil true)
        ->clj)))

