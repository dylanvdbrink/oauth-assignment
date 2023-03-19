# oauth-assignment

## Running
`mvn spring-boot:run` or `./mvnw spring-boot:run`

## Design considerations
1. Would normally use an OAuth library in some way but that kind of defeats the purpose of this assignment. 
2. Arguably could have used Spring Reactive Web instead of Spring Web because it should offer better performance
if a lot of clients are connecting. But because of time constraints I chose Spring Web as I've used that a lot already 
and could deliver faster.
3. Didn't use Lombok as Records could mostly fill getter/setter boilerplate and for the others I didn't feel like an 
extra dependency was worth it.
4. Use intermediate ID's so that internal database IDs are not exposed.
5. Used constructor injection instead of field injection so that each Spring Bean _can_ be unit tested without the 
Spring Framework which will result in faster and arguably better unit tests.
6. Used JUnit and the Spring Boot test framework as I didn't have the time to figure out why my Spock tests weren't 
working.
7. Return the same JWT for the access and id tokens. In a real world scenarion this 

## Notes
- OpenID spec URL should be "https://openid.net/specs/openid-connect-core-1_0.html" (.htm**l**)
- Curious about the choice for the Spock framework as the test framework of choice. As I found the Groovy syntax
to be quite different from the application source code and the switching of (altough similar) languages might be 
inefficient.
- Wasn't sure wat was meant by "Implement authentication by the server and require a session before responding with 
the final redirect containing tokens."