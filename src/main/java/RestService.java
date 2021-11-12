public class RestService {

    private Integer callCount = 0;

    public String fetchSomething() throws NullPointerException {

        this.callCount++;

        System.out.print(callCount + " ");

        this.shouldFailEvery(6);
        this.shouldFailEvery(7);

        return "Success";
    }

    private void shouldFailEvery(Integer callCount) {
        if(this.callCount % callCount == 0) {
            System.out.print("Failure ");

            throw new NullPointerException();
        }
    }
}