;; NOTE: this code is an adaptation of ring-jetty-handler.
;;  It adds some SSL utility functions, and
;;  provides the ability to dynamically register ring handlers.

(ns rack-jetty.rack-jetty-core
  "Adapter for the Rack Jetty webserver."
  (:import (org.eclipse.jetty.servlet ServletContextHandler ServletHolder
                                      DefaultServlet)
           (org.jruby.rack RackFilter RackServletContextListener)
           (org.eclipse.jetty.util.resource Resource))
  (:require [clojure.java.io :refer [file]]))

(defn add-rack-handler
  [webserver rack-path context-path]
  (let [handlers  (:handlers webserver)
        h         (ServletContextHandler. nil context-path ServletContextHandler/NO_SESSIONS)]
    (doto h
      (.addFilter RackFilter "/*" 0)
      (.setBaseResource (Resource/newResource (file rack-path)))
      (.addEventListener (RackServletContextListener.))

      ;; TODO: some of this initialization logic would need improvement before
      ;; this code should be used in production.  See comments inline in the
      ;; following stanza.

      ;; the "rackup" initialization parameter takes a string of ruby code
      ;; as its value.  What we basically want to do is to just slurp in
      ;; the original config.ru that is provided by the user, but we need to
      ;; inject a few (hacky) lines of code at the beginning that do some
      ;; required initialization of the ruby environment in order to make things
      ;; work.
      (.setInitParameter
        "rackup"
        ;; first we add the rack-path to ruby's LOAD_PATH so that the application
        ;; code can be found.
        (str "$LOAD_PATH.unshift(\"" rack-path "\")\n"
             ;; then we do a 'require' on the setup script that is generated
             ;; by `bundler --standalone`; this has the effect of making sure
             ;; that all of the gems that we installed into the source tree
             ;; via bundler are accessible to the application.
             "require '" rack-path "/bundle/setup.rb'\n"
             ;; and finally, we slurp in the "real" config.ru.
             (slurp (file rack-path "config.ru"))))
      ;; This should probably be configurable; see jruby-rack documentation
      (.setInitParameter "jruby.max.runtimes" "1")

      ;; This is a big one.  By default, the jruby runtime will have access
      ;; to all of the system environment variables, including the ones that
      ;; affect ruby.  The most important of these are the ones relating to the
      ;; gem path: e.g. GEM_PATH, GEM_HOME, potentially others.  RUBYLIB is
      ;; probably a concern as well.  We really, really *don't* want the app to
      ;; try to load gems and code from outside of the JVM environment, so we
      ;; need to manually override these environment variablse if they exist.
      ;;
      ;; The `jruby.runtime.env` setting can be used to do just this.  You can
      ;; give it a map containing your desired environment variables, which will
      ;; prevent the jruby runtime from seeing any of the "real" environment
      ;; variables.  If you give it an empty string or empty map, then the jruby
      ;; runtime will not see any environment variables at all.
      ;;
      ;; Currently we're taking this heavy-handed approach of hiding *all*
      ;; environment variables.  It might be a better approach to simply take
      ;; a copy of the "real" environment variables map, and clear out the
      ;; ones we care about (GEM_PATH, etc.).  But for now, rather than trying
      ;; figure out exactly what the list should be, we're just blowing them
      ;; all away.  This decision might need to be revisited.
      (.setInitParameter "jruby.runtime.env" "")
      ;; This one allows you to specify a relative path of additional gems,
      ;; but we don't need it because we're using the bundler/setup.rb approach
      ;; above.
      ;(.setInitParameter "gem.path" "gems/jruby/1.9/gems")


      (.addServlet (ServletHolder. (DefaultServlet.)) "/"))

    (.addHandler handlers h)
    (.start h)
    h))