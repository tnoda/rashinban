(ns tnoda.rashinban
  (:refer-clojure :only [defn defn- ->> ->])
  (:require [clojure.core :as clj]
            [clojure.string :as str]
            [clojure.java.io :as io]
            [tnoda.rashinban.core :as core]))

(defn- load-functions
  [rfns]
  (clj/doseq [rfn (clj/filter #(clj/re-find #"^[a-z]\S*" %) rfns)]
    (clj/intern 'tnoda.rashinban
                (clj/symbol (str/replace rfn \. \-))
                (clj/fn [& more] (core/apply rfn more)))))

(defn- library-index-path
  [lib]
  (-> (core/apply 'library [{:help lib}])
      :path
      clj/first
      (clj/str "/INDEX")))

(defn- load-library
  [lib]
  (clj/let [index-path (library-index-path lib)]
    (core/apply 'library [lib])
    (->> index-path
         io/reader
         clj/line-seq
         (clj/keep #(clj/re-find #"^[a-z]\S*" %))
         load-functions)))

(defn init
  []
  (core/start)
  (load-functions (core/eval "builtins()"))
  (clj/doseq [lib (->> (core/apply 'search)
                       (clj/keep (clj/comp clj/second
                                           #(clj/re-find #"^package:(.+)" %))))]
    (load-library lib)))
