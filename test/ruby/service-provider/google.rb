# Example of 'require'-ing an external library
# Uses net/http to make an HTTP request to google.com and returns the response status

require 'net/http'
require 'uri'

uri = URI("http://www.google.com")
res = Net::HTTP.get_response(uri)
res.code.to_i
