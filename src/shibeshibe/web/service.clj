(ns shibeshibe.web.service
  (:require [io.pedestal.service.http :as bootstrap]
            [io.pedestal.service.impl.interceptor :as ii]
            [io.pedestal.service.interceptor :as i]
            [io.pedestal.service.http.route :as route]
            [io.pedestal.service.http.cors :as cors]
            [io.pedestal.service.http.body-params :as body-params]
            [io.pedestal.service.http.route.definition :refer [defroutes]]
            [shibeshibe.utils :refer :all]
            [shibeshibe.controller :as c]
            [ring.util.response :as ring-resp]))

(defn parse-wallet [{:keys [form-params] :as request}]
  {:status 200
   ;; FIX I want json-params, not form-params
   :body (c/parse-wallet (map-keys keyword form-params))})

(defn retrieve-password []
  (try (.trim (slurp "/etc/shibeshibe.conf"))
       (catch Exception e "such_shibe")))

(defn update-db [context]
  (c/update-db context)
  {:status 200
   :body "ok!"})

(i/definterceptorfn admin-shibe [password]
  (i/interceptor
   :enter (fn [context]
            (if (= password (get-in context [:request :headers "authorization"])) context
                (ii/terminate
                 (assoc-in context [:response]
                           {:status 403  :body "very protected. much sorry."}))))))

(def cat "
    _                ___       _.--.
    \\`.|\\..----...-'`   `-._.-'_.-'`
    /  ' `         ,       __.--'
    )/' _/     \\   `-_,   /
    `-'\" `\"\\_  ,_.-;_.-\\_ ',     fsc/as
        _.-'_./   {_.'   ; /
       {_.-``-'         {_/ ")

(i/definterceptorfn catcher []
  (i/interceptor
   :error (fn [context error]
            (clojure.repl/pst)
            (ii/terminate (assoc context :response
                                 {:status 500
                                  ;; FIX display the cat
                                  ;; even in a 500 response
                                  :body (str "Oops! Couldn't parse the wallet, but I found this cute cat:\n" cat)})))))


(defroutes routes
  [[["/" ^:interceptors [(catcher) (body-params/body-params) bootstrap/html-body]
     ["/api" ;;^:interceptors [(cors/allow-origin ["http://shibeshibe.com"])]
      ["/convert" {:post parse-wallet}]
      ["/admin" ^:interceptors [(admin-shibe (retrieve-password))]
       ["/update" {:post update-db}]]]]]])

(def service {:env :prod
              ::bootstrap/routes routes
              ::bootstrap/resource-path "/public"
              ::bootstrap/allowed-origins ["http://shibeshibe.com"]
              ::bootstrap/type :jetty
              ::bootstrap/port 8080})
