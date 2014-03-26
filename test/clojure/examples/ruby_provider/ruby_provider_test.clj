(ns examples.ruby-provider.ruby-provider-test
  (:require [clojure.test :refer :all]
            [puppetlabs.trapperkeeper.app :refer [get-service]]
            [puppetlabs.trapperkeeper.testutils.bootstrap
               :refer [with-app-with-cli-data]]
            [examples.ruby-provider.ruby-provider-example :refer :all]))

(deftest test-ruby
  (testing "TK service implemented in ruby"
    (with-app-with-cli-data app
      [ruby-service]
      {:config "./test/clojure/examples/ruby_provider/config.ini"}
      (let [s                  (get-service app :RubyService)
            msg-fn             (partial msg-fn s)
            meaning-of-life-fn (partial meaning-of-life-fn s)
            google-fn          (partial google-fn s)
            times-fn           (partial times-fn s)
            fibonacci-fn       (partial fibonacci-fn s)
            gem-test-fn        (partial gem-test-fn s)]
        (is (= (msg-fn) "Hello from ruby"))
        (is (= (meaning-of-life-fn) 42))
        ;; google.com can redirect to national google sites based on the IP address of the client
        ;; so we need to be prepared for the HTTP redirect status
        (is (contains? #{200 302} (google-fn)))
        (is (= (times-fn 23 3) 69))
        (is (= (fibonacci-fn 5) 8))
        (is (= (gem-test-fn) "Gems loaded"))))))
