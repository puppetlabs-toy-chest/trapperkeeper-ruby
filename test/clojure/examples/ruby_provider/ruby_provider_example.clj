(ns examples.ruby-provider.ruby-provider-example
  (:import  (org.jruby.embed ScriptingContainer PathType LocalVariableBehavior))
  (:require [puppetlabs.trapperkeeper.core :refer [defservice]]))

(defn run-ruby-script
  ([file]
   (run-ruby-script (new ScriptingContainer) file))
  ([container file]
   (.runScriptlet container (PathType/RELATIVE) file)))
;; May be more efficient to re-use the ScriptingContainer

(defservice ruby-service
  {:depends  []
   :provides [msg-fn meaning-of-life-fn google-fn fibonacci-fn times-fn]}

  ;; Service functions implemented via ruby scripts.
  ;; See the ruby source files for more information on what they
  ;; are demonstrating.
  {
    :meaning-of-life-fn
    (fn []
      (run-ruby-script "src/ruby/meaning-of-life.rb"))

    :msg-fn
    (fn []
      (run-ruby-script "src/ruby/msg.rb"))

    :google-fn
    (fn []
      (run-ruby-script "src/ruby/google.rb"))

    ;; Example of instantiating a ruby class and calling a method on it
    :times-fn
    (fn [x y]
      ;; Note that we have to use LocalVariableBehavior/PERSISTENT
      ;; This makes the class and instance persist across operations
      ;; on the ScriptingContainer
      (let [container (ScriptingContainer. LocalVariableBehavior/PERSISTENT)

            ;; First, evaluate the ruby source file
            _ (.runScriptlet container (PathType/RELATIVE) "src/ruby/my_class.rb")

            ;; Instatiate MyClass
            instance (.runScriptlet container (str "MyClass.new(" x ")"))]

        ;; Call the `times` method on our instance of
        ;; `MyClass`, passing in `y` as an argument
        (.callMethod container instance "times" (into-array [y]))))

    :fibonacci-fn
    (fn [n]
      (let [container (new ScriptingContainer)
            rubyObject (.runScriptlet container (PathType/RELATIVE) "src/ruby/fibonacci.rb")]

        ;; Call our ruby code to compute the nth fibonacci number
        (.callMethod container rubyObject "fibonacci" (into-array [n]))))

    ;; Example of using a Gemfile to specify dependencies (see README)
    :gem-test-fn
    (fn []
      (let [container (ScriptingContainer. LocalVariableBehavior/PERSISTENT)
            ;; requires all the gems defined in gemfile
            _ (run-ruby-script "src/ruby/gems/bundler/setup.rb")]
        (run-ruby-script "src/ruby/gem_user.rb")))})