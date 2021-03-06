(ns adder.core
  (:use compojure.core)
  (:use hiccup.core)
  (:use hiccup.page)
  (:use ring.adapter.jetty)
  (:use ring.middleware.params)
  (:use ring.middleware.reload)
  (:use ring.middleware.stacktrace)
  (:use ring.util.response)
  (:use adder.middleware)
  (:use ring.middleware.file)
  (:use ring.middleware.file-info))

(def production?
  (= "production" (get (System/getenv) "APP_ENV")))

(def development?
  (not production?))

(defn view-layout [& content]
  (html
    (doctype :xhtml-strict)
    (xhtml-tag "en"
               [:head
                [:meta {:http-equiv "Content-type"
                        :content "text/html; charset=utf-8"}]
                [:title "adder"]
                [:link {:href "/css/adder.css" :rel "stylesheet" :type "text/css"}]]
               [:body content])))

(defn view-input [& [a b]]
  (view-layout
    [:h2 "add two numbers"]
    [:form {:method "post" :action "/adder"}
     (if (and a b)
       [:p "those are not both numbers!"])
     [:input.math {:type "text" :name "a" :value a}] [:span.math "+"]
     [:input.math {:type "text" :name "b" :value b}] [:br]
     [:input.action {:type "submit" :value "add"}]]))

(defn view-output [a b sum]
  (view-layout
    [:h2 "two numbers added"]
    [:p.math a " + " b " = " sum]
    [:a.action {:href "/adder"} "add more numbers"]))

(defn parse-input [a b]
  [(Integer/parseInt a) (Integer/parseInt b)])

(defroutes handler
           (GET "/" []
                (view-input))
           (POST "/" [a b]
                 (try
                   (let [[x y] (parse-input a b)
                         sum (+ x y)]
                     (view-output x y sum))
                 (catch NumberFormatException e
                   (view-input a b))))
           (ANY "/*" [path]
                (redirect "/")))

(def app (-> #'handler
           (wrap-if development? wrap-file "resources/public")
           (wrap-file-info)
           (wrap-request-logging)
           (wrap-reload '[adder.core])
           (wrap-bounce-favicon)
           (wrap-stacktrace)
           (wrap-params)))

(defn -main []
  (run-jetty app {:port 8080}))
