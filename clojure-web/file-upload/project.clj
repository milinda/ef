(defproject file-upload "1.0.0-SNAPSHOT"
  :description "Clojure Ring File Upload Sample"
  :dependencies [[org.clojure/clojure "1.3.0"]
                 [ring/ring-core "1.1.0"]
                 [ring/ring-devel "1.1.0"]
                 [ring/ring-jetty-adapter "1.1.0"]
                 [ring-json-params "0.1.3"]
                 [compojure "1.1.0"]
                 [clj-http "0.5.2"]
                 [org.clojure/tools.nrepl "0.2.0-beta9"]
                 [hiccup "1.0.1"]]
  :dev-dependencies [[lein-ring "0.7.1"]]
  :main file-upload.main)