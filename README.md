# HideMe Giphy Proxy

## Background

In 2020 I applied for a job with [the Signal Foundation](https://signal.org/).
The first step in the interview process was this take-home project. They asked
me to build a privacy-preserving GIF search proxy in Java, similar to the
experimental approach described
[on the Signal blog](https://signal.org/blog/giphy-experiment/).

The key requirements were that this service cannot see the plaintext contents of
the search, while giphy cannot obtain any information about the end-user.

I was asked to move forward with the hiring process, but I withdrew my candidacy
for personal reasons.

This repository is made public under the MIT license for educational purposes.

## Summary

This service acts as a proxy layer between an HTTPS client Giphy API, with the
purpose of masking the client's IP address from Giphy. HTTPS requests to
`api.giphy.com` can be resolved through DNS or other means to connect to a
HideMe instance instead. HideMe will then open a connection to Giphy and blindly
pass all traffic between the client and Giphy, masking the origin of the
requests. The connection remains encrypted via TLS between the client and Giphy,
ensuring that HideMe cannot read the payload.

## Usage

### Starting the Server

To start the HideMe Proxy Server, navigate to the root directory, modify
`config` (optional), and start the service using Gradle.

```
$ ./gradlew run
```

##### Configuration

The default configuration proxies `api.giphy.com`, listening on port `8443` with
a maximum of `30` connections. To change any of these values, modify
`config.properties` in the root directory.

### Connecting Clients

To route traffic through HideMe, you must route the `api.giphy.com` hostname to
resolve to the HideMe server.

#### Using `curl`

When running on localhost with default settings, you can test the service with
the below `curl` command, after adding your `API_KEY`.

```
curl --resolve api.giphy.com:8443:127.0.0.1 "https://api.giphy.com:8443/v1/gifs/search?api_key=API_KEY&q=querystring&limit=5"
```

It's important that client requests to specify the host as `api.giphy.com` in
order for the client to verify the TLS certificates with Giphy and ensure a
secure connection. Someclients may verify the hostname against the HTTP request
header `Host: api.giphy.com` instead of the destination address.

If you want to route all traffic to Giphy through HideMe, you can run HideMe on
port `443` and route traffic either by adding a rule to your DNS resolver, or by
adding an entry to the `hosts` file on your machine.

### Additional notes

- `max_connections` doesn't actually limit socket connections to the server, but
  instead sets a maximum for the number of actively bridged connections. In that
  sense it limits the number of connections to _Giphy_ rather than to HideMe
  clients. HideMe will continue listening for client connections and queue them
  for processing.
- Although the source IP address will be masked for requests that are proxied
  through HideMe, HideMe does not protect Giphy from identifying the user
  through other means. It may be possible for Giphy to identify users through
  other means than the source IP address.
- By design HideMe does not log stack traces for Exceptions thrown from a Socket
  connection. This is done to avoid accidentally logging identifiable client
  information.
- Regarding unit tests: The tests are a little messy due to time constraints. I
  also discovered some restrictions with JMockIt surrounding Java Threads, so I
  haven't yet been able to test ClientConnectionBridge.java or some of the
  shutdown functionality of HideMeServer.java.
- When running in a Production environment with DNS overrides, HideMe should be
  accessible from external port 443 to ensure standard HTTPS traffic is routed.

### Future improvements

- The TLS handshake contains the requested hostname in cleartext. By reading the
  TLS handshake data, it would be possible to expand HideMe to be a generic
  proxy for any HTTPS endpoint. This comes with a tradeoff that HideMe would
  then be able to aggregate web traffic from a client IP and be able to track
  the hostnames requested by clients of the service.
- To prevent a client from accidentally sending a plaintext HTTP request, which
  would both bypass HideMe and allow the service to read request data, an
  additional ServerSocket listening on port 80 could be implemented to send an
  HTTP Redirect to any clients sending a plaintext HTTP request.
- Currently the service can only be stopped by sending an interrupt or killing
  the process. Ideally there would be a way to cleanly shutdown and optionally
  attempt to restart itself in the event of a fatal error, but hasn't been
  implemented.
- Adding monitoring for anonymized metrics, such as dropped connections,
  connection failures, as well as performance metrics such as CPU, network, and
  memory usage is necessary before running in a production environment.

## LICENSE

Copyright (c) 2020 Nik Gilmore

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
