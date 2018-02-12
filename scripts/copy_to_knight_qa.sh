#!/bin/sh

scp ../server/launcher/target/server-0.32.0-SNAPSHOT.jar root@knight-qa.blynk.cc:/root
scp ../server/core/src/main/resources/create_schema.sql root@104.131.50.204:/tmp