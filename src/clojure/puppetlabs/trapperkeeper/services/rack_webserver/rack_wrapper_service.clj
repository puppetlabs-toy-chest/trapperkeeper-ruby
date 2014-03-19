(ns puppetlabs.trapperkeeper.services.rack-webserver.rack-wrapper-service
  (:import (javax.servlet ServletContextListener)
           (org.jruby.rack RackServletContextListener))
  (:require
    [puppetlabs.trapperkeeper.services.rack-webserver.rack-webserver-core :as core]
    [puppetlabs.trapperkeeper.core :refer [defservice]]
    [clojure.java.io :refer [resource]]))

(defprotocol RackWrapperService
  "Protocol spec for Rack Wrapper service"
  (add-rack-handler [this rack-path context-path]))

(defservice rack-wrapper-service
  "Provides a wrapper for running rack based ruby apps in the trapperkeeper's web server service"

  RackWrapperService

  [[:WebserverService add-context-handler]]

  (add-rack-handler [this rack-path context-path]
                    (let [base-path (core/get-base-path (resource rack-path))]
                      (add-context-handler base-path context-path
                                           [(reify ServletContextListener
                                              (contextInitialized [this event]
                                                (core/initialize-rack-servlet-context (.getServletContext event) base-path))
                                              (contextDestroyed [this event]))
                                            (RackServletContextListener.)]))))
