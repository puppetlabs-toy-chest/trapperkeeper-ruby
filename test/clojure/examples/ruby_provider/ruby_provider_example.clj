(ns examples.ruby-provider.ruby-provider-example
  (:import  (org.jruby.embed ScriptingContainer PathType LocalVariableBehavior))
  (:require [puppetlabs.trapperkeeper.core :refer [defservice]]))

(def ruby-test-script-src-path "test/ruby/service-provider/")

(defn ruby-script-path [script-name]
  (str ruby-test-script-src-path script-name))

(defn run-ruby-script
  ([file]
   (run-ruby-script (ScriptingContainer.) file))
  ([container file]
   (.runScriptlet container (PathType/RELATIVE) file)))
;; May be more efficient to re-use the ScriptingContainer

(defprotocol RubyService
  (msg-fn [this])
  (meaning-of-life-fn [this])
  (google-fn [this])
  (times-fn [this x y])
  (fibonacci-fn [this n])
  (gem-test-fn [this]))

(defservice ruby-service
  RubyService

  []

  ;; Service functions implemented via ruby scripts.
  ;; See the ruby source files for more information on what they
  ;; are demonstrating.
  (msg-fn [this]
          (run-ruby-script (ruby-script-path "msg.rb")))

  (meaning-of-life-fn [this]
                      (run-ruby-script (ruby-script-path "meaning-of-life.rb")))

  (google-fn [this]
             (run-ruby-script (ruby-script-path "google.rb")))

  (times-fn [this x y]
            (let [container (ScriptingContainer. LocalVariableBehavior/PERSISTENT)

                  ;; First, evaluate the ruby source file
                  _ (.runScriptlet container (PathType/RELATIVE) (ruby-script-path "my_class.rb"))

                  ;; Instatiate MyClass
                  instance (.runScriptlet container (str "MyClass.new(" x ")"))]

              ;; Call the `times` method on our instance of
              ;; `MyClass`, passing in `y` as an argument
              (.callMethod container instance "times" (into-array [y]))))

  (fibonacci-fn [this n]
                (let [container (ScriptingContainer.)
                      rubyObject (.runScriptlet container (PathType/RELATIVE) (ruby-script-path "fibonacci.rb"))]

                  ;; Call our ruby code to compute the nth fibonacci number
                  (.callMethod container rubyObject "fibonacci" (into-array [n]))))

  ;; Example of using a Gemfile to specify dependencies (see README)
  (gem-test-fn [this]
               (let [container (ScriptingContainer. LocalVariableBehavior/PERSISTENT)
                     ;; requires all the gems defined in gemfile
                     _ (run-ruby-script (ruby-script-path "gems/bundler/setup.rb"))]
                 (run-ruby-script (ruby-script-path "gem_user.rb")))))
