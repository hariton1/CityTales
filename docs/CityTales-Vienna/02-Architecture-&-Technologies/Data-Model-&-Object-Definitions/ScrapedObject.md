Scraper Object Model – UML Diagram

1. Overview 

Below the structure and purpose of the main objects used in the CityTales Vienna project's backend scraping system. It includes a UML class diagram and a detailed explanation of each class and their fields. 

2. Class Descriptions 

2.1 ScrapedObject 

The central data object resulting from the scraping of external sources such as Wikidata. It represents a real-world entity such as an event, person, or place. 

Fields: 

- id (String): Unique identifier (e.g., Q8754 from Wikidata) 

- label (String): Human-readable name 

- description (String): Brief summary of the object 

- startDate (Date): Starting date of the event or existence 

- endDate (Date): Optional end date 

- coordinates (GeoPoint): Latitude and longitude of the object 

- themeTags (List<String>): Tags used to classify the object (e.g., 'diplomacy', 'war') 

- source (String): Name of the source, e.g., 'Wikidata' 

- information accuracy index (float): index of how accurate the information is 

- interest (List<WeightedTopic>): List of topics associated with this object that a user could be interested in where each topic is weighted 

- relatedObjects (List<ScrapedObject>): List of Locations/Events/People related to this 

2.2 GeoPoint 

Represents the geographical location of an object. 

Fields: 

- lat (Float): Latitude 

- lon (Float): Longitude 

2.3 StoryDTO 

A formatted version of the scraped data intended for frontend display. This object contains content ready to be shown to the user. 

Fields: 

- title (String): Title of the story 

- body (String): Full narrative content 

- sourceUrl (String): Link to the original source of information 

- linkedEntities (List<String>): Related entity identifiers 

2.4 LocationMatchDTO 

Used to represent the result of matching a user's location to a known entity in the system. 

Fields: 

- locationName (String): Name of the detected location 

- matchedEntityId (String): Identifier of the matched entity (e.g., Q8754) 

- confidenceScore (Float): Match confidence score between 0.0 and 1.0 

3. UML Diagram
![scraped_object_uml](uploads/72c72fb88939e63b138dc91ecdae37d1/scraped_object_uml.png)