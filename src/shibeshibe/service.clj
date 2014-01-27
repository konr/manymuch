(ns shibeshibe.service
  (:require [io.pedestal.service.http :as bootstrap]
            [io.pedestal.service.http.route :as route]
            [io.pedestal.service.http.body-params :as body-params]
            [io.pedestal.service.http.route.definition :refer [defroutes]]
            [shibeshibe.controller :as c]
            [ring.util.response :as ring-resp]))

(defn parse-wallet [{:keys [json-params] :as request}]
  {:status 200
   :body (c/parse-wallet json-params)})

(defroutes routes
  [[["/" ^:interceptors [(body-params/body-params) bootstrap/html-body]
     ["/api"
      ["/convert" {:post parse-wallet}]]]]])

(def service {:env :prod
              ::bootstrap/routes routes
              ::bootstrap/resource-path "/public"
              ::bootstrap/type :jetty
              ::bootstrap/port 8080})
