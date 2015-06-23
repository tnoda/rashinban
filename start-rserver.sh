#!/bin/sh

R -e 'library(Rserve)' -e 'Rserve(args="--vanilla")'
