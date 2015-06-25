(ns tnoda.rashinban.core
  (:refer-clojure :exclude [apply eval])
  (:require [clojure.core :as clj]
            [clojure.string :as str]
            [tnoda.rashinban.protocols :refer [clj->rexp java->clj]])
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

(defn- connect
  ([]
   (reset! connection (RConnection.)))
  ([^String host]
   (reset! connection (RConnection. host)))
  ([^String host port]
   (reset! connection (RConnection. host (int port)))))


(defn- get-conn ^RConnection
  []
  (or @connection
      (throw (ex-info "Rserve connection has not been established."
                      {:connection @connection}))))

(defn shutdown
  []
  (swap! connection (memfn ^RConnection shutdown)))

(defn eval*
  [^String src]
  (-> (get-conn) (.eval src) .asNativeJavaObject))

(defn eval
  [src]
  (java->clj (eval* src)))

(defn apply*
  [^String rfn & more]
  (let [args (->> (clj/apply list* more)
                  (map clj->rexp)
                  (into-array REXP))
        what (REXP/asCall rfn ^"[Lorg.rosuda.REngine.REXP;" args)]
    (-> (get-conn)
        (.eval what nil true)
        .asNativeJavaObject)))

(defn apply
  [& args]
  (java->clj (clj/apply apply* args)))

(defn- rdefn
  [rfn]
  (let [clojurize #(symbol (str/replace % #"[./]" "-"))]
    (intern 'tnoda.rashinban
            (clojurize rfn)
            #(apply rfn %&))))

(defn- load-builtins
  []
  (doseq [rfn (apply "builtins" nil)]
    (rdefn rfn)))

(defn- load-attached-package-fns
  []
  (doseq [pkg (->> (eval "search()")
                   (keep #(second (re-find #"^package:(.+)" %))))
          rfn (eval (str "ls(getNamespace(\"" pkg "\"))"))]
    (rdefn rfn)))

(defn init
  [& args]
  (connect)
  (load-builtins)
  (load-attached-package-fns))
