(ns shibeshibe.service-test
  (:require [midje.sweet :refer :all]
            [io.pedestal.service.test :refer :all]
            [io.pedestal.service.http :as bootstrap]
            [shibeshibe.datomic.core :as core]
            [shibeshibe.components :as com]
            [shibeshibe.web.service :as service]))



(defn GET [world url]
  (response-for (:service world) :get url))

(defn POST [world url payload]
  (response-for (:service world) :post url :body payload))

(defn components []
  {:db (com/bootstrap! (core/random-uri))
   :service (::bootstrap/service-fn (bootstrap/create-servlet service/service))})


(def world (components))



(facts "on /update"
       (fact "it adds new facts to the database"
             ))


(future-facts "on the interceptors")

(future-facts "on /convert"

              )

