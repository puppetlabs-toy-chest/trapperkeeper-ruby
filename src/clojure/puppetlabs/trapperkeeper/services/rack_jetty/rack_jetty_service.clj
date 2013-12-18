(ns puppetlabs.trapperkeeper.services.rack-jetty.rack-jetty-service
  (:require
    [puppetlabs.trapperkeeper.services.rack-jetty.rack-jetty-core :as core]
    [puppetlabs.trapperkeeper.services.jetty.jetty-core :as jetty-core]
    [puppetlabs.trapperkeeper.core :refer [defservice]]))

(defservice rack-webserver-service
  "Provides a Jetty web server as a service"
  {:depends [[:config-service get-in-config]]
   :provides [add-ring-handler add-rack-handler join shutdown]}
  (let [config    (or (get-in-config [:webserver])
                      ;; Here for backward compatibility with existing projects
                      (get-in-config [:jetty])
                      {})
        webserver (jetty-core/start-webserver config)]
    {:add-ring-handler  (partial jetty-core/add-ring-handler webserver)
     :add-rack-handler  (partial core/add-rack-handler webserver)
     :join              (partial jetty-core/join webserver)
     :shutdown          (partial jetty-core/shutdown webserver)}))