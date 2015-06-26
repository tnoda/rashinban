(ns tnoda.rashinban
  (:refer-clojure :only [])
  (:require [clojure.core :as clj]
            [tnoda.rashinban.core :as core]))

(clj/defn init
  "Initializes the Rashinban environment. R built-in funcitons and
  functions of currently attached packages are loaded into the
   tnoda.rashinban namespace."
  [& args]
  (clj/ns-unmap 'tnoda.rashinban 'Math)
  (clj/apply core/init args))
