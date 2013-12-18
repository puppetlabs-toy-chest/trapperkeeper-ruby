require 'sinatra/base'
# here we import the java class that provides a bridge to the clojure count service
java_import Java::examples.counter.CountService

class SinatraConsumer < Sinatra::Base
  def initialize()
    # and here we grab a singleton instance of the class
    @count_service = CountService.getInstance
  end

  get '/' do
    # and here, we call its one simple method!
    "Sinatra Trapperkeeper Service Consumer! count: '#{@count_service.incAndGet}'"
  end

  run! if app_file == $0
end
