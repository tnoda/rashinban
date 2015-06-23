(defproject org.clojars.tnoda/rashinban "0.0.50"
  :description "A Clojure library to work with R"
  :url "https://github.com/tnoda/rashinban"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.rosuda.REngine/Rserve "1.8.1"]
                 [org.rosuda.REngine/REngine "2.1.0"]
                 [incanter "1.5.6"
                  :exclusions [org.clojure/clojure]]]
  :profiles {:dev {:dependencies [[org.clojure/test.check "0.7.0"]]}})
