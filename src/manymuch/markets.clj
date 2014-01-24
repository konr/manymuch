(ns manymuch.markets
  (:require [org.httpkit.client :as http]
            [swiss-arrows.core  :refer :all]
            [cheshire.core      :as json]))

(def markets
  [{:name :cryptsy
    :url "http://pubapi.cryptsy.com/api.php?method=marketdatav2"}])


(defn get-json [url]
  (let [res @(http/get url)]
    (assert (= 200 (:status res)))
    (-> res :body (json/parse-string keyword))))


(defmulti last-trades :name)

(defmethod last-trades :cryptsy [{url :url}]
  (for [[_ market] (get-in (get-json url) [:return :markets])]
    {:buy (:primarycode market)
     :with (:secondarycode market)
     :for (Double. (:lasttradeprice market))
     :broker :cryptsy}))

