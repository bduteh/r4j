import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import java.time.Duration;
import java.util.function.Supplier;

public class App { 
    public static void main(String[] args) throws InterruptedException {

        RestService restService = new RestService();

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
            .slidingWindowSize(3)
            .failureRateThreshold(50)
            .recordExceptions(NullPointerException.class)
            .waitDurationInOpenState(Duration.ofMillis(100))
            .permittedNumberOfCallsInHalfOpenState(4)
            .build();

        CircuitBreakerRegistry circuitBreakerRegistry =
            CircuitBreakerRegistry.of(circuitBreakerConfig);

        CircuitBreaker circuitBreaker = circuitBreakerRegistry
            .circuitBreaker("rest-service");

        Supplier<String> decoratedSupplier = CircuitBreaker
            .decorateSupplier(circuitBreaker, restService::fetchSomething);

        for (int i = 0; i < 100; i++) {
            String result = Try.ofSupplier(decoratedSupplier)
                .recover(throwable -> recovery()).get();

            System.out.println(result + " " + circuitBreaker.getState());

            Thread.sleep(10);
        }
    }

    private static String recovery() {
        return "Recovering";
    }
}