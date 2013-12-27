(defproject puppetlabs/trapperkeeper-ruby "0.1.0-SNAPSHOT"
  :description "We are trapperkeeper.  We are one."
  ;; Abort when version ranges or version conflicts are detected in
  ;; dependencies. Also supports :warn to simply emit warnings.
  ;; requires lein 2.2.0+.
  :pedantic? :abort
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [puppetlabs/kitchensink "0.3.1-SNAPSHOT" :exclusions [joda-time]]
                 [org.eclipse.jetty/jetty-server "7.6.1.v20120215"]
                 [org.eclipse.jetty/jetty-servlet "7.6.1.v20120215"]
                 [org.clojure/tools.logging "0.2.6"]
                 [prismatic/plumbing "0.1.0"]
                 [org.jruby/jruby "1.7.9" :exclusions [com.github.jnr/jffi com.github.jnr/jnr-x86asm org.jruby.joni/joni]]
                 [org.jruby.joni/joni "2.1.1" :exclusions [org.jruby.jcodings/jcodings]]
                 [org.jruby.rack/jruby-rack "1.1.13.3" :exclusions [org.jruby/jruby-complete]]
                 [puppetlabs/trapperkeeper "0.1.0-SNAPSHOT"]]

  :repositories [["releases" "http://nexus.delivery.puppetlabs.net/content/repositories/releases/"]
                 ["snapshots" "http://nexus.delivery.puppetlabs.net/content/repositories/snapshots/"]]

  :source-paths ["src/clojure"]
  :test-paths   ["test/clojure"]

  :profiles {:dev {:test-paths ["test-resources"]
                   :java-source-paths ["test/java"]}
             :test {:dependencies [[puppetlabs/kitchensink "0.3.1-SNAPSHOT" :classifier "test"]]}}

  :main puppetlabs.trapperkeeper.main
  )
