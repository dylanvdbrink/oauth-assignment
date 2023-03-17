# oauth-assignment

## Running
`mvn spring-boot:run` or `./mvnw spring-boot:run`

## Design considerations
1. Would normally use a OAuth library in some way but that kind of defeats the purpose of this assignment. 
2. Arguably could have used Spring Reactive Web instead of Spring Web because it should offer better performance
if a lot of clients are connecting. But because of time constraints I chose Spring Web as I've used that a lot already.
3. Didn't use Lombok as Records could mostly fill getter/setter boilerplate and for the others I didn't feel like an 
extra dependency was worth it.
4. Use intermediate ID's so that internal database IDs are not exposed.
5. Access token and ID tokens are the same in this case but actually shouldn't in a real world scenario.

## Notes
- OpenID spec URL should be "https://openid.net/specs/openid-connect-core-1_0.html" (.htm**l**)