trapperkeeper-ruby
==================

This project isn't really production-ready; it's got a few things that might
be useful and could probably be brought up to production standards without much
more work, but it at least needs some tests, docs, etc.

The intent of the project is to illustrate some possible ways to run ruby code
inside of trapperkeeper.  This includes:

* Running a ruby Rack application inside of trapperkeeper
* Using a ruby library to provide a trapperkeeper service that other services
  can consume
* Calling a clojure function provided by another service from a ruby-based
  service

== Running a Rack application from inside of trapperkeeper

Provided in this project is a trapperkeeper service called `rack-webserver-service`.
It provides the same API as the built-in trapperkeeper `webserver-service`, with
one additional function called `add-rack-handler`.  To use it, you simply call
that function and point it at the `config.ru` for your rack application.

The one caveat is that you need to bundle all of the gems that your app depends
on so that they can be included in the jar.  More info on this a bit later.

The service is defined in `src/clojure/rack_jetty/rack_jetty_service.clj`.

You can find an example sinatra app in `test/clojure/examples/rack_example.clj` and
`test/ruby/hello-sinatra`.  To run the code:

    lein trampoline run --bootstrap-config ./test/clojure/examples/bootstrap.cfg \
                        --config ./test/clojure/examples/config.ini

The `rack-webserver-service` could be useful in production and could be brought
up to production quality without too much effort.

== Using a ruby library to provide a trapperkeeper service

TODO: add Kevin's stuff

== Calling a clojure function provided by a trapperkeeper service from a rack app

This involves a bit more interop than I'm really proud of, so I'm not sure whether
it's wise to do in production... but it turns out to be possible, and not all
that difficult.  The basic steps:

* Write a Java class/interface that specifies the method signatures corresponding
  to the clojure service's functions that you wish to call
* Write a clojure trapperkeeper service that creates an instance of the java
  interface, and then makes that instance accessible as a singleton
* Grab a reference to the singleton from the Ruby code.

For a simple example of this, see the following files:

    test/ruby/sinatra-service-consumer/sinatra-consumer.rb
    test/clojure/examples/counter/count_service_java_bridge.clj
    test/java/examples/counter/CountService.java
