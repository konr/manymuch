(ns shibeshibe.service-test
  (:require [midje.sweet :refer :all]
            [io.pedestal.service.test :refer :all]
            [io.pedestal.service.http :as bootstrap]
            [shibeshibe.datomic.core :as core]
            [shibeshibe.components :as com]
            [shibeshibe.controller :as c]
            [shibeshibe.web.service :as service]))



(defn GET [world url]
  (response-for (:service world) :get url))

(defn POST [world url payload & [headers]]
  (response-for (:service world) :post url :body payload :headers (or headers {})))

(defn components []
  {:db      (com/bootstrap! (core/random-uri))
   :service (::bootstrap/service-fn (bootstrap/create-servlet service/service))})

(defn gather-troops! []
  (alter-var-root #'com/system (constantly (components))))


(gather-troops!)

(with-redefs [service/retrieve-password (constantly "shibeshibe")]

  (facts "on /admin/update"
         (fact "it adds new facts to the database"

               (POST com/system "/api/admin/update" "" {"authorization" "veryshibe"})
               => (contains {:status 200})

               )


         ))


(future-facts "on the interceptors")

(future-facts "on /convert"

              )
