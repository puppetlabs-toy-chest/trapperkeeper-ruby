(ns examples.ruby-provider.ruby-provider-test
  (:require [clojure.test :refer :all]
            [puppetlabs.trapperkeeper.core :as trapperkeeper]
            [puppetlabs.trapperkeeper.services :as tk-services]))

(deftest test-ruby
  (testing "TK service implemented in ruby"
    (let [app (trapperkeeper/bootstrap
                {:bootstrap-config "./test/clojure/examples/ruby_provider/bootstrap.cfg"
                 :config "./test/clojure/examples/ruby_provider/config.ini"})
          msg-fn (tk-services/get-service-fn app :ruby-service :msg-fn)
          google-fn (tk-services/get-service-fn app :ruby-service :google-fn)
          fibonacci-fn (tk-services/get-service-fn app :ruby-service :fibonacci-fn)
          times-fn (tk-services/get-service-fn app :ruby-service :times-fn)
          gem-test-fn (tk-services/get-service-fn app :ruby-service :gem-test-fn)
          meaning-of-life-fn (tk-services/get-service-fn app :ruby-service :meaning-of-life-fn)]
      (is (= (msg-fn) "Hello from ruby"))
      (is (= (google-fn) 200))
      (is (= (times-fn 23 3) 69))
      (is (= (fibonacci-fn 5) 8))
      (is (= (gem-test-fn) "Gems loaded"))
      (is (= (meaning-of-life-fn) 42)))))
