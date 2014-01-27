(ns shibeshibe.service
  (:require [io.pedestal.service.http :as bootstrap]
            [io.pedestal.service.http.route :as route]
            [io.pedestal.service.http.body-params :as body-params]
            [io.pedestal.service.http.route.definition :refer [defroutes]]
            [shibeshibe.utils :refer :all]
            [shibeshibe.controller :as c]
            [ring.util.response :as ring-resp]))

(defn parse-wallet [{:keys [form-params] :as request}]
  {:status 200
   ;; FIX I want json-params, not form-params
   :body (c/parse-wallet (map-keys keyword form-params))})

(defroutes routes
  [[["/" ^:interceptors [(body-params/body-params) bootstrap/html-body]
     ["/api"
      ["/convert" {:post parse-wallet}]]]]])

(def service {:env :prod
              ::bootstrap/routes routes
              ::bootstrap/resource-path "/public"
              ::bootstrap/allowed-origins ["http://localhost"]
              ::bootstrap/type :jetty
              ::bootstrap/port 8080})
