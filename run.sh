#!/bin/bash

mvn clean package

java -jar ./target/code-challenge-1.0.0-SNAPSHOT.jar sendToPartners ./data/SaleObjects.json ./data/SaleObjects.xml
