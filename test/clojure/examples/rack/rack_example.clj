(ns examples.rack.rack-example
  (:require [puppetlabs.trapperkeeper.core :refer [defservice]]
            [clojure.tools.logging :as log]))

(defservice hello-sinatra-service
  "This is a very basic 'hello world' web service, written in sinatra."
  [[:RackWrapperService add-rack-handler]]
  (init [this context]
        (log/info "Rack hello sinatra webservice starting up!")
        (add-rack-handler "hello-sinatra" "/hello-sinatra")
        context)

  (stop [this context]
        (log/info "Rack hello sinatra webservice shutting down!")
        context))

(defservice sinatra-consumer-service
  "This is another 'hello world' web service, written in sinatra.  However, it
  also uses a very simple java bridge to enable it to call functions that are
  provided by a clojure service.  For more info have a look at the sinatra ruby
  code for this service, the implementation of count-service-java-bridge,
  and the clojure count-service."
  [[:RackWrapperService add-rack-handler]]
  (init [this context]
        (log/info "Rack sinatra consumer webservice starting up!")
        (add-rack-handler "sinatra-service-consumer" "/sinatra-consumer")
        context)

  (stop [this context]
        (log/info "Rack sinatra consumer webservice shutting down!")
        context))
