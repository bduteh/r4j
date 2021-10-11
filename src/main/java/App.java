import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.vavr.control.Try;
import java.time.Duration;
import java.util.function.Supplier;

public class App {
    public static void main(String[] args) throws InterruptedException {

        BackendService backendService = new BackendService();

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
            .slidingWindowSize(2)
            .failureRateThreshold(50)
            .recordExceptions(NullPointerException.class)
            .waitDurationInOpenState(Duration.ofMillis(100))
            .permittedNumberOfCallsInHalfOpenState(4)
            .build();

        CircuitBreakerRegistry circuitBreakerRegistry =
            CircuitBreakerRegistry.of(circuitBreakerConfig);

        CircuitBreaker circuitBreaker = circuitBreakerRegistry
            .circuitBreaker("my-circuit-breaker");

        Supplier<String> decoratedSupplier = CircuitBreaker
            .decorateSupplier(circuitBreaker, backendService::doSomething);

        for (int i = 0; i < 200; i++) {

            String result = Try.ofSupplier(decoratedSupplier)
                .recover(throwable -> "Recovery").get();

            System.out.println(circuitBreaker.getState() + " " + result);
            Thread.sleep(10);
        }
    }
}