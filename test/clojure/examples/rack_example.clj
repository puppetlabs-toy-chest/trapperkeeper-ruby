(ns examples.rack-example
  (:require [puppetlabs.trapperkeeper.core :refer [defservice]]
            [clojure.tools.logging :as log]))

(defservice hello-sinatra-service
  "This is a very basic 'hello world' web service, written in sinatra."
  {:depends [[:rack-webserver-service add-rack-handler]]
   :provides [shutdown]}
  (log/info "Rack hello sinatra webservice starting up!")
  (add-rack-handler "./test/ruby/hello-sinatra" "/hello-sinatra")
  {:shutdown (fn [] (log/info "Rack hello sinatra webservice shutting down!"))})

(defservice sinatra-consumer-service
  "This is another 'hello world' web service, written in sinatra.  However, it
  also uses a very simple java bridge to enable it to call functions that are
  provided by a clojure service.  For more info have a look at the sinatra ruby
  code for this service, the implementation of count-service-java-bridge,
  and the clojure count-service."
  {:depends [[:rack-webserver-service add-rack-handler]
             ;; we don't need any functions from the :count-service-java-bridge,
             ;; but we need to make sure it's loaded before this service.
             [:count-service-java-bridge]]
   :provides [shutdown]}
  (log/info "Rack sinatra consumer webservice starting up!")
  (add-rack-handler "./test/ruby/sinatra-service-consumer" "/sinatra-consumer")
  {:shutdown (fn [] (log/info "Rack sinatra consumer webservice shutting down!"))})
