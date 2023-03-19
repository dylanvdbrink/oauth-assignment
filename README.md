# oauth-assignment

## Running

### Docker
`docker run -d -p 10000:8080 $(docker build -q .)` will expose the service locally on port 10000

### Maven
`mvn spring-boot:run` or `./mvnw spring-boot:run` will expose the service locally on port 8080

### Tests
`mvn clean test`

## Usage
1. Using the following url in a browser/client: `http://localhost:10000/authorize?response_type=id%20token
&client_id=00000000-0000-0000-0000-000000000000&redirect_uri=https%3A%2F%2Fvodafoneziggo.nl%2F&scope=openid
&state=asd&nonce=asd`
2. Retrieve the access_token and id_token from the redirected URL
3. Call `http://localhost:8080/test/ping` with the access token prefixed "Bearer" in the Authorization header:
`Authorization: Bearer ey...` should return a succesful response
4. The access_token and id_token can be inserted into any JWT reader to see the contents (e.g. https://jwt.io)

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
7. Return the same JWT for the access and id tokens. In a real world scenarion this.

## Notes
- OpenID spec URL should be "https://openid.net/specs/openid-connect-core-1_0.html" (.htm**l**)
- Curious about the choice for the Spock framework as the test framework of choice. As I found the Groovy syntax
to be quite different from the application source code and the switching of (altough similar) languages might be 
inefficient.
- Wasn't sure wat was meant by "Implement authentication by the server and require a session before responding with 
the final redirect containing tokens."
- There is a bug where if you request a protected endpoint without a token, it does "block" it but still returns a 200. 
Couldn't figure out why. 