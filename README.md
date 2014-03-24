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

## Running a Rack application from inside of trapperkeeper

Provided in this project is a trapperkeeper service called `rack-wrapper-service`.
It provides just one function called `add-rack-handler`. This function internally
delegates to the `add-context-handler` function defined in the WebserverService
protocol. To use it, you simply call that function and point it at the folder
containing the `config.ru` for your rack application.

The one caveat is that you need to bundle all of the gems that your app depends
on so that they can be included in the jar.  More info on this a bit later.

The service is defined in
`src/clojure/puppetlabs/trapperkeeper/services/rack_webserver/rack_wrapper_service.clj`

You can find an example sinatra app in `test/clojure/examples/rack/rack_example.clj` and
`test/ruby/hello-sinatra`.  To run the code:

    lein trampoline run --bootstrap-config ./test/clojure/examples/rack/bootstrap.cfg \
                        --config ./test/clojure/examples/rack/config.ini

The `rack-wrapper-service` could be useful in production and could be brought
up to production quality without too much effort.

### Using a ruby library to provide a trapperkeeper service

There is an example of using Ruby code to provide a trapperkeeper service in
`test/clojure/examples/ruby_provider/ruby_provider_example.clj`.  It includes
examples of simply executing ruby scripts, and also of instantiating classes that
are defined in ruby code and calling methods on them.  The corresponding ruby
code is in `test/ruby/service-provider`.

Note that the example ruby code has gem dependencies, and that our current
approach for resolving them is to use bundler to install the gems into
the source tree so that they can be packaged directly in the jar file.  For
more information, see the section on gems below.

### Calling a clojure function provided by a trapperkeeper service from a rack app

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
    test/clojure/examples/rack/counter/count_service_java_bridge.clj
    test/java/examples/rack/counter/CountService.java


## gems
This repo contains an example of integrating with a ruby codebase that contains a `Gemfile`.  Here's how you can make this work:

##### Step 0 (optional)
Install bundler if you don't already have it installed.  You can do this via jruby:
```
lein run -m org.jruby.Main -S gem install bundler --install-dir /tmp/bundler
```
If you are going this way, you should prepend the following to all of the `lein` commands below so it can find bundler:
```
PATH=/tmp/bundler/bin:$PATH GEM_HOME=/tmp/bundler
```

##### Step 1
Tell bundler to download all of the gems in `Gemfile` and add them to the source tree:
```
lein run -m org.jruby.Main -S bundle install --gemfile src/ruby/Gemfile --path gems
```
The example above uses `src/ruby/Gemfile` and downloads the gems to `src/ruby/gems`.  If you are not specifying a temporary bundler executable (as explained it step 0), it will use the system bundler (from `$PATH`).

##### Step 2
Tell bundler to generate a ruby script that will add the gems to the load path:
```
lein run -m org.jruby.Main -S bundle install --gemfile src/ruby/Gemfile --standalone
```

##### Step 3
Bundler outputs this script in kind of a weird location (`gems/bundler/setup.rb`), so move it into the normal source tree structure:
```
mv gems/bundler src/ruby/gems
```

##### Step 4
Execute that script in the jruby execution container that in which you want the gems to be available.  See the implementation of `:gem-test-fn` in `wrapper.clj`.

[This commit](https://github.com/KevinCorcoran/trapperkeeper-polyglot-playground/commit/39f3bbf02a7ab71a1aaf70601d0fd0051d4bc57e) contains the end result of this process - a simple `Gemfile` specifying a dependency on the `awesome-print` gem, a ruby script that uses `awesome-print`, and a trapperkeeper service that runs that executes the bunder-generated `setup.rb` script to add the depencies into the the ruby load path before executing the script which depends on them.

## Note about ruby objects
The ruby scripts used in the jruby example only returns "raw" values (strings and numbers); these cases are handled nicely by jruby and automatically converted into the appropriate java objects (`java.lang.String`, `java.lang.Integer`, etc.).  However, if the result of the ruby script is *not* a primitive type, it will come back to JVM land as a [`RubyObject`](http://jruby.org/apidocs/org/jruby/RubyObject.html) (or `RubyHash`, `RubyArray`, etc.).  In this case, I believe it will be necessary to manually convert the `RubyObject` to whatever data structure is needed in JVM-land (in the case of things like arrays and hashes, there may be automatic conversion utliites but I haven't found them yet.)
