(defproject org.clojars.tnoda/rashinban "0.0.154"
  :description "A Clojure library to work with R"
  :url "https://github.com/tnoda/rashinban"
  :license {:name "Apache License, Version 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :dependencies [[org.rosuda.REngine/Rserve "1.8.1"]
                 [org.rosuda.REngine/REngine "2.1.0"]]
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.7.0"]
                                  [org.clojure/test.check "0.8.0"
                                   :exclusions [org.clojure/clojure]]]}})
