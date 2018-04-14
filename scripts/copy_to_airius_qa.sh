#!/bin/sh

scp ../server/launcher/target/airiusfans-0.35.0-SNAPSHOT.jar root@airiusfans-qa.blynk.cc:/root
scp ../server/core/src/main/resources/create_schema.sql root@104.131.50.204:/tmp