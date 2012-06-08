(ns cabinet.web
  (:use compojure.core)
  (:use ring.middleware.json-params)
  (:use ring.adapter.jetty)
  (:require [clj-json.core :as json])
  (:require [cabinet.elem :as elem])
  (:import org.codehaus.jackson.JsonParseException)
  (:use [slingshot.slingshot :only [try+]]))

(def error-codes
  {:invalid 400
   :not-found 404})

(defn json-response [data & [status]]
  {:status (or status 200)
   :headers {"Content-Type" "application/json"}
   :body (json/generate-string data)})

(defn wrap-error-handling [handler]
  (fn [req]
    (try+
      (or (handler req)
          (json-response {"error" "resource not found"} 404))
      (catch JsonParseException e
        (json-response {"error" "malformed json"} 400))
      (catch [:type :not-found] {:keys [type message]}
        (json-response {"error" message} (error-codes :not-found)))
      (catch [:type :invalid] {:keys [type message]}
        (json-response {"error" message} (error-codes :invalid))))))


(defroutes handler
           (GET "/" []
                (json-response {"hello" "world"}))
           (PUT "/" [name]
                (json-response {"hello" name}))
           (GET "/elems" []
                (json-response (elem/list)))
           (GET "/elems/:id" [id]
                (json-response (elem/get id)))
           (PUT "/elems/:id" [id attrs]
                (json-response (elem/put id attrs)))
           (DELETE "/elems/:id" [id]
                (json-response (elem/delete id))))

(def app
  (-> handler
    wrap-json-params
    wrap-error-handling))

(defn -main []
  (run-jetty app {:port 8080}))
