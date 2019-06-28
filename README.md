# Netty Echo TLS Reload

Sometimes you want to reload TLS certificate from the filesystem for a Netty-based server, without having to bounce the server. May be your certificates get renewed every now or then. Or, may be you are using a certificate manager that sends your server the temporary certificate for the time being and sends the permanent one in a few minutes. 

There are multiple ways of getting a Netty-based server to reload TLS certificate from the filesystem, or rather dealing with refresh of certificates. The strategies include: 

1. Shut down the existing channels on the server, based on the event that the TLS certificate changes. Hopefully the client retries and your exception handling is robust enough to make the experience seamless. 

2. Write your own `SslContext` class, and have it renew itself whenever it is notified of the change in TLS certificate. 

3. Replace the SSL/TLS Handler from the channel pipeline on the fly, based on the event notification. 

4. Close the existing channels, and propagate the errors to the client. Let the client retry, which will result in opening of a new channel using a SSL Context built from the newer material.

5. Let existing channels continue to use old certificate. New channels use an SSL Context built from the new certificate and key. 

This repo contains an example that explores use of strategies #3 and #4. It takes the [Echo example](https://github.com/netty/netty/tree/4.1/example/src/main/java/io/netty/example/echo) from [Netty Repo](https://github.com/netty/netty/) and extends it. 

## Status: Work in Progress
