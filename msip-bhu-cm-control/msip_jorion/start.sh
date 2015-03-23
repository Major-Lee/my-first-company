#!/bin/bash


java -cp bin/:lib/activemq-client-5.11.1.jar:lib/mina-integration-jmx-2.0.9.jar:lib/slf4j-log4j12-1.7.10.jar:lib/geronimo-j2ee-management_1.1_spec-1.0.1.jar:lib/log4j-1.2.17.jar:lib/mina-statemachine-2.0.9.jar:lib/spring-2.5.6.SEC03.jar:lib/geronimo-jms_1.1_spec-1.1.1.jar:lib/mina-core-2.0.9.jar:lib/slf4j-api-1.7.10.jar:lib/stringtree-json-2.0.5.jar com.bhu.jorion.JOrion > stdout.log 2>&1 &

