package org.example.main.completable.advanced;

public class AdvancedMain {

    /*
    TODO:
    1. Add locks controller --DONE
    2. Implement status handling --Partially done (TODO: status of specific values)
    3. Fix critical error extra info
    4. Fix memoization map
    5. Fix exits
    6. Fix console output -- use \r?
     */
    public static void main(String[] args) {
        AdvancedController controller = new AdvancedController();
        controller.start();
    }

}
