# Groovy Rule Engine

This application will update the cost of the travel based on rules associated.<BR>
e.g.) User may be senior citizen and need to add some discount on the base fare.

The system mostly doesn't need reboot for new or updating the existing business rules.

It is a spring boot application responsible for
 1. Reading the raw event from {@link com.nepu.metro.common.consumer.`Consumer` consumer},
 2. {@link com.nepu.metro.common.consumer.handler.`Converter` Enrich} it for rules execution
 3. {@link com.nepu.metro.common.consumer.handler.`EventHandler` Execute} the business rules, and update the event
 4. {@link com.nepu.metro.common.producer.`Producer` Push} back to producer for further processing or storing into db.
 
Application execution begins with `TravelEventConsumer`,<BR>
Can be started with `RuleEngineApplication` <BR>
Rules can be edited at `GroovyTravelEventHandler`.<BR>

