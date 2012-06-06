(ns adder.core
  (:use compojure.core)
  (:use hiccup.core)
  (:use hiccup.page)
  (:use ring.adapter.jetty)
  (:use ring.middleware.params))

(defn view-layout [& content]
  (html
    (doctype :xhtml-strict)
    (xhtml-tag "en"
               [:head
                [:meta {:http-equiv "Content-type"
                        :content "text/html; charset=utf-8"}]
                [:title "adder"]]
               [:body content])))

(defn view-input []
  (view-layout
    [:h2 "add two numbers"]
    [:form {:method "post" :action "/"}
     [:input.math {:type "text" :name "a"}] [:span.math "+"]
     [:input.math {:type "text" :name "b"}] [:br]
     [:input.action {:type "submit" :value "add"}]]))

(defn view-output [a b sum]
  (view-layout
    [:h2 "two numbers added"]
    [:p.math a " + " b " = " sum]
    [:a.action {:href "/"} "add more numbers"]))

(defn parse-input [a b]
  [(Integer/parseInt a) (Integer/parseInt b)])

(defroutes myroutes
           (GET "/" []
                (view-input))
           (POST "/" [a b]
                   (let [[x y] (parse-input a b)
                         sum (+ x y)]
                     (view-output x y sum))))
(def app (wrap-params myroutes))

(defn -main []
  (run-jetty app {:port 8080}))
