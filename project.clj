(defproject tailrecursion/lein-proxy "0.1.0-SNAPSHOT"
  :description "Serves static files and proxies requests."
  :url "http://github.com/tailrecursion/lein-proxy/"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :eval-in-leiningen true
  :dependencies [[org.clojure/clojure "1.5.0"]
                 [ring/ring-jetty-adapter "1.1.6"]
                 [compojure "1.1.3"]
                 [org.clojure/tools.cli "0.2.2"]
                 [tailrecursion/ring-proxy "2.0.0-SNAPSHOT"]])
