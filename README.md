# Groovy Rule Engine

Spring boot application responsible for
 1. Reading the raw event from {@link com.nepu.metro.common.consumer.Consumer consumer},
 2. {@link com.nepu.metro.common.consumer.handler.Converter Enrich} it for rules execution
 3. {@link com.nepu.metro.common.consumer.handler.EventHandler Execute} the business rules, and update the event
 4. {@link com.nepu.metro.common.producer.Producer Push} back to producer for further processing or storing into db.
 
Application execution begins with TravelEventConsumer,
Can be started with RuleEngineApplication.
Rules can be edited at GroovyTravelEventHandler.
