(ns shibeshibe.service-test
  (:require [midje.sweet :refer :all]
            [io.pedestal.service.test :refer :all]
            [io.pedestal.service.http :as bootstrap]
            [shibeshibe.web.service :as service]))

(def service
  (::bootstrap/service-fn (bootstrap/create-servlet service/service)))


(defn GET [url]
  (response-for service :get url)) 

(defn POST [url payload]
  (response-for service :post url :body payload)) 

(future-facts "on the interceptors")

(future-facts "on /convert"

              )

(future-facts "on /update")
