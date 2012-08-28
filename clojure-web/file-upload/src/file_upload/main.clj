(ns file-upload.main
  (:require [file-upload.core :as core])
  (:use [ring.adapter.jetty]))

(defn -main []
  (run-jetty core/app {:port 8080}))