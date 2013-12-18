(ns examples.rack.counter.clj-count-service
  (:require [puppetlabs.trapperkeeper.core :refer [defservice]]
            [clojure.tools.logging :as log]))

(defservice count-service
  "This is a simple counter service.  The counter starts at zero, and the service
  provides one function, `inc-and-get`, which increments the counter and returns
  the new value"
  {:depends  []
   :provides [inc-and-get]}
  (log/info "############## Counter service starting up.")
  (let [counter (atom 0)]
    {:inc-and-get (fn [] (swap! counter inc))}))
