(ns tnoda.rashinban
  (:refer-clojure :only [defn defn- ->> ->])
  (:require [clojure.core :as clj]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [tnoda.rashinban.core :as core]))

(defn- create-rfn
  [rname]
  (clj/println rname)
  (clj/if-let [cljname (clj/re-find #"^[0-9a-zA-Z*+!_?-]+$"
                                    (str/replace rname \. \-))]
    (clj/intern 'tnoda.rashinban
                (clj/symbol cljname)
                (clj/fn [& more] (core/apply rname more)))))

(defn- load-package-fns
  [pkg]
  (clj/doseq [rname (core/eval (clj/str "ls(getNamespace(\"" pkg "\"))"))]
    (create-rfn rname)))

(defn init
  []
  (core/connect)
  (clj/doseq [bfn (core/eval "builtins()")]
    (create-rfn bfn))
  (clj/doseq [pkg (->> (core/apply 'search)
                       (clj/keep (clj/comp clj/second
                                           #(clj/re-find #"^package:(.+)" %))))]
    (load-package-fns pkg)))
