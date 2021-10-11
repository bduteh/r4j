public class BackendService {

    private Integer callCount = 0;

    public String doSomething() throws NullPointerException {

        this.callCount++;

        System.out.print(callCount + " ");

        this.shouldFailEvery(6);

        return "Success";
    }

    private void shouldFailEvery(Integer callCount) {
        if(this.callCount % callCount == 0)
            throw new NullPointerException();
    }
}