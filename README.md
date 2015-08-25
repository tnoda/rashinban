# Rashinban

Rashinban is a Cloujure library designed to work with R. It is built on the [Rserve](https://rforge.net/Rserve/) library, and provides R functions by dynamically creating proxy functions into a Clojure namespace. It also converts automatically data types between Clojure and R when you invoke the proxy functions.


![R-Rserve-Clojure](https://tnoda.github.io/rashinban/figs/rashinban-configuration.png)


By using Rashinban, you will be easily able to use plentiful R functions from Clojure.


## Requirements

+ Mac OS X 10.10
  - Rashinban has been developed on Max OS X 10.10.x. It should be able to run on other Unix-like operating systems.
+ [R](https://www.r-project.org/) 3.2
+ [Rserve](https://rforge.net/Rserve/) 1.8

Before using Rashinban, you have to install R and Rserve into your local host settings.


## Leiningen dependency information

[![Clojars Project](http://clojars.org/org.clojars.tnoda/rashinban/latest-version.svg)](http://clojars.org/org.clojars.tnoda/rashinban)



## Usage

**1. Start your Rserve server**

Rashinban comes with a shell script to start a dedicated Rserve server. The shell command will then just be:

    $ ./start-server.sh

**2. Initialize the Rshinban library**

`tnoda.rashinban/init` is the entry point to start Rashinban. It establishes a connection to the Rserve server and searches all available R functions to create Clojure proxy functions. The only thing you have to do is:

    user> (require 'tnoda.rashinban :as r)
    nil
    
    user> (r/init)
    nil

**3. Use R functions by invoking thier Clojure proxy functions**

`tnoda.rashinban/init` creates proxy functions for R functions into the `tnoda.rashinban` namespace. You can call R functions by prefixig `r/` to their names. For example:

    user> (r/sum (range 10))
    [45.0]
    
    user> (r/var (range 10))
    [9.166666666666666]


## Example

![screenshot](https://tnoda.github.io/rashinban/figs/rashinban-example.jpg)


## Data type conversion

Clojure Type       | R Type
-------------------|-----------
nil                | NULL
symbol             | symbol
boolean            | logical vectors
string             | character vectors
seqable of doubles | numeric vector
seqable of longs   | numeric vector


## TODOs

https://github.com/tnoda/rashinban/wiki/Rashinban2Features

## License

Copyright © 2015 Takahiro Noda

Distributed under the Apache License version 2.0.
