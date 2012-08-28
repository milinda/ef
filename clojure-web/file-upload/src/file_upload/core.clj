(ns file-upload.core
  (:use [compojure.core]
        [ring.middleware.params]
        [ring.middleware.multipart-params]
        [ring.adapter.jetty]
        [hiccup.core]
        [clojure.java.io])
  (:import [java.io File]))

(defn home-page []
  (html [:form {:action "/file" :method "post" :enctype "multipart/form-data"}
         [:input {:name "file" :type "file" :size "20"}]
         [:input {:type "submit" :name "submit" :value "submit"}]]))

(defn upload-file [file]
  (let [file-name (file :filename)
        size (file :size)
        actual-file (file :tempfile)]
    (do
      (copy actual-file (File. (format "/Users/milinda/Desktop/%s" file-name)))
      {:status 200
       :headers {"Content-Type" "text/html"}
       :body (html [:h1 file-name]
                 [:h1 size])})))
  
(defroutes handler
  (POST "/file" {params :params}
        (let [file (get params "file")]
         (upload-file file)))
  (GET "/" []
       (home-page)))

(def app
  (-> handler
      wrap-params
      wrap-multipart-params))
