(ns dev
  (:require [shibeshibe.service :as service]
            [shibeshibe.server :as server]
            [shibeshibe.core :as core]
            [io.pedestal.service.http :as bootstrap]
            [io.pedestal.service-tools.dev :as dev-tools :refer [watch tools-help]]))



(defn start []
  (core/connect)
  (server/run-dev))

;; TODO Figure out those below

#_(def service (dev-tools/init (merge service/service
                                    ;; reload routes on every request
                                    {::bootstrap/routes #(deref #'service/routes)})))


#_(defn start
  "Start a development web server. Default port is 8080.

  You must call init prior to calling start."
  [& [opts]]
  (server/create-server (merge service opts))
  (bootstrap/start server/server-instance)
  :ok)

#_(defn stop
  "Stop the current web server."
  []
  (bootstrap/stop server/server-instance)
  :ok)

#_(defn restart
  "Stop, then start the current web server."
  []
  (stop)
  (start))

#_(defn -main
  "The entry-point for 'lein run-dev'. Starts a web server and watches the projects files for any changes."
  [& args]
  (start)
  (watch))
