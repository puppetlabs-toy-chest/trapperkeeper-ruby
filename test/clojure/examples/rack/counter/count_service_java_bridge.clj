(ns examples.rack.counter.count-service-java-bridge
  (:import [examples.counter CountService])
  (:require [puppetlabs.trapperkeeper.core :refer [defservice]]
            [clojure.tools.logging :as log]))

(defservice count-service-java-bridge
  "This service provides a simple java bridge for the clojure count-service.  It
  accomplishes this via an abstract Java class (test/java/examples/counter/CountService.java)
  which provides a Java signature for the clojure function.  The Java class also
  serves as a singleton holder so that we can store an instance of the service,
  and a getter that allows us to retrieve the singleton instance from other code
  (such as the JRuby sinatra-consumer web app."
  {:depends [[:count-service inc-and-get]]
   :provides []}
  (log/info "Count service java bridge initializing.")
  ;; Create an instance of the java CountService class
  (let [cs (proxy [CountService] []
              ;; provide an implementation of the abstract method, which simply
              ;; wraps the clojure function that was injected by trapperkeeper
              (incAndGet [] (inc-and-get)))]
    ;; store the singleton instance so that it can be retrieved from ruby.
    (CountService/setInstance cs))
  {})