(ns examples.rack.counter.clj-count-service
  (:require [puppetlabs.trapperkeeper.core :refer [defservice]]
            [clojure.tools.logging :as log]))

(defprotocol CountService
  "Protocol spec for Count service"
  (inc-and-get [this]))

(defservice count-service
  "This is a simple counter service.  The counter starts at zero, and the service
  provides one function, `inc-and-get`, which increments the counter and returns
  the new value"

  CountService

  []

  (init [this context]
        (log/info "############## Counter service starting up.")
        (assoc context :counter (atom 0)))

  (inc-and-get [this]
               (let [counter ((service-context this) :counter)]
                 (swap! counter inc))))
