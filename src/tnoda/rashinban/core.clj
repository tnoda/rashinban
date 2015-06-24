(ns tnoda.rashinban.core
  (:refer-clojure :exclude [apply eval])
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

(defn apply
  [& args]
  (throw (ex-info "undefined"
                  {:connection @connection
                   :args args})))
